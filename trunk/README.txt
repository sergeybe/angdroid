How to compile the Angband Native port for Android.

To build, you should use Ant 1.8.1. See here http://ant.apache.org/.

1. Edit ant.properties file and set ndk.dir and sdk.dir properties:

...
ndk.dir=/home/user/Java/android-ndk-r5b
sdk.dir=/home/user/Java/android-sdk-linux
...

The ndk.dir and sdk.dir properties should point to Android NDK and Android SDK.


2. To build the Angband debug apk file, execute:

ant

-------------------------------------------------------------------

Other supported targets for Angband Variants apk and release builds:

ant angband-release
ant variants-debug
ant variants-release

Note that Angband Variants apks end up under angdroid/alt-apk/variants/bin/
