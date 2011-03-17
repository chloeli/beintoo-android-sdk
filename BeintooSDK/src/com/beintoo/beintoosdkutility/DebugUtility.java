package com.beintoo.beintoosdkutility;

import android.util.Log;

public class DebugUtility {
	static public boolean isDebugEnable = false;
	
	public static void showLog (String output){
		if(isDebugEnable)
			Log.d("DEBUG: ", output);
	}
}
