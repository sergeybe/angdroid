LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := frogknows
LOCAL_CFLAGS := -DTC_COLOR -DANDROID -DAUTOROLLER -DUSE_MY_STR

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../include \
$(LOCAL_PATH)/../../curses \
$(LOCAL_PATH)/../extsrc/src

LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := \
android-main.c \
../../curses/curses.c \
../extsrc/src/tcio.c ../extsrc/src/main.c ../extsrc/src/misc1.c \
../extsrc/src/misc2.c ../extsrc/src/store1.c ../extsrc/src/files.c \
../extsrc/src/create.c ../extsrc/src/desc.c ../extsrc/src/describe.c \
../extsrc/src/generate.c ../extsrc/src/sets.c ../extsrc/src/dungeon.c \
../extsrc/src/creature.c ../extsrc/src/death.c ../extsrc/src/eat.c \
../extsrc/src/help.c ../extsrc/src/magic.c ../extsrc/src/potions.c \
../extsrc/src/prayer.c ../extsrc/src/save.c ../extsrc/src/staffs.c \
../extsrc/src/wands.c ../extsrc/src/scrolls.c ../extsrc/src/spells.c \
../extsrc/src/wizard.c ../extsrc/src/store2.c ../extsrc/src/signals.c \
../extsrc/src/moria1.c ../extsrc/src/moria2.c ../extsrc/src/monsters.c\
../extsrc/src/treasure.c ../extsrc/src/vars1.c ../extsrc/src/vars2.c \
../extsrc/src/recall.c ../extsrc/src/undef.c \
../extsrc/src/player.c ../extsrc/src/tables.c ../extsrc/src/rods.c \
../extsrc/src/random.c 

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
