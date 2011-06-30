LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := larn
LOCAL_CFLAGS := -DANDROID -DUSE_MY_STR -DUSE_COLOR -DNO_CLEAR

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
android-main.c \
../../curses/curses.c \
../extsrc/src/action.c \
../extsrc/src/ansiterm.c \
../extsrc/src/bill.c \
../extsrc/src/config.c \
../extsrc/src/create.c \
../extsrc/src/data.c \
../extsrc/src/diag.c \
../extsrc/src/display.c \
../extsrc/src/fortune.c \
../extsrc/src/global.c \
../extsrc/src/help.c \
../extsrc/src/io.c \
../extsrc/src/iventory.c \
../extsrc/src/main.c \
../extsrc/src/monster.c \
../extsrc/src/moreobj.c \
../extsrc/src/movem.c \
../extsrc/src/object.c \
../extsrc/src/regen.c \
../extsrc/src/savelev.c \
../extsrc/src/scores.c \
../extsrc/src/spells.c \
../extsrc/src/spheres.c \
../extsrc/src/store.c \
../extsrc/src/sysdep.c \
../extsrc/src/tgoto.c \
../extsrc/src/tok.c \

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
