package com.beintoo.activities.marketplace;

import com.beintoo.R;
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.main.Beintoo;
import com.beintoo.main.managers.LocationMManager;
import com.beintoo.vgood.VgoodSendToFriend;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodPoi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

public class MarketplaceBuy extends Dialog implements Button.OnClickListener{
	private Context mContext;
	private Dialog current;
	private Vgood vgood;
	private Player player;
	
	public MarketplaceBuy(Context context, Vgood v, boolean showBuyButton, boolean showSendAsAGiftButton) {
		super(context);
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.vgood = v;
		this.current = this;
		DialogStack.addToDialogStack(this);
		player = Current.getCurrentPlayer(mContext);
		setContentView(R.layout.marketplace_buy);
		setupView(showBuyButton, showSendAsAGiftButton);
	}
	
	private void setupView(boolean showBuyButton, boolean showSendAsAGiftButton){
		Double price = (vgood.getVirtualCurrencyPrice() != null) ? vgood.getVirtualCurrencyPrice() : vgood.getBedollars();
		String currencyName = "Bedollars";
		if(Beintoo.virtualCurrencyData != null){
			currencyName = Beintoo.virtualCurrencyData.get("currencyName");
		}
		
		ImageManager imageDisplayer = new ImageManager(mContext);
		ImageView i = (ImageView) findViewById(R.id.buy_coupon_image);
		i.setTag(vgood.getImageSmallUrl());
		imageDisplayer.displayImage(vgood.getImageSmallUrl(), mContext, i);
		
		((TextView)findViewById(R.id.buy_couponname)).setText(vgood.getName());
		((TextView)findViewById(R.id.buy_coupon_bedollars)).setText(mContext.getString(R.string.price)+price+" "+currencyName);
		if(vgood.getVgoodPOIs().size() == 1){
			for (VgoodPoi vp : vgood.getVgoodPOIs()){
				if(vp.getPlace() != null && vp.getPlace().getName() != null){
					((TextView)findViewById(R.id.buy_couponwhere)).setText(mContext.getString(R.string.where)+vp.getPlace().getName());					
				}else findViewById(R.id.buy_couponwhere).setVisibility(View.GONE);
			}
		}else findViewById(R.id.buy_couponwhere).setVisibility(View.GONE);
				
		if(MarketplaceList.userBedollars == null || MarketplaceList.userBedollars < price){
			findViewById(R.id.buy_buttons).setVisibility(View.GONE);			
			TextView needBedollars = (TextView) findViewById(R.id.buy_youneedbedollars);
			needBedollars.setVisibility(View.VISIBLE);
			String text = String.format(mContext.getString(R.string.needbedollars), ((int) (price-MarketplaceList.userBedollars)) +" "+ currencyName);
			needBedollars.setText(text);
		}else{		
			if(showBuyButton){
				((Button)findViewById(R.id.buy_buy_button)).setOnClickListener(this);
				((Button)findViewById(R.id.buy_buy_button)).setVisibility(View.VISIBLE);
				if(!showSendAsAGiftButton)					
					((LinearLayout.LayoutParams)((Button)findViewById(R.id.buy_buy_button)).getLayoutParams()).leftMargin = 0;
			}else ((Button)findViewById(R.id.buy_buy_button)).setVisibility(View.GONE);
			
			if(showSendAsAGiftButton){
				((Button)findViewById(R.id.buy_sendgift_button)).setOnClickListener(this);
				((Button)findViewById(R.id.buy_sendgift_button)).setVisibility(View.VISIBLE);
				if(!showBuyButton)
					((LinearLayout.LayoutParams)((Button)findViewById(R.id.buy_sendgift_button)).getLayoutParams()).rightMargin = 0;
			}else ((Button)findViewById(R.id.buy_sendgift_button)).setVisibility(View.GONE);			
		}	
				
		// no virtual currency and no user, show signup button
		if((player == null) || Beintoo.virtualCurrencyData == null && player != null && player.getUser() == null){
			((Button)findViewById(R.id.buy_signup_button)).setText(Html.fromHtml(mContext.getString(R.string.signupnow)));
			((Button)findViewById(R.id.buy_signup_button)).setOnClickListener(this);
			((Button)findViewById(R.id.buy_signup_button)).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		Double price = (vgood.getVirtualCurrencyPrice() != null) ? vgood.getVirtualCurrencyPrice() : vgood.getBedollars();
		
		if(v.getId() == R.id.buy_sendgift_button){			
			if(player != null && player.getUser() == null){ // players can't send as a gift
				current.dismiss();
				MarketplaceList.openSignupDialog(mContext);
				return;
			}
			final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.friendLoading),true);
			new Thread(new Runnable(){      
	    		public void run(){
	    			try{     			
	    				BeintooUser u = new BeintooUser();
	    				
	    				final User[] friends = u.getUserFriends(player.getUser().getId(), null);	    				
	    				UIHandler.post(new Runnable(){
							@Override
							public void run() {
								VgoodSendToFriend f = new VgoodSendToFriend(mContext, VgoodSendToFriend.OPEN_FRIENDS_FROM_MARKETPLACE, friends, vgood);
								f.previous = current;								
								f.vgoodID = vgood.getId();
								f.show();
							}	    					
	    				});
	    			}catch (Exception e){
	    				e.printStackTrace();
	    			}
	    			dialog.dismiss();
	    		}
			}).start();	
		}else if(v.getId() == R.id.buy_buy_button){			
			handleOpenVgood();			
		}else if(v.getId() == R.id.buy_signup_button){			
			SignupLayouts.goSignupOrUserSelection(mContext);
		}
		
		if(Beintoo.virtualCurrencyData != null){
			MarketplaceList.updateVirtualCurrency(price);
		}
		
		DialogStack.removeFromDialogStack(this);
		current.dismiss();
	}
	
	public void handleOpenVgood(){		
		Uri.Builder url = Uri.parse(vgood.getGetRealURL()).buildUpon();
		if(player != null) url.appendQueryParameter("guid", player.getGuid());
		
		if(Beintoo.virtualCurrencyData != null)
			url.appendQueryParameter("developer_user_guid", Beintoo.getVirtualCurrencyDevUserId());
		
		if(!vgood.isOpenInBrowser()){
			BeintooBrowser bb = new BeintooBrowser(mContext, url.toString());
			bb.show();
		}else{
			Location loc = LocationMManager.getSavedPlayerLocation(getContext());
			if(loc != null){
				Double latitude = loc.getLatitude();
				Double longitude = loc.getLongitude();
				Float accuracy = loc.getAccuracy();
				url.appendQueryParameter("lat", latitude.toString());
				url.appendQueryParameter("lon", longitude.toString());
				url.appendQueryParameter("acc", accuracy.toString());
			}
						
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url.toString()));
			mContext.startActivity(browserIntent);
		}
		
		DialogStack.removeFromDialogStack(this);
		current.dismiss();
	}	
	
	@Override
	public void onBackPressed() {
		DialogStack.removeFromDialogStack(this);
		super.onBackPressed();
	}
	
	Handler UIHandler = new Handler(); 
}
