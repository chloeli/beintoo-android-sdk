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
package com.beintoo.activities;

import java.util.Collection;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.Vgood;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;

public class VGoodGetDialog extends Dialog{
	protected static final int OPEN_FRIENDS_FROM_VGOOD = 1;
	Dialog current;
	String vgoodExtId;
	public VGoodGetDialog(Context ctx, final Vgood vgood) {
		super(ctx, R.style.ThemeBeintooOn);	
		setContentView(R.layout.vgood);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		BeButton b = new BeButton(ctx);
		
		LinearLayout congrat = (LinearLayout) findViewById(R.id.textlayout);
		congrat.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*33),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		
		LinearLayout secondrow = (LinearLayout) findViewById(R.id.secondtrow);
		secondrow.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*100),BDrawableGradient.LIGHT_GRAY_GRADIENT));
				
		
		LoaderImageView vgoodpict = (LoaderImageView) findViewById(R.id.vgoodpict);		
		TextView title = (TextView) findViewById(R.id.couponname);
		TextView shortdesc = (TextView) findViewById(R.id.shortdesc);
		TextView endate = (TextView) findViewById(R.id.endate);
		
		try {
			vgoodExtId = vgood.getId();
			
			vgoodpict.setImageDrawable(vgood.getImageUrl());
			title.setText(vgood.getName());
			shortdesc.setText(vgood.getDescriptionSmall());
			endate.setText(vgood.getEnddate());
			 
			Collection<User> users = vgood.getWhoAlsoConverted();
			
			LinearLayout alsoconverterLayout = (LinearLayout) findViewById(R.id.alsoconverted);
			if(users != null) {
				int count = 0;
				for(User u : users){
					
					LinearLayout ll = new LinearLayout (getContext());
					ll.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
					ll.setOrientation(LinearLayout.VERTICAL);
					ll.setGravity(Gravity.CENTER);
					ll.setPadding(0, 0, 10, 0);
					final LoaderImageView image = new LoaderImageView(getContext(), u.getUserimg(),70,70);
					ll.addView(image);
					
					alsoconverterLayout.addView(ll);
					count++;
					if(count == 3) break;
				}
				
			}
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}
		
		Button getVgood = (Button) findViewById(R.id.getcoupon);
		getVgood.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		getVgood.setBackgroundDrawable(
				b.setPressedBackg(
			    		new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
		getVgood.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				PreferencesHandler.saveString("openUrl", vgood.getGetRealURL(), v.getContext());
				BeintooBrowser bb = new BeintooBrowser(v.getContext());
				bb.show();
				current.dismiss();
			}
		});
		
		Button sendasgift = (Button) findViewById(R.id.sendasgift);
		sendasgift.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		sendasgift.setBackgroundDrawable(
				b.setPressedBackg(
			    		new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
		sendasgift.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.friendLoading),true);
				new Thread(new Runnable(){      
		    		public void run(){
		    			try{     			
		    				BeintooUser u = new BeintooUser();
		    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
		    				User[] friends = u.getUserFriends(p.getUser().getId(), null);
		    				PreferencesHandler.saveString("friends", new Gson().toJson(friends), getContext());
		    				UIhandler.sendEmptyMessage(1);
		    			}catch (Exception e){
		    				e.printStackTrace();
		    			}
		    			dialog.dismiss();
		    		}
				}).start();					
				
			}
		});
				
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  FriendList f = new FriendList(getContext(),OPEN_FRIENDS_FROM_VGOOD);
			  f.previous = current;
			  f.vgoodID = vgoodExtId;
			  f.show();
			  super.handleMessage(msg);
		  }
	};
}
