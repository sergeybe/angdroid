LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := rogue
LOCAL_CFLAGS := -DANDROID -DUSE_MY_STR -DRELEASE

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
android-main.c \
../../curses/curses.c \
../extsrc/src/hit.c ../extsrc/src/init.c ../extsrc/src/invent.c \
../extsrc/src/level.c ../extsrc/src/main.c ../extsrc/src/message.c \
../extsrc/src/monster.c ../extsrc/src/move.c ../extsrc/src/object.c ../extsrc/src/pack.c \
../extsrc/src/play.c ../extsrc/src/random.c ../extsrc/src/ring.c ../extsrc/src/room.c \
../extsrc/src/save.c ../extsrc/src/score.c ../extsrc/src/spec_hit.c ../extsrc/src/throw.c \
../extsrc/src/trap.c ../extsrc/src/use.c ../extsrc/src/zap.c

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
