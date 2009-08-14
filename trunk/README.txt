How to compile Angband Native port for Android.

For build code you should use Ant 1.7.1. See here http://ant.anache.org/.

1. Copy this source to Angband source tree in ./angband-3.X.X.XXXX/src/android/

2. Apply patch android.patch in ./angband-3.X.X.XXXX directory.

3. Create build.properties file and add ndk-location and sdk-location properties:

...
ndk-location=/home/user/Java/android-ndk-1.5_r1/
sdk-location=/home/user/Java/android-sdk-linux_x86-1.5_r3
...

The ndk-location and sdk-location properties should point to Android NDK and Android SDK.

4. Run 'ant links' command to create links in NDK or create "angband" links manually
in apps and sources directory.

5. Run 'ant' to build apk file.
