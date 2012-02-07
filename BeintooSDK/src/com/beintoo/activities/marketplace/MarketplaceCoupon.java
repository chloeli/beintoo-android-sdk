package com.beintoo.activities.marketplace;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.R.color;
import com.beintoo.beintoosdkui.BeintooListViewDialogES;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.main.Beintoo;
import com.beintoo.main.managers.LocationMManager;
import com.beintoo.wrappers.Place;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodPoi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

public class MarketplaceCoupon extends BeintooListViewDialogES implements Button.OnClickListener{	
	private Vgood vgood;
	private Double price;
	public MarketplaceCoupon(Context context, Vgood vgood) {		
		super(context);		
		this.mContext = context;
		this.vgood = vgood;		
		setContentView(R.layout.marketplace_coupon);
		// GETTING DENSITY PIXELS RATIO
		double ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);	
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		DialogStack.addToDialogStack(this);
		try{
			// SET TITLE
			TextView t = (TextView)findViewById(R.id.dialogTitle);
			t.setText((vgood.getName().length() > 32) ? (vgood.getName().substring(0, 32)) : vgood.getName());
			player = Current.getCurrentPlayer(mContext);
			price = (vgood.getVirtualCurrencyPrice() != null) ? vgood.getVirtualCurrencyPrice() : vgood.getBedollars();
			setButtonsListeners();
			loadData();	
			loadAddressList();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void loadData(){	
		try {			
			String currencyName = "Bedollars";
			if(Beintoo.virtualCurrencyData != null){
				currencyName = Beintoo.virtualCurrencyData.get("currencyName");
				findViewById(R.id.buy_bedollar_logo).setVisibility(View.GONE);
				findViewById(R.id.buy_sendgift_bedollar_logo).setVisibility(View.GONE);
			}

			// if we are using bedollars and the player doesn't have much bedollars change the bedollars logo
			if((Beintoo.virtualCurrencyData == null) && (Marketplace.userBedollars == null || Marketplace.userBedollars < price)){
				((ImageView)findViewById(R.id.buy_bedollar_logo)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.b01));
				((ImageView)findViewById(R.id.buy_sendgift_bedollar_logo)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.b01));
			}
			
			Double price = (vgood.getVirtualCurrencyPrice() != null) ? vgood.getVirtualCurrencyPrice() : vgood.getBedollars();
			((TextView)findViewById(R.id.coupon_name)).setText(vgood.getName());			
			((TextView)findViewById(R.id.coupon_name)).setSingleLine(true);
			
			((TextView)findViewById(R.id.coupon_bedollars)).setText(mContext.getString(R.string.price) + price + " "+ currencyName);
			((TextView)findViewById(R.id.coupon_bedollars_buy)).setText(price +" "+ ((!currencyName.equals("Bedollars")) ? " "+currencyName : ""));
			((TextView)findViewById(R.id.coupon_bedollars_sendgift)).setText(price +" "+ ((!currencyName.equals("Bedollars")) ? " "+currencyName : ""));
			
			((TextView)findViewById(R.id.coupon_title_large)).setText(vgood.getName());
			((TextView)findViewById(R.id.coupon_desc)).setText(vgood.getDescription());
			SimpleDateFormat dateFormatter = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
			dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date msgDate = dateFormatter.parse(vgood.getEnddate());			
			dateFormatter.setTimeZone(TimeZone.getDefault());					
			((TextView)findViewById(R.id.coupon_valid)).setText(mContext.getString(R.string.challEnd)+" "+DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(msgDate));
			ImageView img = (ImageView)findViewById(R.id.coupon_image);
			img.setTag(vgood.getImageUrl());
			ImageManager imageDisplayer = new ImageManager(mContext);
			imageDisplayer.displayImage(vgood.getImageUrl(), mContext, img);
			imageDisplayer.interrupThread();
			if(vgood.getRating() != null)
				((RatingBar)findViewById(R.id.vgood_rating)).setRating(vgood.getRating());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setButtonsListeners(){				
		if(Marketplace.userBedollars == null || Marketplace.userBedollars < price){											
			RelativeLayout sendgift = (RelativeLayout)findViewById(R.id.coupon_sendgift_button);
			RelativeLayout buy = (RelativeLayout)findViewById(R.id.coupon_get_button);
			sendgift.setBackgroundColor(Color.parseColor("#c4c5c7"));
			buy.setBackgroundColor(Color.parseColor("#c4c5c7"));
			for(int i = 0; i < sendgift.getChildCount(); i++){
				if((sendgift.getChildAt(i) instanceof TextView)){
					((TextView)sendgift.getChildAt(i)).setTextColor(mContext.getResources().getColor(color.light_gray_text));
					((TextView)sendgift.getChildAt(i)).setTypeface(Typeface.DEFAULT);
				}
			}
			for(int i = 0; i < buy.getChildCount(); i++){
				if((buy.getChildAt(i) instanceof TextView)){
					((TextView)buy.getChildAt(i)).setTextColor(mContext.getResources().getColor(color.light_gray_text));
					((TextView)buy.getChildAt(i)).setTypeface(Typeface.DEFAULT);
				}
			}			
		}
		
		findViewById(R.id.coupon_sendgift_button).setOnClickListener(this);
		findViewById(R.id.coupon_get_button).setOnClickListener(this);
		findViewById(R.id.coupon_share_button).setOnClickListener(this);
	}

	private void loadAddressList(){
		hasReachedEnd = true;		
		setupListView();
		objectItems = new ArrayList<Object>();
		objectList = new ArrayList<Object>();
		Location playerLocation = LocationMManager.getSavedPlayerLocation(mContext);
		for(VgoodPoi vp : vgood.getVgoodPOIs()){
			if(vp.getPlace() != null)
				objectList.add(vp.getPlace());
		}
				
		for(int i = 0; i < objectList.size(); i++){
			Place p = ((Place)objectList.get(i));
			
			if(p != null && p.getLatitude() != null && p.getLongitude() != null){
				ListMarketplaceItem item = new ListMarketplaceItem(null, p.getName(), p.getAddress(), null, null);				
				String distance = null;
				if(playerLocation != null){
					if(p.getLatitude() != null && p.getLongitude() != null){
						Location l = new Location("network");
						l.setLatitude(p.getLatitude());
						l.setLongitude(p.getLongitude());
						System.out.println(l);
						float meterDistance = l.distanceTo(playerLocation);						
						distance = mContext.getString(R.string.distance) + "("+String.format("%.1f", meterDistance/1000)+"km / "+
											String.format("%.1f", (meterDistance*0.000621371192))+"mi)";
					}
				}
				item.distance = distance;
				item.lat = p.getLatitude();
				item.lon = p.getLongitude();				
				objectItems.add(item);
			}			 														
		}	
		
		if(objectItems.size() > 0){ // sorting by distance
			for(int i = 0; i < objectItems.size(); i++){
				for(int j = 0; j < objectItems.size(); j++){
					String d1 = ((ListMarketplaceItem)objectItems.get(i)).distance;
					String d2 = ((ListMarketplaceItem)objectItems.get(j)).distance;
					
					if(d1 != null && d2 != null && d2.compareTo(d1) > 0){
						ListMarketplaceItem toMove1 = (ListMarketplaceItem) objectItems.get(i);
						ListMarketplaceItem toMove2 = (ListMarketplaceItem) objectItems.get(j);
						objectItems.set(i, toMove2);
						objectItems.set(j, toMove1);					
					}			
				}
			}
		}
		
		if(objectItems.size() == 0){
			findViewById(R.id.addresslist).setVisibility(View.GONE);
			findViewById(R.id.listView).setVisibility(View.GONE);
			return;
		}
		
		listView.setFocusable(false);		
		MarketplaceListAdapter adapter = new MarketplaceListAdapter(mContext, objectItems, null);							
		updateListView(adapter);
		
		setListViewHeightBasedOnChildren(listView);
	}
	
	@Override
	public void hideLoading(){}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.coupon_share_button){
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, vgood.getName());
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, vgood.getShareURL());			
			mContext.startActivity(Intent.createChooser(shareIntent, "Share "+vgood.getName()));
		}else{
			boolean showBuyButton = (v.getId() == R.id.coupon_get_button) ? true : false;
			boolean showSendAsAGiftButton = (v.getId() == R.id.coupon_sendgift_button) ? true : false;
			MarketplaceBuy mb = new MarketplaceBuy(mContext, vgood, showBuyButton, showSendAsAGiftButton);
			mb.show();
		}		
	}
	
	private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);        
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();           
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));        
        listView.setLayoutParams(params);
        listView.requestLayout();
        
    }
	
	@Override
	public void onBackPressed() {		
		DialogStack.removeFromDialogStack(this);
		
		super.onBackPressed();
	}
	
	Handler UIHandler = new Handler();	 
}
