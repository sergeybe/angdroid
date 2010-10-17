LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sangband
LOCAL_CFLAGS := -DUSE_AND -DHAVE_MKSTEMP -DANGDROID_SANGBAND_PLUGIN

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../common/angdroid.c \
../extsrc/src/attack.c \
../extsrc/src/birth.c \
../extsrc/src/borgdumb.c \
../extsrc/src/cave.c \
../extsrc/src/cmd1.c \
../extsrc/src/cmd2.c \
../extsrc/src/cmd3.c \
../extsrc/src/cmd4.c \
../extsrc/src/cmd5.c \
../extsrc/src/cmd6.c \
../extsrc/src/dungeon.c \
../extsrc/src/effects.c \
../extsrc/src/files.c \
../extsrc/src/generate.c \
../extsrc/src/info.c \
../extsrc/src/init1.c \
../extsrc/src/init2.c \
../extsrc/src/intrface.c \
../extsrc/src/loadsave.c \
../extsrc/src/monattk.c \
../extsrc/src/monmove.c \
../extsrc/src/monster1.c \
../extsrc/src/monster2.c \
../extsrc/src/obj_make.c \
../extsrc/src/object1.c \
../extsrc/src/object2.c \
../extsrc/src/quest.c \
../extsrc/src/randart.c \
../extsrc/src/skills.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/spells3.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/talents.c \
../extsrc/src/traps.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wizard1.c \
../extsrc/src/wizard2.c \
../extsrc/src/xtra1.c \
../extsrc/src/xtra2.c \
../extsrc/src/z-file.c \
../extsrc/src/z-form.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-term.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
