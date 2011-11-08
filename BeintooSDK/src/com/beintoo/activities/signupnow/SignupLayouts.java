package com.beintoo.activities.signupnow;

import com.beintoo.R;
import com.beintoo.activities.UserRegistration;
import com.beintoo.activities.UserSelection;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.User;
import com.google.beintoogson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignupLayouts {
	
	public static void signupNowDialog(final Context context, final String feature){		
		
		final Dialog d 					= new Dialog(context);
		final LinearLayout container 	= new LinearLayout(context);
		final LinearLayout firstRow 	= new LinearLayout(context);
	 	final LinearLayout titleBox 	= new LinearLayout(context);
	 	final LinearLayout inside	 	= new LinearLayout(context);
	 	final LinearLayout bottom	 	= new LinearLayout(context);
	 	final LinearLayout blackline 	= new LinearLayout(context);
	 	final LinearLayout greyline 	= new LinearLayout(context);
		final ImageView icon 			= new ImageView(context);
		final TextView title 			= new TextView(context);
		final TextView subtitle 		= new TextView(context);
		final TextView signuptext 		= new TextView(context);
		final Button signupNow 			= new Button(context);
		final double ratio 				= (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		
		if(feature.equals(Beintoo.FEATURE_PROFILE)){			
			icon.setImageResource(R.drawable.profbt);
			title.setText(context.getString(R.string.profile));
			subtitle.setText(context.getString(R.string.profiledesc));
			signuptext.setText(context.getString(R.string.signupprofile));
		}else if(feature.equals(Beintoo.FEATURE_CHALLENGES)){
			signuptext.setText(context.getString(R.string.signupchallenge));
			icon.setImageResource(R.drawable.challengebt);
			title.setText(context.getString(R.string.challenges));
			subtitle.setText(context.getString(R.string.challengesdesc));
		}else if(feature.equals(Beintoo.FEATURE_FORUMS)){
			signuptext.setText(context.getString(R.string.signupforums));
			icon.setImageResource(R.drawable.forum);
			title.setText(context.getString(R.string.forum));
			subtitle.setText(context.getString(R.string.forumdesc));
		}
		
		
		signuptext.setPadding(0, (int)(ratio*15), 0, (int)(ratio*15));
		signuptext.setTextColor(Color.parseColor("#878485"));	
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);		
		title.setTextColor(Color.parseColor("#000000"));
		subtitle.setTextColor(Color.parseColor("#878485"));
		
		titleBox.setPadding((int)(ratio*15), 0, 0, 0);
		titleBox.setOrientation(LinearLayout.VERTICAL);
		titleBox.addView(title);
		titleBox.addView(subtitle);
		firstRow.setOrientation(LinearLayout.HORIZONTAL);
		firstRow.addView(icon);
		firstRow.addView(titleBox);
					
		signupNow.setTextColor(Color.WHITE);
		signupNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
		signupNow.setPadding(0, 0, 0, (int)(ratio*3));		
		signupNow.setText(Html.fromHtml(context.getString(R.string.signupnow)));
		signupNow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*50)));
		signupNow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SignupLayouts.goSignupOrUserSelection(context);				
				d.dismiss();
			}			
		});		
		signupNow.setBackgroundDrawable(
				new BeButton(context).setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROUNDED_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROLL_ROUNDED_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROLL_ROUNDED_BUTTON_GRADIENT)));
		
		
		inside.setOrientation(LinearLayout.VERTICAL);
		inside.addView(firstRow);
		inside.addView(signuptext);
		inside.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*160),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		inside.setPadding((int)(ratio*15), (int)(ratio*15), (int)(ratio*15), (int)(ratio*15));		
		bottom.addView(signupNow);
		bottom.setPadding((int)(ratio*15), (int)(ratio*15), (int)(ratio*15), (int)(ratio*15));
		bottom.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*160),BDrawableGradient.GRAY_GRADIENT));
		
		blackline.setBackgroundColor(Color.parseColor("#aaaaaa"));
		blackline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*2)));
		greyline.setBackgroundColor(Color.parseColor("#dddddd"));
		greyline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*5)));		
		
		container.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*200),BDrawableGradient.GRAY_GRADIENT));		
		container.setOrientation(LinearLayout.VERTICAL);		
		container.addView(inside);
		container.addView(blackline);
		container.addView(greyline);
		container.addView(bottom);
		
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(container);		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(d.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    d.show();	    
	    d.getWindow().setAttributes(lp);         				
	}
	
	public static LinearLayout signupRow(final Context context, final String feature){
		final LinearLayout container 	= new LinearLayout(context);
		final LinearLayout firstRow 	= new LinearLayout(context);
		final LinearLayout secondRow 	= new LinearLayout(context);
		final LinearLayout whiteline 	= new LinearLayout(context);
	 	final LinearLayout greyline 	= new LinearLayout(context);
		final TextView signuptext 		= new TextView(context);
		final Button signupNow 			= new Button(context);
		final double ratio 				= (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		
		
		if(feature.equals(Beintoo.FEATURE_PROFILE)){						
			signuptext.setText(context.getString(R.string.signupprofile));
		}else if(feature.equals(Beintoo.FEATURE_LEADERBOARD)){
			signuptext.setText(context.getString(R.string.signupleaderboard));					
		}else if(feature.equals(Beintoo.FEATURE_CHALLENGES)){
			signuptext.setText(context.getString(R.string.signupchallenge));			
		}else if(feature.equals(Beintoo.FEATURE_FORUMS)){
			signuptext.setText(context.getString(R.string.signupforums));			
		}else{
			signuptext.setText(context.getString(R.string.signuphome));
		}
		
		signuptext.setPadding(0, (int)(ratio*10), 0, (int)(ratio*10));
		signuptext.setTextColor(Color.parseColor("#878485"));
		signuptext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		
		signupNow.setTextColor(Color.WHITE);		
		signupNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
		signupNow.setPadding(0, 0, 0, (int)(ratio*3));
		signupNow.setText(Html.fromHtml(context.getString(R.string.signupnow)));
		signupNow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*50)));
		signupNow.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				SignupLayouts.goSignupOrUserSelection(context);
			}			
		});		
		signupNow.setBackgroundDrawable(
	    		new BeButton(context).setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROUNDED_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROLL_ROUNDED_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50), BDrawableGradient.BLU_ROLL_ROUNDED_BUTTON_GRADIENT)));			
		firstRow.setPadding((int) (ratio*15), 0, (int) (ratio*15), 0);
		firstRow.addView(signuptext);
		secondRow.setPadding((int) (ratio*15), 0, (int) (ratio*15), (int) (ratio*15));
		secondRow.addView(signupNow);
		
		whiteline.setBackgroundColor(Color.WHITE);
		whiteline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*2)));
		greyline.setBackgroundColor(Color.parseColor("#8F9193"));
		greyline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio*2)));		
		
		container.setBackgroundColor(Color.WHITE);
		container.setOrientation(LinearLayout.VERTICAL);		
		container.addView(firstRow);
		container.addView(secondRow);
		container.addView(greyline);
		container.addView(whiteline);
		
		return container;
	}
	
	public static void goSignupOrUserSelection(final Context context){		
		final ProgressDialog  dialog = ProgressDialog.show(context, "", context.getString(R.string.loadingBeintoo),true);
		final BeintooUser usr = new BeintooUser(); 
		
		new Thread(new Runnable(){      
    		public void run(){
    			User[] arr = null;
    			try{             			
    				String deviceID = DeviceId.getUniqueDeviceId(context);            				
    				if(deviceID != null){ // CHECK IF WE ARE ON FROYO AND WITHOUT SIM (BUG)
        				arr = usr.getUsersByDeviceUDID(deviceID);
        				if(arr.length == 0) { // Loading the registration form 
        					UIhandler.post(new Runnable(){
								@Override
								public void run() {
									UserRegistration userReg = new UserRegistration(context);
					            	Beintoo.currentDialog = userReg;
					            	userReg.show();								            	
								}        						
        					});
        				}else if(arr.length > 0){ // check the existing users
        					Gson gson = new Gson();
        					String jsonUsers = gson.toJson(arr);
        					// SAVE THE CURRENT USERS BY DEVICE UDID
        					PreferencesHandler.saveString("deviceUsersList", jsonUsers, context);
        					UIhandler.post(new Runnable(){
    							@Override
    							public void run() {
		        					UserSelection userSelection = new UserSelection(context);      
		        					Beintoo.currentDialog = userSelection;
		        					userSelection.show();		        					
    							}        						
        					});
        				} 
    				}else {
    					UIhandler.post(new Runnable(){
							@Override
							public void run() {
								UserRegistration userReg = new UserRegistration(context);
				            	Beintoo.currentDialog = userReg;
				            	userReg.show();									
							}        						
    					});            					
    				}
    			}catch(Exception e){ ErrorDisplayer.showConnectionErrorOnThread("Connection error.\nPlease check your Internet connection.", context,null); }
    			dialog.dismiss();
    			DialogStack.dismissAllDialogStack();
    		}
		}).start();
		
	}
	
	private static Handler UIhandler = new Handler() {
		 
	};
}
