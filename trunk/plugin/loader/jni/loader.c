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
jint( * angdroid_gameQueryRedraw ) (JNIEnv*, jobject, jint, jint, jint, jint) = NULL;
jint( * angdroid_gameQueryInt ) (JNIEnv*, jobject, jint, jobjectArray) = NULL;
jstring( * angdroid_gameQueryString ) (JNIEnv*, jobject, jint, jobjectArray) = NULL;

JNIEnv* pass_env1; 
jobject pass_obj1;
jint    pass_argc;
jobject pass_argv;

static JavaVM *jvm;
static void* handle = NULL;
pthread_mutex_t muQuery = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t muGame = PTHREAD_MUTEX_INITIALIZER;

static jclass NativeWrapperClass;
static jobject NativeWrapperObj;
static jmethodID NativeWrapper_onGameExit;

void* run_angband(void* foo)
{
	angdroid_gameStart(pass_env1, pass_obj1, pass_argc, pass_argv);   
	return foo;
}

void gameStart
(JNIEnv *env1, jobject obj1, jstring pluginPath, jint argc, jobjectArray argv)
{
	pthread_t game_thread;

	//LOGD("loader.startGame.syncwait");

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

	//LOGD("loader.startGame.initializing");

	pass_env1 = env1;
	pass_obj1 = obj1;
	pass_argc = argc;
	pass_argv = argv;

	/* Init exit game callback */
	NativeWrapperObj = obj1;
	NativeWrapperClass = (*env1)->GetObjectClass(env1, NativeWrapperObj);
	NativeWrapper_onGameExit = (*env1)->GetMethodID(env1, NativeWrapperClass, "onGameExit", "()V");

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

	//LOGD("loader.create_thread");
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
	(*env1)->CallVoidMethod(env1, NativeWrapperObj, NativeWrapper_onGameExit);

	//LOGD("loader.unlock mutexes");
	// end synchronize
	pthread_mutex_unlock (&muQuery);
	pthread_mutex_unlock (&muGame);

	LOGD("loader.return");
}

JNIEXPORT void JNICALL Java_org_angdroid_angband_NativeWrapper_gameStart
(JNIEnv *env1, jobject obj1, jstring pluginPath, jint argc, jobjectArray argv)
{
	gameStart(env1,obj1,pluginPath,argc,argv);
}

JNIEXPORT void JNICALL Java_org_angdroid_variants_NativeWrapper_gameStart
(JNIEnv *env1, jobject obj1, jstring pluginPath, jint argc, jobjectArray argv)
{
	gameStart(env1,obj1,pluginPath,argc,argv);
}

JNIEXPORT jstring JNICALL Java_org_angdroid_angband_NativeWrapper_gameQueryString
  (JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	return (jstring)0; // null indicates error
}
JNIEXPORT jstring JNICALL Java_org_angdroid_variants_NativeWrapper_gameQueryString
  (JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	return (jstring)0; // null indicates error
}

jint gameQueryInt
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

JNIEXPORT jint JNICALL Java_org_angdroid_angband_NativeWrapper_gameQueryInt
(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	return gameQueryInt(env1,obj1,argc,argv);
}
JNIEXPORT jint JNICALL Java_org_angdroid_variants_NativeWrapper_gameQueryInt
(JNIEnv *env1, jobject obj1, jint argc, jobjectArray argv)
{
	return gameQueryInt(env1,obj1,argc,argv);
}

