This open source Java library allows you to integrate Beintoo into your Android application. Except as otherwise noted, the Beintoo Android SDK is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

Getting Started
===============

Setup your environment
--------------------------

1. Pull the repository from GitHub:

    git clone git://github.com/Beintoo/beintoo-android-sdk.git
 
2. If you have not already done so, signup for an apikey follow the (http://www.beintoo.com/business/signup.html) and then follow setup istructions (http://www.beintoo.com/business/sdk_android.html) [Android SDK Getting Started Guide]. You will need the device emulator and debugging tools.

3. The Beintoo Android SDK works fine in any Android development environment. To build in Eclipse:

	* Create a new project for the Beintoo SDK in your Eclipse workspace. 
	* Select __File__ -> __New__ -> __Project__, choose __Android Project__ (inside the Android folder), and then click __Next__.
	* Select "Create project from existing source".
	* Select the __BeintooSDK__ subdirectory from within the git repository. You should see the project properties populated (you might want to change the project name to something like "BeintooSDK").
	* Click Finish to continue.

The Beintoo SDK is now configured and ready to go.  



Integrate with an existing application
-----------

To use Beintoo in your applications you you will need to add a reference to the BeintooSDK project. To do this open the properties window for your app (__File__ -> __Properties__), select the Android item from the list, then press the __Add...__ button in the Library area and select the Beintoo SDK project created above.

Once the Beintoo SDK is referenced you need to modify the app __AndroidManifest.xml__ 
You need to add this __permissions__

	
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />	
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
	                        

You need to edit the configuration in the manifest.xml of every activity where you will call the Beintoo.BeintooStart() method by adding
android:configChanges="orientation|keyboardHidden"

For example
	
	<activity android:name="myMainAppActivity" android:configChanges="orientation|keyboardHidden">
	
Finally you have to add the gson framework which is included in the BeintooSDK directory

To do that you should put the __beintoogson-1.7.1.jar__ in your project libs dir __yourproject/libs/beintoogson-1.7.1.jar__


Start using Beintoo
-----------

First of all you need to set your __apikey__, you can do this in your first activity by calling 
	
	Beintoo.setApiKey("YOUR-API-KEY");
	
Now you can login your players to Beintoo by calling 
	
	Beintoo.playerLogin(this);
	
in your main activity

You should also put a Beintoo button in your main activity where you will call 
	
	Beintoo.BeintooStart(getContext(), true);
	
this will start the main Beintoo app where your users will see Profile, Leaderboards, Challenges and Wallet

If you want to submit a score to your players you should call for example
	
	Beintoo.submitScore(getContext(), score, true);

We provide two different methods of develivering virtual goods.

1) A dialog that informs the user that he won a reward and he can decide to see the prize or to close the dialog.  To do this you should call

	Beintoo.GetVgood(this, true, null, Beintoo.VGOOD_NOTIFICATION_ALERT)

2) A banner instead of the dialog but in this case you should allocate a LinearLayout where to show the banner, as you do with adnetworks.
To do this you should call

	Beintoo.GetVgood(this, true, container, Beintoo.VGOOD_NOTIFICATION_BANNER)

If you want to assign a single virtual good to your users you must set to false the second parameter.
	
Now for assign a Virtual Good to a player for example every 10 points, and notify the player with an AlertDialog and a choice of multiple virtual rewards you should use this method

	Beintoo.submitScoreAndGetVgood (context, 1, 10);

1 is the score of the player and 10 is the threshold for receive a new virtual reward.

By default this method use an AlertDialog and a multiple choice of virtual goods. If you want to specify other parameters you should call
	
	Beintoo.submitScoreAndGetVgood(final Context ctx, int score, int threshold, String codeID, boolean isMultiple,
			LinearLayout container, int notificationType, final BSubmitScoreListener slistener, final BGetVgoodListener glistener)

