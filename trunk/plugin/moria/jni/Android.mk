LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := moria
LOCAL_CFLAGS := -DTURBOC_COLOR -DANDROID -DUSE_MY_STR

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
android-main.c \
../../curses/curses.c \
../extsrc/ibmpc/tcio.c ../extsrc/src/main.c ../extsrc/src/misc1.c \
../extsrc/src/misc2.c ../extsrc/src/misc3.c  ../extsrc/src/misc4.c \
../extsrc/src/store1.c ../extsrc/src/files.c \
../extsrc/src/create.c ../extsrc/src/death.c ../extsrc/src/desc.c \
../extsrc/src/generate.c ../extsrc/src/sets.c ../extsrc/src/dungeon.c \
../extsrc/src/creature.c ../extsrc/src/eat.c \
../extsrc/src/help.c ../extsrc/src/magic.c ../extsrc/src/potions.c \
../extsrc/src/prayer.c ../extsrc/src/save.c ../extsrc/src/staffs.c \
../extsrc/src/wands.c ../extsrc/src/scrolls.c ../extsrc/src/spells.c \
../extsrc/src/wizard.c ../extsrc/src/store2.c ../extsrc/src/signals.c \
../extsrc/src/moria1.c ../extsrc/src/moria2.c ../extsrc/src/moria3.c \
../extsrc/src/moria4.c ../extsrc/src/monsters.c \
../extsrc/src/treasure.c ../extsrc/src/variable.c ../extsrc/src/recall.c \
../extsrc/src/player.c ../extsrc/src/tables.c \
../extsrc/src/rnd.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
