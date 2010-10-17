LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := npp
LOCAL_CFLAGS := -DUSE_AND -DHAVE_MKSTEMP -DANGDROID_NPP_PLUGIN

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../common/angdroid.c \
../extsrc/src/attack.c \
../extsrc/src/birth.c \
../extsrc/src/button.c \
../extsrc/src/calcs.c \
../extsrc/src/cave.c \
../extsrc/src/cmd-know.c \
../extsrc/src/cmd-obj.c \
../extsrc/src/cmd0.c \
../extsrc/src/cmd1.c \
../extsrc/src/cmd2.c \
../extsrc/src/cmd3.c \
../extsrc/src/cmd4.c \
../extsrc/src/cmd5.c \
../extsrc/src/death.c \
../extsrc/src/dump_items.c \
../extsrc/src/dungeon.c \
../extsrc/src/effect.c \
../extsrc/src/feature.c \
../extsrc/src/files.c \
../extsrc/src/game-cmd.c \
../extsrc/src/game-event.c \
../extsrc/src/generate.c \
../extsrc/src/identify.c \
../extsrc/src/init1.c \
../extsrc/src/init2.c \
../extsrc/src/load.c \
../extsrc/src/melee1.c \
../extsrc/src/melee2.c \
../extsrc/src/monster1.c \
../extsrc/src/monster2.c \
../extsrc/src/obj-info.c \
../extsrc/src/obj-ui.c \
../extsrc/src/obj-util.c \
../extsrc/src/object1.c \
../extsrc/src/object2.c \
../extsrc/src/pathfind.c \
../extsrc/src/prefs.c \
../extsrc/src/quest.c \
../extsrc/src/randart.c \
../extsrc/src/save.c \
../extsrc/src/score.c \
../extsrc/src/signals.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/squelch.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/target.c \
../extsrc/src/timed.c \
../extsrc/src/ui-birth.c \
../extsrc/src/ui-event.c \
../extsrc/src/ui-menu.c \
../extsrc/src/ui.c \
../extsrc/src/use-obj.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wizard1.c \
../extsrc/src/wizard2.c \
../extsrc/src/x-char.c \
../extsrc/src/x-spell.c \
../extsrc/src/xtra1.c \
../extsrc/src/xtra2.c \
../extsrc/src/xtra3.c \
../extsrc/src/z-file.c \
../extsrc/src/z-form.c \
../extsrc/src/z-msg.c \
../extsrc/src/z-quark.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-term.c \
../extsrc/src/z-type.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
