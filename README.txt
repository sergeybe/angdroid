How to compile the Angband Native port for Android.

To build, you should use Ant 1.7.1. See here http://ant.anache.org/.

1. Edit build.properties file and set ndk-location and sdk-location properties:

...
ndk-location=/home/user/Java/android-ndk-r4b
sdk-location=/home/user/Java/android-sdk-linux_86
...

The ndk-location and sdk-location properties should point to Android NDK and Android SDK.


2. To retrieve and patch all external Angband source, execute:

ant plugin-src


3. To build the apk file, execute:

ant
