LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := steam
LOCAL_CFLAGS := -DUSE_AND -DANGDROID_STEAM_PLUGIN
#-DHAVE_MKSTEMP 

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../curses/curses.c \
../../common/angdroid.c \
../extsrc/src/birth.c \
../extsrc/src/cave.c \
../extsrc/src/classpower.c \
../extsrc/src/cmd-attk.c \
../extsrc/src/cmd-book.c \
../extsrc/src/cmd-item.c \
../extsrc/src/cmd-know.c \
../extsrc/src/cmd-misc.c \
../extsrc/src/cmd-util.c \
../extsrc/src/dungeon.c \
../extsrc/src/effects.c \
../extsrc/src/files.c \
../extsrc/src/generate.c \
../extsrc/src/init1.c \
../extsrc/src/init2.c \
../extsrc/src/level.c \
../extsrc/src/load2.c \
../extsrc/src/monattk.c \
../extsrc/src/monmove.c \
../extsrc/src/monster1.c \
../extsrc/src/monster2.c \
../extsrc/src/mspells1.c \
../extsrc/src/mutation.c \
../extsrc/src/object1.c \
../extsrc/src/object2.c \
../extsrc/src/pet.c \
../extsrc/src/quest.c \
../extsrc/src/save.c \
../extsrc/src/skills.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/spells3.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wizard1.c \
../extsrc/src/wizard2.c \
../extsrc/src/xtra1.c \
../extsrc/src/xtra2.c \
../extsrc/src/z-form.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-term.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c \

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
