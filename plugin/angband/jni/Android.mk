LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := angband
LOCAL_CFLAGS := -DUSE_AND -DANGDROID_ANGBAND_PLUGIN -std=c99

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../common/angdroid.c \
../extsrc/src/player/calcs.c \
../extsrc/src/player/player.c \
../extsrc/src/player/spell.c \
../extsrc/src/player/timed.c \
../extsrc/src/player/util.c \
../extsrc/src/attack.c \
../extsrc/src/birth.c \
../extsrc/src/button.c \
../extsrc/src/cave.c \
../extsrc/src/cmd0.c \
../extsrc/src/cmd1.c \
../extsrc/src/cmd2.c \
../extsrc/src/cmd3.c \
../extsrc/src/cmd4.c \
../extsrc/src/cmd-misc.c \
../extsrc/src/cmd-obj.c \
../extsrc/src/death.c \
../extsrc/src/debug.c \
../extsrc/src/dungeon.c \
../extsrc/src/effects.c \
../extsrc/src/files.c \
../extsrc/src/game-cmd.c \
../extsrc/src/game-event.c \
../extsrc/src/generate.c \
../extsrc/src/history.c \
../extsrc/src/init2.c \
../extsrc/src/load.c \
../extsrc/src/load-old.c \
../extsrc/src/macro.c \
../extsrc/src/option.c \
../extsrc/src/parser.c \
../extsrc/src/pathfind.c \
../extsrc/src/prefs.c \
../extsrc/src/randname.c \
../extsrc/src/save.c \
../extsrc/src/savefile.c \
../extsrc/src/score.c \
../extsrc/src/signals.c \
../extsrc/src/snd-sdl.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/squelch.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/target.c \
../extsrc/src/trap.c \
../extsrc/src/ui.c \
../extsrc/src/ui-birth.c \
../extsrc/src/ui-knowledge.c \
../extsrc/src/ui-menu.c \
../extsrc/src/ui-options.c \
../extsrc/src/ui-spell.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wizard.c \
../extsrc/src/wiz-spoil.c \
../extsrc/src/wiz-stats.c \
../extsrc/src/x-char.c \
../extsrc/src/x-spell.c \
../extsrc/src/xtra2.c \
../extsrc/src/xtra3.c \
../extsrc/src/z-bitflag.c \
../extsrc/src/z-file.c \
../extsrc/src/z-form.c \
../extsrc/src/z-msg.c \
../extsrc/src/z-quark.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-term.c \
../extsrc/src/z-textblock.c \
../extsrc/src/z-type.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c \
../extsrc/src/monster/melee1.c \
../extsrc/src/monster/melee2.c \
../extsrc/src/monster/monster1.c \
../extsrc/src/monster/monster2.c \
../extsrc/src/object/identify.c \
../extsrc/src/object/obj-desc.c \
../extsrc/src/object/obj-info.c \
../extsrc/src/object/obj-make.c \
../extsrc/src/object/obj-power.c \
../extsrc/src/object/obj-ui.c \
../extsrc/src/object/obj-util.c \
../extsrc/src/object/randart.c \

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
