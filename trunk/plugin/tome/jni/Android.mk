LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := tome
LOCAL_CFLAGS := -DUSE_AND -DHAS_USLEEP -DANGDROID_TOME_PLUGIN -DUSE_MY_STR

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src \
$(LOCAL_PATH)/../extsrc/src/lua

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
../../curses/curses.c \
../../common/angdroid.c \
../extsrc/src/birth.c \
../extsrc/src/bldg.c \
../extsrc/src/cave.c \
../extsrc/src/cmd1.c \
../extsrc/src/cmd2.c \
../extsrc/src/cmd3.c \
../extsrc/src/cmd4.c \
../extsrc/src/cmd5.c \
../extsrc/src/cmd6.c \
../extsrc/src/cmd7.c \
../extsrc/src/cmovie.c \
../extsrc/src/dungeon.c \
../extsrc/src/files.c \
../extsrc/src/generate.c \
../extsrc/src/gen_evol.c \
../extsrc/src/gen_maze.c \
../extsrc/src/ghost.c \
../extsrc/src/gods.c \
../extsrc/src/help.c \
../extsrc/src/init1.c \
../extsrc/src/init2.c \
../extsrc/src/irc.c \
../extsrc/src/levels.c \
../extsrc/src/loadsave.c \
../extsrc/src/lua_bind.c \
../extsrc/src/melee1.c \
../extsrc/src/melee2.c \
../extsrc/src/modules.c \
../extsrc/src/monster1.c \
../extsrc/src/monster2.c \
../extsrc/src/monster3.c \
../extsrc/src/notes.c \
../extsrc/src/object1.c \
../extsrc/src/object2.c \
../extsrc/src/plots.c \
../extsrc/src/powers.c \
../extsrc/src/randart.c \
../extsrc/src/script.c \
../extsrc/src/skills.c \
../extsrc/src/spells1.c \
../extsrc/src/spells2.c \
../extsrc/src/squeltch.c \
../extsrc/src/status.c \
../extsrc/src/store.c \
../extsrc/src/tables.c \
../extsrc/src/traps.c \
../extsrc/src/util.c \
../extsrc/src/variable.c \
../extsrc/src/wild.c \
../extsrc/src/wizard1.c \
../extsrc/src/wizard2.c \
../extsrc/src/w_dun.c \
../extsrc/src/w_mnster.c \
../extsrc/src/w_obj.c \
../extsrc/src/w_play_c.c \
../extsrc/src/w_player.c \
../extsrc/src/w_quest.c \
../extsrc/src/w_spells.c \
../extsrc/src/w_util.c \
../extsrc/src/w_z_pack.c \
../extsrc/src/xtra1.c \
../extsrc/src/xtra2.c \
../extsrc/src/z-form.c \
../extsrc/src/z-rand.c \
../extsrc/src/z-sock.c \
../extsrc/src/z-term.c \
../extsrc/src/z-util.c \
../extsrc/src/z-virt.c \
../extsrc/src/lua/lapi.c \
../extsrc/src/lua/lauxlib.c \
../extsrc/src/lua/lbaselib.c \
../extsrc/src/lua/lcode.c \
../extsrc/src/lua/ldblib.c \
../extsrc/src/lua/ldebug.c \
../extsrc/src/lua/ldo.c \
../extsrc/src/lua/lfunc.c \
../extsrc/src/lua/lgc.c \
../extsrc/src/lua/liolib.c \
../extsrc/src/lua/llex.c \
../extsrc/src/lua/lmem.c \
../extsrc/src/lua/lobject.c \
../extsrc/src/lua/lparser.c \
../extsrc/src/lua/lstate.c \
../extsrc/src/lua/lstring.c \
../extsrc/src/lua/lstrlib.c \
../extsrc/src/lua/ltable.c \
../extsrc/src/lua/ltests.c \
../extsrc/src/lua/ltm.c \
../extsrc/src/lua/lundump.c \
../extsrc/src/lua/lvm.c \
../extsrc/src/lua/lzio.c \
../extsrc/src/lua/tolua_bd.c \
../extsrc/src/lua/tolua_eh.c \
../extsrc/src/lua/tolua_gp.c \
../extsrc/src/lua/tolua_lb.c \
../extsrc/src/lua/tolua_rg.c \
../extsrc/src/lua/tolua_tm.c \
../extsrc/src/lua/tolua_tt.c 
#../extsrc/src/lua/tolua.c
#../extsrc/src/lua/tolualua.c

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
