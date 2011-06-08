package com.beintoo.vgood;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;


import com.beintoo.R;

import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;

import com.beintoo.wrappers.VgoodChooseOne;

public class BeintooRecomDialog extends Dialog implements OnClickListener{
	Context ctx;
	private Dialog current;
	VgoodChooseOne vgood;
	private ImageView image;
	
	public BeintooRecomDialog(Context context, VgoodChooseOne v) {
		super(context, R.style.ThemeBeintooVgood);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		ctx = context;
		vgood = v;
		current = this;
		
		try {
			
			LinearLayout main = new LinearLayout(context);
			LinearLayout.LayoutParams mainparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			main.setPadding(toDip(5), 0, toDip(5), 0);		
			main.setBackgroundColor(Color.TRANSPARENT);
			main.setOrientation(LinearLayout.VERTICAL);
			main.setLayoutParams(mainparams);
			main.setGravity(Gravity.CENTER);
							
			LinearLayout bg = new LinearLayout(context);
			LinearLayout.LayoutParams bgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			bg.setPadding(toDip(1), toDip(1), toDip(1), toDip(1));
			bg.setBackgroundColor(Color.argb(180, 150, 150, 150));
			bg.setOrientation(LinearLayout.VERTICAL);
			bg.setLayoutParams(bgparams);
			
			LinearLayout top = new LinearLayout(context);
			LinearLayout.LayoutParams topparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);	
			top.setPadding(toDip(15), toDip(15), toDip(15), toDip(15));
			top.setOrientation(LinearLayout.HORIZONTAL);
			top.setLayoutParams(topparams);
			top.setBackgroundColor(Color.argb(190, 30, 25, 25));	
			top.setGravity(Gravity.CENTER);
			
			bg.addView(top);
					
			LinearLayout bottom = new LinearLayout(context);
			LinearLayout.LayoutParams bottomparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
			bottom.setPadding(0, toDip(10), 0, 0);
			bottom.setOrientation(LinearLayout.HORIZONTAL);
			bottom.setLayoutParams(bottomparams);
			bottom.setBackgroundColor(Color.TRANSPARENT);		
			
			image = new ImageView(ctx);			
			image.setPadding(0, 0, toDip(10), 0);			
			top.addView(image);

			// BUTTONS
			LinearLayout.LayoutParams buttonsparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,toDip(30),1f);
			buttonsparams.setMargins(toDip(5), 0, toDip(5), 0);
			
			BeButton b = new BeButton(context);		
			Button yes = new Button(context);
			yes.setBackgroundDrawable(
					b.setPressedBackg(
				    		new BDrawableGradient(0,toDip(30),BDrawableGradient.LIGHT_GRAY_GRADIENT),
							new BDrawableGradient(0,toDip(30),BDrawableGradient.GRAY_GRADIENT),
							new BDrawableGradient(0,toDip(30),BDrawableGradient.GRAY_GRADIENT)));
			yes.setText(context.getString(R.string.vgoodrecommpositive));
			yes.setId(1);
			yes.setOnClickListener(this);
			yes.setLayoutParams(buttonsparams);
			yes.setPadding(0,0,0,0);
			yes.setTextColor(Color.argb(255, 41, 34, 34));
			Button no = new Button(context);
			no.setBackgroundDrawable(
					b.setPressedBackg(
				    		new BDrawableGradient(0,toDip(30),BDrawableGradient.LIGHT_GRAY_GRADIENT),
							new BDrawableGradient(0,toDip(30),BDrawableGradient.GRAY_GRADIENT),
							new BDrawableGradient(0,toDip(30),BDrawableGradient.GRAY_GRADIENT)));		
			no.setText(context.getString(R.string.vgoodmessagenegative));	
			no.setId(0);
			no.setOnClickListener(this);
			no.setLayoutParams(buttonsparams);
			no.setPadding(0,0,0,0);
			no.setTextColor(Color.argb(255, 41, 34, 34));
			bottom.addView(yes);
			bottom.addView(no);
			
			
			main.addView(bg,mainparams);
			main.addView(bottom,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));	
			
			this.setContentView(main);			
			
		}catch (Exception e) {e.printStackTrace();}	
	}
	
	public void loadAlert(){
		try{
			getDrawableFromUrl(vgood.getVgoods().get(0).getImageUrl());
		}catch(Exception e){e.printStackTrace();}
	}
	
	private int toDip (int px) {
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		
		return (int)(ratio*px);
	}

	public void onClick(View v) {
		try {
			if(v.getId() == 0){ // REFUSE
				this.cancel();
			}else if(v.getId() == 1){ // ACCEPT
				//BeintooBrowser bb = new BeintooBrowser(ctx,vgood.getVgoods().get(0).getGetRealURL());
				//bb.show();
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(vgood.getVgoods().get(0).getGetRealURL()));
				ctx.startActivity(browserIntent);
				this.cancel();
			}
		}catch(Exception e){}
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  current.show();
		  }
	};
	
	private String getWebUserAgent(){
		String userAgent = null;
		
		try {
			WebView wv = new WebView(ctx);
			userAgent = wv.getSettings().getUserAgentString();
		}catch(Exception e){return userAgent;}
		
		return userAgent;
	}
	
	private void getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
		try{ 
			new Thread(new Runnable(){     					
				public void run(){ 
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
			            
			            image.setImageBitmap(resizedbitmap);
			            UIhandler.sendEmptyMessage(0);
					}catch (Exception e){e.printStackTrace();} 
				}
			}).start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
