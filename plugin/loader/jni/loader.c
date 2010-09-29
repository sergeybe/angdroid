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

void( * startGame ) (JNIEnv*, jobject, jstring, jstring, jstring) = NULL; 
JNIEnv* pass_env1; 
jobject pass_obj1;
jstring pass_pluginPath;
jstring pass_libPath;
jstring pass_arguments;

static JavaVM *jvm;
static JNIEnv *env;

void* run_angband(void* foo)
{
	startGame(pass_env1, pass_obj1, pass_pluginPath, pass_libPath, pass_arguments);   
	return foo;
}

JNIEXPORT void JNICALL Java_org_angdroid_angband_TermView_startGame
	(JNIEnv *env1, jobject obj1, jstring pluginPath, jstring libPath, jstring arguments)
{
	void* handle;
        pthread_t game_thread;

	pass_env1 = env1;
	pass_obj1 = obj1;
	pass_pluginPath = pluginPath;
	pass_libPath = libPath;
	pass_arguments = arguments;
	LOGD("in the loader");

	/*
	if ((*env1)->GetJavaVM(env1, &jvm) < 0){
		LOGE("Error: Can't get JavaVM!");
	}

	(*jvm)->AttachCurrentThread(jvm, &env1, NULL);
	*/

	// load game plugin lib
	const char *copy_pluginPath = (*env1)->GetStringUTFChars(env1, pluginPath, 0);
	handle = dlopen(copy_pluginPath,RTLD_LOCAL | RTLD_LAZY);  
	if (!handle) {
		LOGE("dlopen failed on %s", copy_pluginPath);
		return;
	}	
	(*env1)->ReleaseStringUTFChars(env1, pluginPath, copy_pluginPath);

	// find entry point
	startGame = dlsym(handle, "Java_org_angdroid_angband_TermView_startGame");   
	if (!startGame) {
		LOGE("dlsym failed on Java_org_angdroid_angband_TermView_startGame");
		return;
	}	

	LOGD("loader.create_thread");
	// start thread to call entry point
	pthread_create(&game_thread, NULL, run_angband, (void*) NULL);	

	LOGD("loader.waiting on game_thread");
	pthread_join(game_thread, NULL); // wait for thread to exit

	LOGD("loader.game_thread is finished");
	dlclose(handle);           	 // unload angband lib

	//LOGD("loader.detatch");
	//(*jvm)->DetachCurrentThread(jvm);
}
