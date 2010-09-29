LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := loader
LOCAL_CFLAGS := -DUSE_AND
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../jni

LOCAL_LDLIBS := -llog -ldl

LOCAL_SRC_FILES := \
loader.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
