package com.beintoo.beintoosdkutility;

import android.content.Context;

public class DpiPxConverter {
	public static Double getRatio(Context context){
		return (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
	}
	
	public static int pixelToDpi (Context context, int pixels){		
		return (int)(context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d * pixels);
	}
}

