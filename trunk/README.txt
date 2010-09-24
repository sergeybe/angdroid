How to compile Angband Native port for Android.

To build, you should use Ant 1.7.1. See here http://ant.anache.org/.


1. Copy this source to Angband source tree in ./angband-3.X.X.XXXX/src/android/


2. cd to ./angband-3.X.X.XXXX/src directory, apply android.patch with:

patch -i android/android.patch


3. Create build.properties file and add ndk-location and sdk-location properties:

...
ndk-location=/home/user/Java/android-ndk-r4b
sdk-location=/home/user/Java/android-sdk-linux_86
...

The ndk-location and sdk-location properties should point to Android NDK and Android SDK.


4. Run 'ant' to build apk file.
