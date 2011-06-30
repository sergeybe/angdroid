LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := loader-classics
LOCAL_CFLAGS := -DUSE_AND
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include

LOCAL_LDLIBS := -llog -ldl

LOCAL_SRC_FILES := \
loader.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
