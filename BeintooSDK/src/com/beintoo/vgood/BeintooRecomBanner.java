package com.beintoo.vgood;



import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BeintooAnimations;
import com.beintoo.wrappers.VgoodChooseOne;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class BeintooRecomBanner implements OnClickListener{
	private Context ctx;
	private ViewFlipper vf;
	private LinearLayout container;
	private VgoodChooseOne vgood;
	
	public BeintooRecomBanner(Context context, LinearLayout c, VgoodChooseOne v) {
		ctx = context;
		vgood = v;
		container = c;
		try {
			vf = new ViewFlipper(context);
			LinearLayout empty = new LinearLayout(context);
			vf.addView(empty);
	
			LinearLayout.LayoutParams bgparams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			
			LinearLayout top = new LinearLayout(context);
			LinearLayout.LayoutParams topparams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);			
			top.setOrientation(LinearLayout.HORIZONTAL);
			top.setLayoutParams(topparams);
			top.setBackgroundColor(Color.TRANSPARENT);
			top.setGravity(Gravity.CENTER);
	
			ImageView image = new ImageView(ctx);
			image.setImageBitmap(getDrawableFromUrl(vgood.getVgoods().get(0).getImageUrl()));
			top.addView(image);
			
			vf.addView(top);
			vf.setForegroundGravity(Gravity.CENTER);
			vf.setLayoutParams(bgparams);
			container.setOnClickListener(this);
			
		}catch(Exception e){}	
	}
	
	public void show(){ 
		try {
			Handler UIhandler = new Handler();
			UIhandler.postDelayed(inRunnable, 400);
			UIhandler.postDelayed(outRunnable, 12000);
			UIhandler.postDelayed(goneRunnable, 14000);
		}catch(Exception e){}
	}

	private Runnable inRunnable = new Runnable() {
		public void run() {
			try {
				// SETTING UP CONTAINER			
				container.setBackgroundColor(Color.TRANSPARENT);
				container.setGravity(Gravity.CENTER);
				container.addView(vf);		
				container.setVisibility(LinearLayout.VISIBLE);
				
				vf.setInAnimation(BeintooAnimations.inFromBottomAnimation());
				vf.setOutAnimation(BeintooAnimations.outFromTopAnimation());
				vf.showPrevious();
			}catch(Exception e){}
		}
	};

	private Runnable outRunnable = new Runnable() {
		public void run() {
			vf.showPrevious();
		}
	};
	
	private Runnable goneRunnable = new Runnable() {
		public void run() {	
			try {
				container.setVisibility(LinearLayout.GONE);
				container.removeAllViews();
			}catch(Exception e){}
		}
	};

	public void onClick(View v) {
		try {
			// OPEN BROWSER		
			BeintooBrowser bb = new BeintooBrowser(ctx,vgood.getVgoods().get(0).getGetRealURL());
			bb.show();
		}catch(Exception e){}
	}
	
	private String getWebUserAgent(){
		String userAgent = null;
		
		try {
			WebView wv = new WebView(ctx);
			userAgent = wv.getSettings().getUserAgentString();
		}catch(Exception e){return userAgent;}
		
		return userAgent;
	}
	
	private Bitmap getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		try{ 
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			String userAgent = getWebUserAgent();
			if(userAgent != null)
				conn.setRequestProperty("User-Agent", userAgent);
			conn.connect();	            
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            Bitmap bmImg = BitmapFactory.decodeStream(is);
            // SCALE THE IMAGE TO FIT THE DEVICE SIZE
            Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmImg, toDip(bmImg.getWidth()), toDip(bmImg.getHeight()), true);
            bis.close();
            is.close();
            
            return resizedbitmap;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	private int toDip (int px) {
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		
		return (int)(ratio*px);
	}
}
