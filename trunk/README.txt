How to compile the Angband Native port for Android.

To build, you should use Ant 1.8.1. See here http://ant.apache.org/.

1. Edit build.properties file and set ndk-location and sdk-location properties:

...
ndk-location=/home/user/Java/android-ndk-r4b
sdk-location=/home/user/Java/android-sdk-linux_86
...

The ndk-location and sdk-location properties should point to Android NDK and Android SDK.


2. To build the Angband debug apk file, execute:

ant

-------------------------------------------------------------------

Other supported targets for Angband Variants apk and release builds:

ant angband-release
ant variants-debug
ant variants-release

Note that Angband Variants apks end up under angdroid/alt-apk/variants/bin/
