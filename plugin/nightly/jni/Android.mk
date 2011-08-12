LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := nightly
LOCAL_CFLAGS := -DUSE_AND -DANGDROID_ANGBAND_PLUGIN -DANGDROID_NIGHTLY -std=c99

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src \
$(LOCAL_PATH)/../extsrc/src/object

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../curses/curses.c \
../../common/angdroid.c \
../extsrc/src/buildid.c

include $(LOCAL_PATH)/Makefile.src

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
