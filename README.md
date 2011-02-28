This open source Java library allows you to integrate Beintoo into your Android application. Except as otherwise noted, the Beintoo Android SDK is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

Getting Started
===============

Setup your environment
--------------------------

1. Pull the repository from GitHub:

    git clone git://github.com/beintoo/beintoo-android-sdk.git

2. If you have not already done so, signup for an apikey follow the (http://www.beintoo.com/business/signup.html) and then follow setup istructions (http://www.beintoo.com/business/sdk_android.html) [Android SDK Getting Started Guide]. You will need the device emulator and debugging tools.

3. The Beintoo Android SDK works fine in any Android development environment. To build in Eclipse:

  * Create a new project for the Beintoo SDK in your Eclipse workspace. 
  * Select __File__ -> __New__ -> __Project__, choose __Android Project__ (inside the Android folder), and then click __Next__.
  * Select "Create project from existing source".
  * Select the __beintoo__ subdirectory from within the git repository. You should see the project properties populated (you might want to change the project name to something like "BeintooSDK").
  * Click Finish to continue.

The Beintoo SDK is now configured and ready to go.  



Integrate with an existing application
-----------

To use Beintoo in your applications you you will need to add a reference to the BeintooSDK project. To do this open the properties window for your app (File -> Properties), select the Android item from the list, then press the Add... button in the Library area and select the Beintoo SDK project created above.

Once the Beintoo SDK is referenced you need to modify the app manifest.xml 
You need to add this permissions
<code>
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
</code>                        
You need to edit the configuration in the manifest.xml of every activity where you will call the Beintoo.BeintooStart() method by adding
android:configChanges="orientation|keyboardHidden"

For example
<code>
<activity android:name="myMainAppActivity" android:configChanges="orientation|keyboardHidden">
</code>
Finally you have to add the gson framework which is included in the BeintooSDK directory

To include gson Select your application project -> Properties -> Java build path -> Libraries -> Add External JARs and then select gson-1.6.jar in the BeintooSDK directory


Start using Beintoo
-----------

First of all you need to set your apikey, for example you can do this in your first activity by calling 
<code>
Beintoo.setApiKey("YOUR-API-KEY");
</code>
Now you can login your players to Beintoo by calling 
<code>
Beintoo.playerLogin(this);
</code>
in your main activity

You should also put a Beintoo button in your main activity where you will call 
<code>
Beintoo.BeintooStart(getContext());
</code>
this will start the main Beintoo app where your users will see Profile, Leaderboards, Challenges and Wallet

If you want to submit a score to your players you have to call for example
<code>
Beintoo.submitScore(getContext(), score, true);
</code>
Now for assign a Virtual Good to a player for example every 10 points you should do something like
<code>
PlayerScore p = Beintoo.getPlayerScore(getContext());
if(p != null){
   if(p.getBalance % 10 == 0){ // EVERY 10 POINTS WE SEND A REWARD 
         Beintoo.GetVgood(ctx);
   }
}	
</code>

