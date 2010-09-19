#include "angdroid.h"

#include <jni.h>
#include <android/log.h>
#include <pthread.h>
#include <dlfcn.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "Angband", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "Angband", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "Angband", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "Angband", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "Angband", __VA_ARGS__) 

/* FIXME __android_log_write or __android_log_print ??? */
#define LOG(msg) (__android_log_write(ANDROID_LOG_DEBUG, "Angband", (msg)));

void( * startGame ) (JNIEnv*, jobject, jstring) = NULL; 
JNIEnv* pass_env1; 
jobject pass_obj1;
jstring pass_filesPath;

void* run_angband(void* foo)
{
	startGame(pass_env1, pass_obj1, pass_filesPath);   
	return foo;
}

JNIEXPORT void JNICALL Java_org_angdroid_angband_TermView_startGame
	(JNIEnv *env1, jobject obj1, jstring filesPath)
{
	void* handle;
        pthread_t game_thread;
	pass_env1 = env1;
	pass_obj1 = obj1;
	pass_filesPath = filesPath;
	LOGD("in the loader");

	// load angband lib, find entry pt
	handle = dlopen("/data/data/org.angdroid.angband/lib/libangband.so",RTLD_LOCAL | RTLD_LAZY);  
	if (!handle) {
		LOGE("dlopen failed on libangband.so");
		return;
	}	

	startGame = dlsym(handle, "Java_org_angdroid_angband_TermView_startGame");   
	if (!startGame) {
		LOGE("dlsym failed on libangband.so -> Java_org_angdroid_angband_TermView_startGame");
		return;
	}	

	LOGD("loader.create_thread");
	// start thread to call entry point
	pthread_create(&game_thread, NULL, run_angband, (void*) NULL);	

	LOGD("loader.waiting on game_thread");
	pthread_join(game_thread, NULL); // wait for thread to exit

	LOGD("loader.game_thread is finished");
	dlclose(handle);           	 // unload angband lib

	LOGD("loader.detatch");
	//(*jvm)->DetachCurrentThread(jvm); // don't think this is necessary, since never Attached
}
