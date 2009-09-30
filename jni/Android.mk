LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := angband
LOCAL_CFLAGS := -I$(LOCAL_PATH)/../.. -DUSE_AND \
-DDEFAULT_PATH='"/data/data/org.angdroid.angband2/files"'

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

LOCAL_SRC_FILES := \
angdroid.c \
../../player/calcs.c \
../../player/timed.c \
../../player/util.c \
../../attack.c \
../../birth.c \
../../button.c \
../../cave.c \
../../cmd0.c \
../../cmd1.c \
../../cmd2.c \
../../cmd3.c \
../../cmd4.c \
../../cmd5.c \
../../cmd-know.c \
../../cmd-obj.c \
../../death.c \
../../debug.c \
../../dungeon.c \
../../effects.c \
../../files.c \
../../game-cmd.c \
../../game-event.c \
../../generate.c \
../../history.c \
../../init1.c \
../../init2.c \
../../load.c \
../../load-old.c \
../../option.c \
../../pathfind.c \
../../prefs.c \
../../randname.c \
../../save.c \
../../savefile.c \
../../score.c \
../../signals.c \
../../snd-sdl.c \
../../spells1.c \
../../spells2.c \
../../squelch.c \
../../store.c \
../../tables.c \
../../target.c \
../../trap.c \
../../ui-birth.c \
../../ui.c \
../../ui-event.c \
../../ui-menu.c \
../../util.c \
../../variable.c \
../../wizard.c \
../../wiz-spoil.c \
../../wiz-stats.c \
../../x-spell.c \
../../xtra2.c \
../../xtra3.c \
../../z-file.c \
../../z-form.c \
../../z-msg.c \
../../z-quark.c \
../../z-rand.c \
../../z-term.c \
../../z-type.c \
../../z-util.c \
../../z-virt.c \
../../monster/melee1.c \
../../monster/melee2.c \
../../monster/monster1.c \
../../monster/monster2.c \
../../object/identify.c \
../../object/obj-desc.c \
../../object/obj-info.c \
../../object/obj-make.c \
../../object/obj-power.c \
../../object/obj-ui.c \
../../object/obj-util.c \
../../object/randart.c

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
