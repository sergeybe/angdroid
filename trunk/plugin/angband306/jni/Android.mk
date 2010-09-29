LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := angband306
LOCAL_CFLAGS := -DUSE_AND -DHAVE_MKSTEMP -DANGDROID_ANGBAND306_PLUGIN

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

# note: includes 3.0.6 squelch & borg patches
LOCAL_SRC_FILES := \
../../common/angdroid.c \
../extsrc/src/birth.c \
../extsrc/src/borg1.c \
../extsrc/src/borg2.c \
../extsrc/src/borg3.c \
../extsrc/src/borg4.c \
../extsrc/src/borg5.c \
../extsrc/src/borg6.c \
../extsrc/src/borg7.c \
../extsrc/src/borg8.c \
../extsrc/src/borg9.c \
../extsrc/src/cave.c \
../extsrc/src/cmd1.c \
../extsrc/src/cmd2.c \
../extsrc/src/cmd3.c \
../extsrc/src/cmd4.c \
../extsrc/src/cmd5.c \
../extsrc/src/cmd6.c \
../extsrc/src/dungeon.c \
../extsrc/src/files.c \
../extsrc/src/generate.c \
../extsrc/src/init1.c \
../extsrc/src/init2.c \
../extsrc/src/l-misc.c \
../extsrc/src/l-monst.c \
../extsrc/src/load.c \
../extsrc/src/l-object.c \
../extsrc/src/l-player.c \
../extsrc/src/l-random.c \
../extsrc/src/l-spell.c \
../extsrc/src/l-ui.c \
../extsrc/src/melee1.c \
../extsrc/src/melee2.c \
../extsrc/src/monster1.c \
../extsrc/src/monster2.c \
../extsrc/src/object1.c \
../extsrc/src/object2.c \
../extsrc/src/obj-info.c \
../extsrc/src/randart.c \
../extsrc/src/save.c \
../extsrc/src/script.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/squelch.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/use-obj.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wizard1.c \
../extsrc/src/wizard2.c \
../extsrc/src/x-spell.c \
../extsrc/src/xtra1.c \
../extsrc/src/xtra2.c \
../extsrc/src/z-form.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-term.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c \
../extsrc/src/lua/lapi.c \
../extsrc/src/lua/ldebug.c \
../extsrc/src/lua/lmem.c \
../extsrc/src/lua/lstrlib.c \
../extsrc/src/lua/lvm.c \
../extsrc/src/lua/lauxlib.c \
../extsrc/src/lua/ldo.c \
../extsrc/src/lua/lobject.c \
../extsrc/src/lua/ltable.c \
../extsrc/src/lua/lzio.c \
../extsrc/src/lua/lbaselib.c \
../extsrc/src/lua/lfunc.c \
../extsrc/src/lua/lparser.c \
../extsrc/src/lua/lcode.c \
../extsrc/src/lua/lgc.c \
../extsrc/src/lua/lopcodes.c \
../extsrc/src/lua/lstate.c \
../extsrc/src/lua/ltm.c  \
../extsrc/src/lua/ldblib.c \
../extsrc/src/lua/llex.c \
../extsrc/src/lua/lstring.c \
../extsrc/src/lua/ldump.c \
../extsrc/src/lua/lundump.c \
../extsrc/src/lua/ltablib.c \
../extsrc/src/lua/tolua_map.c \
../extsrc/src/lua/tolua_is.c \
../extsrc/src/lua/tolua_to.c \
../extsrc/src/lua/tolua_push.c \
../extsrc/src/lua/tolua_event.c

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
