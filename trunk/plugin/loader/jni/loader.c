#include "angdroid.h"

#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include <dlfcn.h>
#include <stdio.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "Angband", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "Angband", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "Angband", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "Angband", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "Angband", __VA_ARGS__) 

/* FIXME __android_log_write or __android_log_print ??? */
#define LOG(msg) (__android_log_write(ANDROID_LOG_DEBUG, "Angband", (msg)));

void( * angdroid_gameStart ) (JNIEnv*, jobject, jint, jobjectArray) = NULL; 
jint( * angdroid_gameQueryInt ) (JNIEnv*, jobject, jint, jobjectArray) = NULL;
jstring( * angdroid_gameQueryString ) (JNIEnv*, jobject, jint, jobjectArray) = NULL;
jint( * angdroid_gamePluginVersion ) (JNIEnv*, jobject) = NULL; 

JNIEnv* pass_env1; 
jobject pass_obj1;
jint    pass_argc;
jobject pass_argv;

static JavaVM *jvm;
static void* handle = NULL;
pthread_mutex_t muQuery = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t muGame = PTHREAD_MUTEX_INITIALIZER;

static jclass TermViewClass;
static jobject TermViewObj;
static jmethodID TermView_onExitGame;

void* run_angband(void* foo)
{
	angdroid_gameStart(pass_env1, pass_obj1, pass_argc, pass_argv);   
	return foo;
}

JNIEXPORT jint JNICALL Java_org_angdroid_angband_TermView_gamePluginVersion
(JNIEnv *env1, jobject obj1, jstring pluginPath)
{
	jint result = -1; //error

	LOGD("loader.gamePluginVersion");

	// begin synchronize
	pthread_mutex_lock (&muGame);
	pthread_mutex_lock (&muQuery);

	LOGD("loader.startGame.initializing");

	// load game plugin lib
	const char *copy_pluginPath = (*env1)->GetStringUTFChars(env1, pluginPath, 0);
	handle = dlopen(copy_pluginPath,RTLD_LOCAL | RTLD_LAZY);  
	if (!handle) {
		LOGE("dlopen failed on %s", copy_pluginPath);
		return result;
	}	
	(*env1)->ReleaseStringUTFChars(env1, pluginPath, copy_pluginPath);

	// find entry point
    angdroid_gamePluginVersion = dlsym(handle, "angdroid_gamePluginVersion");   
	if (!angdroid_gamePluginVersion) {
		LOGE("dlsym failed on angdroid_gamePluginVersion");
		return result;
	}	

	result = angdroid_gamePluginVersion(env1, obj1);

	dlclose(handle);           	 // unload angband lib

	// clear pointers
	handle = NULL;
	angdroid_gamePluginVersion = NULL;

	// end synchronize
	pthread_mutex_unlock (&muQuery);
	pthread_mutex_unlock (&muGame);

	return result;
}

JNIEXPORT void JNICALL Java_org_angdroid_angband_TermView_gameStart
(JNIEnv *env1, jobject obj1, jstring pluginPath, jint argc, jobjectArray argv)
{
	pthread_t game_thread;

	LOGD("loader.startGame.syncwait");

	// begin synchronize

	int ct = 0;
	while (pthread_mutex_trylock(&muGame)!=0) {
		sleep(100);
		if(ct++>5) {
			LOGE("failed to acquire game thread lock, bailing");
			return;
		}
	}

	pthread_mutex_lock (&muQuery);

	LOGD("loader.startGame.initializing");

	pass_env1 = env1;
	pass_obj1 = obj1;
	pass_argc = argc;
	pass_argv = argv;

	/* Init exit game callback */
	TermViewObj = obj1;
	TermViewClass = (*env1)->GetObjectClass(env1, TermViewObj);
	TermView_onExitGame = (*env1)->GetMethodID(env1, TermViewClass, "onExitGame", "()V");

	// load game plugin lib
	const char *copy_pluginPath = (*env1)->GetStringUTFChars(env1, pluginPath, 0);
	handle = dlopen(copy_pluginPath,RTLD_LOCAL | RTLD_LAZY);  
	if (!handle) {
		LOGE("dlopen failed on %s", copy_pluginPath);
		return;
	}	
	(*env1)->ReleaseStringUTFChars(env1, pluginPath, copy_pluginPath);

	// find entry point
	angdroid_gameStart = dlsym(handle, "angdroid_gameStart");   
	if (!angdroid_gameStart) {
		LOGE("dlsym failed on gameStart");
		return;
	}	

	LOGD("loader.create_thread");
	// start thread to call entry point
	pthread_create(&game_thread, NULL, run_angband, (void*) NULL);	

	// end synchronize
	pthread_mutex_unlock (&muQuery);

	LOGD("loader.waiting on game_thread");
	pthread_join(game_thread, NULL); // wait for thread to exit

	// begin synchronize
	pthread_mutex_lock (&muQuery);

	LOGD("loader.game_thread is finished");
	dlclose(handle);           	 // unload angband lib

	// clear pointers
	handle = NULL;
	angdroid_gameQueryInt = NULL;
	angdroid_gameQueryString = NULL;

	// signal game has exited
	(*env1)->CallVoidMethod(env1, TermViewObj, TermView_onExitGame);

	// end synchronize
	pthread_mutex_unlock (&muQuery);
	pthread_mutex_unlock (&muGame);
}

JNIEXPORT jstring JNICALL Java_org_angdroid_angband_TermView_gameQueryString
  (JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	return (jstring)0; // null indicates error
}

JNIEXPORT jint JNICALL Java_org_angdroid_angband_TermView_gameQueryInt
(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	jint result = -1; // -1 indicates error

	// begin synchronize
	pthread_mutex_lock (&muQuery);

	if (handle) {
		if (!angdroid_gameQueryInt)
		  	// find entry point
		  	angdroid_gameQueryInt = dlsym(handle, "angdroid_gameQueryInt");   

		if (angdroid_gameQueryInt)
			result = angdroid_gameQueryInt(env1, obj1, argc, argv);
		else
			LOGE("dlsym failed on angdroid_gameQueryInt");
	}
	else {
		LOGE("dlopen failed -- angdroid_gameQueryInt");
	}

	// end synchronize
	pthread_mutex_unlock (&muQuery);

	return result;
}
