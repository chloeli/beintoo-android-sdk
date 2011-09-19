package com.beintoo.activities.mission;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.CustomFonts;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MissionAchievementMessage {
	static Dialog toast = null;
	static WindowManager wM;
	static LinearLayout container;
	static Handler mHandler;
	static Typeface tf;
	public static void showMessage(final Context ctx, final Bundle message, final int gravity){
		new Thread(new Runnable(){
			@Override
			public void run() {
				tf = CustomFonts.handWriteFont(ctx);
				UIHandler.post(new Runnable(){
					@Override
					public void run() {
						show(ctx, message, gravity);
					}					
				});								
			}			
		}).start();
	}
	
	public static void show (Context ctx, Bundle data, int gravity){
		try {
			wM = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			if(container != null){
				if(container.isShown()){
					wM.removeView(container);
					mHandler.removeCallbacks(mRunnable);
				}
			}
				
			WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
			
			
			container = new LinearLayout(ctx);
			container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			container.setBackgroundColor(Color.argb(200, 0, 0, 0));
			container.setOrientation(LinearLayout.VERTICAL);
			container.setPadding(toDip(ctx,15), toDip(ctx,10), toDip(ctx,10), toDip(ctx,10));
			
			LinearLayout firstRow = new LinearLayout(ctx);
			firstRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			firstRow.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout secondRow = new LinearLayout(ctx);
			secondRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			secondRow.setPadding(0, toDip(ctx,10), 0, 0);
			 			
			// ACHIEVEMENT NAME AND UNLOCKED MESSAGE
			TextView greetingstext = new TextView(ctx);
			String text = "<font color=\"#abc237\">"+ data.get("name").toString() + "</font> " + ctx.getString(R.string.achievementunlocked);
			greetingstext.setText(Html.fromHtml(text));
			greetingstext.setMaxLines(2);			
			greetingstext.setTextSize(17);			
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			p.weight = 1;
			greetingstext.setLayoutParams(p);
			greetingstext.setPadding(0, 0, toDip(ctx,5), 0);
			greetingstext.setTextColor(Color.parseColor("#FFFFFF"));
			
			// 100% TEXT
			TextView completed = new TextView(ctx);
			completed.setText("100%");
			completed.setTextSize(17);
			completed.setTextColor(Color.rgb(171, 194, 55));
			
			// ACHIEVEMENT MISSION MESSAGE 
			TextView message = new TextView(ctx);
			message.setText(data.get("message").toString());
			message.setTextSize(15);
			message.setMaxLines(2);
			message.setTextColor(Color.parseColor("#FFFFFF"));
			
			if(tf != null){
				completed.setTypeface(tf);
				greetingstext.setTypeface(tf);
				message.setTypeface(tf);
			}
			
			firstRow.addView(greetingstext);
			firstRow.addView(completed);
			secondRow.addView(message);
			
			container.addView(firstRow);
			
			if(data.get("message").toString().length() > 0)
				container.addView(secondRow);
			
			mParams.gravity = gravity;
			mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			mParams.width = WindowManager.LayoutParams.FILL_PARENT;
			mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
			mParams.format = PixelFormat.TRANSLUCENT;
			mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
			mParams.windowAnimations = android.R.style.Animation_Toast;		
			wM.addView(container, mParams);
				
			mHandler = new Handler();			
			mHandler.postDelayed(mRunnable, 5000);
		}catch (Exception e){e.printStackTrace();}
	}
	
	private static Runnable mRunnable = new Runnable(){
	    public void run()
	    {	    	
	    	try {
	    		wM.removeView(container);
	    	}catch(Exception e){e.printStackTrace();}
	    }
	};
	 
	private static int toDip (Context context, int px) {
		double ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);			
		return (int)(ratio*px);
	}
	
	private static Handler UIHandler = new Handler(); 
}
