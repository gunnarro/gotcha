sign app see http://developer.android.com/guide/publishing/app-signing.html
-------------------
Create a private key:
keytool -genkey -v -keystore android_app_key.keystore -alias gotcha_app -keyalg RSA -keysize 2048 -validity 10000


When the certificate expires, you will get a build error. On Ant builds, the error looks like this:

debug:
[echo] Packaging bin/samples-debug.apk, and signing it with a debug key...
[exec] Debug Certificate expired on 8/4/08 3:43 PM
In Eclipse/ADT, you will see a similar error in the Android console.

To fix this problem, simply delete the debug.keystore file. The default storage location for AVDs is in ~/.android/ on OS X and Linux, in C:\Documents and Settings\<user>\.android\ on Windows XP, and in C:\Users\<user>\.android\ on Windows Vista and Windows 7.

The next time you build, the build tools will regenerate a new keystore and debug key.

