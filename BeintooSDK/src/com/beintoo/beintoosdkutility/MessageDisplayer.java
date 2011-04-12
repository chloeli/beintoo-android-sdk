/*******************************************************************************
 * Copyright 2011 Beintoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.beintoo.beintoosdkutility;

import com.beintoo.R; 

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageDisplayer{
	static Dialog toast = null;
	static WindowManager wM;
	static View v;
	static Handler mHandler;
	public static void showMessage (Context ctx, String message, int g){
		/*Toast toast = new Toast(ctx);
		LinearLayout ll = new LinearLayout(ctx);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
		          LinearLayout.LayoutParams.WRAP_CONTENT,
		          LinearLayout.LayoutParams.WRAP_CONTENT
		      ));
		ll.setPadding(10, 10, 10, 10);
		ll.setGravity(Gravity.CENTER_VERTICAL);		
		ll.setBackgroundDrawable(messageBackground());
		
		ImageView logo = new ImageView(ctx);		
		logo.setImageResource(R.drawable.beintoobtn);
		
		TextView messageView = new TextView(ctx);
		messageView.setPadding(10,0,0,0);
		messageView.setText(message);
		messageView.setTextColor(Color.WHITE);
		messageView.setTypeface(Typeface.DEFAULT_BOLD, 0);
		messageView.setGravity(Gravity.CENTER_VERTICAL);
		 
		ll.addView(logo);
		ll.addView(messageView);

		toast.setGravity(Gravity.CENTER, 0, 20);
		toast.setView(ll);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();*/
		
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		wM = (WindowManager) ctx.getApplicationContext()
         .getSystemService(Context.WINDOW_SERVICE);
		if(v != null){
			if(v.isShown()){
				wM.removeView(v);
				mHandler.removeCallbacks(mRunnable);
			}
		}
			
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
		 LayoutInflater inflate = (LayoutInflater) ctx
		         .getApplicationContext().getSystemService(
		                 Context.LAYOUT_INFLATER_SERVICE);
		v = inflate.inflate(R.layout.notification, null);
		
		TextView tw = (TextView) v.findViewById(R.id.message);
		tw.setText(message);
		
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.main);
		ll.setBackgroundColor(Color.argb(200, 65, 65, 65)); 
		
		mParams.gravity = g;
		mParams.height = (int)(ratio*30);
		mParams.width = WindowManager.LayoutParams.FILL_PARENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		mParams.windowAnimations = android.R.style.Animation_Toast;		
		wM.addView(v, mParams);
	
		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, getDelay(tw.length()));
	}
	
	private static Runnable mRunnable = new Runnable()
	{
	    public void run()
	    {	    	
	    	wM.removeView(v);
	    }
	 };
	
	 private static int getDelay(int lenght){
		 if(wM.getDefaultDisplay().getWidth() < wM.getDefaultDisplay().getHeight())
			 if(lenght < 30)
				 return 2000;
			 else if(lenght >=30 && lenght <60)
				 return 5000;
			 else if(lenght >=60)
				 return 8000;
			 else 
				 return 1000;
		 else
			 if(lenght < 60)
				 return 2000;
			 else if(lenght >=60 && lenght <120)
				 return 5000;
			 else if(lenght >=120)
				 return 8000;
			 else 
				 return 1000;
	 }
	 
	@SuppressWarnings("unused")
	private static ShapeDrawable messageBackground(){
		//RoundRectShape rect = new RoundRectShape( new float[] {15,15, 15,15, 15,15, 15,15}, null, null);
		RoundRectShape rect = new RoundRectShape( new float[] {0,0, 0,0, 0,0, 0,0}, null, null);
		ShapeDrawable bg = new ShapeDrawable(rect);		
		
		Paint p = new Paint();
		int [] colors = {0xffC6CACE, 0xff9EA6AF};
		float [] positions = {0.0f,0.5f};
	    p.setShader(new LinearGradient(0, 0, 0, 50, colors, positions,  Shader.TileMode.MIRROR));	    	    
	    bg.getPaint().set(p);
	    bg.getPaint().setAlpha(230); 
	    
	    return bg;
	}
}
