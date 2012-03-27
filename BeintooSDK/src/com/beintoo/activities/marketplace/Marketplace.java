package com.beintoo.activities.marketplace;

import java.util.List;

import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeintooListViewDialogES;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.main.Beintoo;
import com.beintoo.main.managers.LocationMManager;
import com.beintoo.wrappers.Category;
import com.beintoo.wrappers.Vgood;

import com.beintoo.R;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;


public class Marketplace extends BeintooListViewDialogES implements Button.OnClickListener{
	public static String MarketplaceKind_FEATURED = "FEATURED";
	public static String MarketplaceKind_NATIONAL = "NATIONAL";
	public static String MarketplaceKind_AROUNDME = "AROUNDME";
	public static String MarketplaceKind_NATIONAL_TOPSOLD = "NATIONAL_TOPSOLD";
	public static String MarketplaceSort_DISTANCE = "DISTANCE";
	public static String MarketplaceSort_PRICE = "PRICE";
	public static String MarketplaceSort_PRICE_ASC = "PRICE_ASC";
	
	private Integer mCurrentCategory = null; 
	private int mCurrentSection;
	public static Double userBedollars = null;
	
	public Marketplace(Context context) {
		super(context);
		super.mContext = context;
		super.setMaxRequestRow(20);
		setContentView(R.layout.marketplace);
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);	
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		DialogStack.addToDialogStack(this);
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.marketplace);		
		setupBalanceStatus();
		setupButtons();     
		
		loadMarketplace(Marketplace.MarketplaceKind_NATIONAL_TOPSOLD, null, null);		
	}
	
	private void setupButtons(){
		((Button) findViewById(R.id.marketplace_featured_button)).setOnClickListener(this);
		((Button) findViewById(R.id.marketplace_national_button)).setOnClickListener(this);
		((Button) findViewById(R.id.marketplace_aroundme_button)).setOnClickListener(this);
		((Button) findViewById(R.id.marketplace_top_button)).setOnClickListener(this);
		((Button) findViewById(R.id.marketplace_category_button)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.marketplace_order_asc)).setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.marketplace_order_desc)).setOnClickListener(this);
		
		mCurrentSection = R.id.marketplace_top_button; 		
	}
	
	private void setupBalanceStatus(){
		player = Current.getCurrentPlayer(mContext);
		
		String currencyName = "Bedollars";
		if(Beintoo.virtualCurrencyData != null){
			currencyName = Beintoo.virtualCurrencyData.get("currencyName");
		}
				
		if(Beintoo.virtualCurrencyData != null){ // if the app is with virtual currency add the player virtual currency balance
			Marketplace.userBedollars = Double.parseDouble(Beintoo.virtualCurrencyData.get("currencyBalance"));
			String balance = Beintoo.virtualCurrencyData.get("currencyBalance");			
			((TextView) findViewById(R.id.marketplace_balancecredit)).setText(balance+" "+currencyName);
		}else if(player != null && player.getUser() != null){ // if is a beintoo user without virtual currency
			Marketplace.userBedollars = player.getUser().getBedollars();
			double balance = player.getUser().getBedollars();			
			((TextView) findViewById(R.id.marketplace_balancecredit)).setText((int)balance+" "+currencyName);
		}else{ // if it's only a player and no virtual currency
			Marketplace.userBedollars = -0.1;
			findViewById(R.id.tip).setVisibility(View.GONE);
		}						
	}
	
	private void setSelectedButton(int selected){
		((Button) findViewById(R.id.marketplace_featured_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons));
		((Button) findViewById(R.id.marketplace_national_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons));
		((Button) findViewById(R.id.marketplace_aroundme_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons));
		((Button) findViewById(R.id.marketplace_top_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons));
		((Button) findViewById(R.id.marketplace_category_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons));
		((Button) findViewById(selected)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons_selected));
	}
	
	private void showPriceFilter(){
		RelativeLayout filter = (RelativeLayout) findViewById(R.id.marketplace_order_filter);
		if(filter.getVisibility() == View.GONE){
			filter.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
			filter.setVisibility(View.VISIBLE);
		}
	}
	
	private void hidePriceFilter(){		
		findViewById(R.id.marketplace_order_filter).setVisibility(View.GONE);		
	}
	
	private void hideSubcategory(){		
		findViewById(R.id.marketplace_subcategory_buttons).setVisibility(View.GONE);
	}
	
	private void showSubcategory(){
		findViewById(R.id.marketplace_subcategory_buttons).startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
		findViewById(R.id.marketplace_subcategory_buttons).setVisibility(View.VISIBLE);
	}
	
	private void handleFilterVisibility(){
		if(mCurrentSection == R.id.marketplace_featured_button){
			showPriceFilter();
		}else if(mCurrentSection == R.id.marketplace_national_button){
			showPriceFilter();
		}else if(mCurrentSection == R.id.marketplace_top_button){
			((RelativeLayout)findViewById(R.id.marketplace_order_asc)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.blu_gradient_button));
			((RelativeLayout)findViewById(R.id.marketplace_order_desc)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.blu_gradient_button));
			showPriceFilter();
		}else if(mCurrentSection == R.id.marketplace_category_button){
			showPriceFilter();
		}
	}

	@Override
	public void onClick(View v) {		
		if(isLoading) return;
		mCurrentCategory = null;
		
		if(v.getId() != R.id.marketplace_order_asc && v.getId() != R.id.marketplace_order_desc)
			setSelectedButton(v.getId());
		
		if(v.getId() == R.id.marketplace_featured_button){
			mCurrentSection = R.id.marketplace_featured_button;
			hideSubcategory();
			hidePriceFilter();
			loadMarketplace(Marketplace.MarketplaceKind_FEATURED, Marketplace.MarketplaceSort_PRICE_ASC, null);
			v.requestFocus();
		}else if(v.getId() == R.id.marketplace_national_button){
			mCurrentSection = R.id.marketplace_top_button;
			((Button) findViewById(R.id.marketplace_top_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons_selected));
			showSubcategory();
			hidePriceFilter();
			loadMarketplace(Marketplace.MarketplaceKind_NATIONAL_TOPSOLD, Marketplace.MarketplaceSort_PRICE_ASC, null);
		}else if(v.getId() == R.id.marketplace_aroundme_button){
			mCurrentSection = R.id.marketplace_aroundme_button;
			hideSubcategory();
			hidePriceFilter();
			loadMarketplace(Marketplace.MarketplaceKind_AROUNDME, Marketplace.MarketplaceSort_DISTANCE,null);
		}else if(v.getId() == R.id.marketplace_top_button){
			((Button) findViewById(R.id.marketplace_national_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons_selected));
			hidePriceFilter();
			mCurrentSection = R.id.marketplace_top_button;
			loadMarketplace(Marketplace.MarketplaceKind_NATIONAL_TOPSOLD, Marketplace.MarketplaceSort_PRICE_ASC, null);
		}else if(v.getId() == R.id.marketplace_category_button){
			hidePriceFilter();
			((Button) findViewById(R.id.marketplace_national_button)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.tab_buttons_selected));
			mCurrentSection = R.id.marketplace_category_button;
			loadCategories();
		}else if(v.getId() == R.id.marketplace_order_asc){
			if(mCurrentSection == R.id.marketplace_top_button){
				loadMarketplace(Marketplace.MarketplaceKind_NATIONAL_TOPSOLD, Marketplace.MarketplaceSort_PRICE_ASC, null);
			}else if(mCurrentSection == R.id.marketplace_featured_button){
				loadMarketplace(Marketplace.MarketplaceKind_FEATURED, Marketplace.MarketplaceSort_PRICE_ASC, null);
			}else if(mCurrentSection == R.id.marketplace_category_button){
				loadMarketplace(Marketplace.MarketplaceKind_NATIONAL, Marketplace.MarketplaceSort_PRICE_ASC, mCurrentCategory);
			}
		}else if(v.getId() == R.id.marketplace_order_desc){
			if(mCurrentSection == R.id.marketplace_top_button){
				loadMarketplace(Marketplace.MarketplaceKind_NATIONAL_TOPSOLD, Marketplace.MarketplaceSort_PRICE, null);
			}else if(mCurrentSection == R.id.marketplace_featured_button){
				loadMarketplace(Marketplace.MarketplaceKind_FEATURED, Marketplace.MarketplaceSort_PRICE, null);
			}else if(mCurrentSection == R.id.marketplace_category_button){
				loadMarketplace(Marketplace.MarketplaceKind_NATIONAL, Marketplace.MarketplaceSort_PRICE, mCurrentCategory);
			}
		}
	}
	
	private void loadCategories(){		
		hideOnlyListViewOnLoading = true;		
		super.setOptionalEmptyMessage(mContext.getString(R.string.nocoupon));
		mRunnable = new Runnable(){      
    		public void run(){
    			try{ 	        				
    				isLoading = true;
    				setDisableEndlessScrolling();
    				
    				BeintooVgood marketplace = new BeintooVgood();    				
    				final List<Category> tmp = marketplace.showCategories(player.getGuid());
					objectList.addAll(tmp);						
					tmp.clear();
					
					for(int i = start; i < objectList.size(); i++){
						Category a = ((Category)objectList.get(i));		
						String imgUrl = "http://static.beintoo.com/sdk/marketplace_categories/"+a.getId()+".png";
						ListMarketplaceItem item = new ListMarketplaceItem(imgUrl, a.getName(), null, null, null);
						objectItems.add(item); 														
					}	
					
					UIHandler.post(new Runnable(){
						@Override
						public void run() {		
							isLoading = false;
							if(adapter == null){
								MarketplaceListAdapter adapter = new MarketplaceListAdapter(mContext, objectItems, null);
								updateListView(adapter);
							}else{
								updateListView(null);
							}																						
						}						
					});					
    			}catch (Exception e){
    				isLoading = false;
    				hasReachedEnd = true;
    				e.printStackTrace();    				
    				ErrorDisplayer.showConnectionErrorOnThread(ErrorDisplayer.CONN_ERROR, mContext, null);
    			}
    		}    	    		
		};
		startFirstLoading(true);
	}
	
	
	private void loadMarketplace(final String MarketplaceKind, final String MarketplaceSort, final Integer category){		
		hideOnlyListViewOnLoading = true;		
		super.setOptionalEmptyMessage(mContext.getString(R.string.nocoupon));
		mRunnable = new Runnable(){      
    		public void run(){
    			try{ 	    		
    				isLoading = true;
    				BeintooVgood marketplace = new BeintooVgood();
    				
    				Location userLoc = LocationMManager.getSavedPlayerLocation(mContext);
    				Double lat = null, lon = null;
    				Float radius = null;
    				if(userLoc != null){
    					lat = userLoc.getLatitude();
    					lon = userLoc.getLongitude();
    					radius = userLoc.getAccuracy();
    				}
    				
    				String guid = null;
    				if(player != null) guid = player.getGuid();

    				final List<Vgood> tmp = marketplace.marketplace(lat, lon, radius, start, rows, MarketplaceKind, MarketplaceSort, category, guid);
					objectList.addAll(tmp);	
										
					if(tmp.size() == 0 || tmp.size() < MAX_REQ_ROWS)
						hasReachedEnd = true;					
					tmp.clear();
					
					for(int i = start; i < objectList.size(); i++){
						Vgood a = ((Vgood)objectList.get(i));	
						Double price = (a.getVirtualCurrencyPrice() != null) ? a.getVirtualCurrencyPrice() : a.getBedollars();
						 
						String distance = null;						
						if(a.getDistance() != null)							
							distance = mContext.getResources().getString(R.string.distance)+"("+String.format("%.1f", a.getDistance()/1000)+"km / "+
									String.format("%.1f", (a.getDistance()*0.000621371192))+"mi)";
						ListMarketplaceItem item = new ListMarketplaceItem(a.getImageSmallUrl(), a.getName(), a.getDescriptionSmall(), distance, price);
						objectItems.add(item); 														
					}	
					
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							isLoading = false;
							listView.setItemsCanFocus(true);								
							if(adapter == null){
								MarketplaceListAdapter adapter = new MarketplaceListAdapter(mContext, objectItems, objectList);								
								updateListView(adapter);
								if(objectItems.size() > 0)
									handleFilterVisibility();
							}else{
								updateListView(null);
							}
																					
						}						
					});
    			}catch (Exception e){
    				isLoading = false;
    				hasReachedEnd = true;
    				e.printStackTrace();    				
    				ErrorDisplayer.showConnectionErrorOnThread(ErrorDisplayer.CONN_ERROR, mContext, null);
    			}
    		}    	    		
		};
		startFirstLoading(true);
	}
		
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		
		super.onWindowFocusChanged(hasFocus);		
		// check if the user bought a coupon
		if(hasFocus && Beintoo.virtualCurrencyData != null){
			updateVirtualCurrencyView();			
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {		
		super.onItemClick(arg0, arg1, position, arg3);		
		if(mCurrentSection == R.id.marketplace_category_button){
			if(objectList.size() > 0)
				if(objectList.get(0) instanceof Category){					
					mCurrentCategory = ((Category)objectList.get(position)).getId();
					loadMarketplace(Marketplace.MarketplaceKind_NATIONAL, Marketplace.MarketplaceSort_PRICE_ASC, mCurrentCategory);
				}else if (objectList.get(0) instanceof Vgood){
					MarketplaceCoupon mc = new MarketplaceCoupon(mContext, ((Vgood)objectList.get(position)));
					mc.show();					
				}
		}else{
			MarketplaceCoupon mc = new MarketplaceCoupon(mContext, ((Vgood)objectList.get(position)));
			mc.show();
		}
	}

	private void updateVirtualCurrencyView(){
		String currencyName = Beintoo.virtualCurrencyData.get("currencyName");			
		((TextView) findViewById(R.id.marketplace_balancecredit)).setText(Marketplace.userBedollars+" "+currencyName);
		if(objectList.size() > 0){
			listView.invalidateViews();
		}
	}
	
	public static void updateVirtualCurrency(Double creditsSpent){
		Marketplace.userBedollars = Marketplace.userBedollars - creditsSpent;
		Beintoo.updateVirtualCurrencyBalance(Marketplace.userBedollars);
	}
	
	public static void openSignupDialog(Context context){
		SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_MARKETPLACE);
	}
	
	@Override
	public void onBackPressed() {
		DialogStack.removeFromDialogStack(this);
		if(adapter != null){
			MarketplaceListAdapter a = (MarketplaceListAdapter) adapter;
			a.interruptImageManager();
		}		
		if(mCurrentSection == R.id.marketplace_category_button && mCurrentCategory != null){
			mCurrentCategory = null;
			loadCategories();
			return;
		}
		
		super.onBackPressed();
	}
	
	Handler UIHandler = new Handler();
}

class ListMarketplaceItem {
	public String imgUrl;
	public String title;
	public String desc;
	public String distance;
	public Double bedollars;
	public Double userBedollars;
	public Double lat;
	public Double lon;
	public ListMarketplaceItem(String imgUrl, String title, String desc, String distance, Double bedollars) {			
		this.imgUrl = imgUrl;
		this.title = title;
		this.desc = desc;	
		this.distance = distance;
		this.bedollars = bedollars;		
	}
}
