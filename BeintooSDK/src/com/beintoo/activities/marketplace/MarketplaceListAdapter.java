package com.beintoo.activities.marketplace;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.DpiPxConverter;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Vgood;

public class MarketplaceListAdapter extends ArrayAdapter<Object>{
	private List<Object> list;
	private Context mContext;	
	public ImageManager imageManager;	
	public List<Object> vgoodlist;
	
	public MarketplaceListAdapter(Context context, ArrayList<Object> list, List<Object> vgoodlist) {
		super(context, 0, list);
		this.list = list;
		this.vgoodlist = vgoodlist;
		this.mContext = context;			
		imageManager = new ImageManager(context);		
	}
	
	public static class ViewHolder{
		public TextView firstrow;
		public TextView secondrow;
		public TextView thirdrow;
		public RelativeLayout buy;
		public ImageView img;
	}
	
	public View getView(int position, View v, ViewGroup parent) {				
		ViewHolder holder;
		final ListMarketplaceItem item = ((ListMarketplaceItem)list.get(position));
		final int pos = position;
		
		try {
			if (v == null || true) { // FIXME: temporary workaround
				LayoutInflater vi = 
					(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.marketplaceitemrow, parent, false);
				holder = new ViewHolder();
				holder.firstrow = (TextView) v.findViewById(R.id.marketplace_row_name);
				holder.secondrow = (TextView) v.findViewById(R.id.marketplace_row_desc);
				holder.thirdrow = (TextView) v.findViewById(R.id.marketplace_row_distance);
				holder.img = (ImageView) v.findViewById(R.id.marketplace_row_img);
				holder.buy = (RelativeLayout) v.findViewById(R.id.marketplace_buy);
				v.setTag(holder);
			}//else holder = (ViewHolder) v.getTag();
			
			if(position %2 == 0){				
				((LinearLayout)v.findViewById(R.id.marketplace_listview_row)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listview_background_pair));
			}else{
				((LinearLayout)v.findViewById(R.id.marketplace_listview_row)).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listview_background_disp));
			}
			
			String title = item.title;
			if(title != null){
				if(title.length() > 40)
					title = title.substring(0, 40).trim() + "...";
				holder.firstrow.setText(Html.fromHtml(title));		  			
				holder.firstrow.setTextColor(Color.parseColor("#545859"));
				holder.firstrow.setTextSize(14);
			}else holder.firstrow.setVisibility(View.GONE);
			
			if(item.desc != null){
				holder.secondrow.setTextColor(Color.parseColor("#545859"));
				holder.secondrow.setTextSize(12);
				holder.secondrow.setText(item.desc);
			}else holder.secondrow.setVisibility(View.GONE);
			
			if(item.bedollars != null){	// it means that we are in the coupon list
				String currencyName = null;//"<small>Bedollars</small>";
				if(Beintoo.virtualCurrencyData != null){
					holder.buy.findViewById(R.id.buy_bedollar_logo).setVisibility(View.GONE);
					currencyName = "<small>"+Beintoo.virtualCurrencyData.get("currencyName")+"</small>";					
				}
				
				final TextView priceValue = (TextView) holder.buy.findViewById(R.id.buy_price);
				if(currencyName == null)
					priceValue.setText(Html.fromHtml(item.bedollars.toString()));
				else
					priceValue.setText(Html.fromHtml(item.bedollars +" "+currencyName));
				
				holder.buy.setFocusable(false);				
				holder.buy.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						MarketplaceBuy mb = new MarketplaceBuy(mContext, (Vgood)vgoodlist.get(pos), true, true);
						mb.show();
						/*if((priceValue).getText().equals(mContext.getString(R.string.buy))){											
							MarketplaceBuy mb = new MarketplaceBuy(mContext, (Vgood)vgoodlist.get(pos), true, true);
							mb.show();			
							return;
						}
						/*if(Marketplace.userBedollars != null && (Marketplace.userBedollars >= item.bedollars)){ // check if the user has enough bedollars
							((RelativeLayout)v).startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
							priceValue.setText(mContext.getString(R.string.buy));
							priceValue.setTextColor(Color.DKGRAY);
							((RelativeLayout)v).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.green_gradient_button));
							((RelativeLayout)v).findViewById(R.id.buy_bedollar_logo).setVisibility(View.GONE);
						}else{ // if not don't show the green button but open the buy view
							MarketplaceBuy mb = new MarketplaceBuy(mContext, (Vgood)vgoodlist.get(pos), true, true);
							mb.show();
						}*/
					}				
				});	
				if(Marketplace.userBedollars == null || (Marketplace.userBedollars < item.bedollars)){				
					priceValue.setTextColor(mContext.getResources().getColor(R.color.light_gray_text));
					priceValue.setTypeface(Typeface.DEFAULT);
					holder.buy.setBackgroundColor(Color.parseColor("#d4d5d7"));
					((ImageView)holder.buy.findViewById(R.id.buy_bedollar_logo)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.b01));
				}
			}else if(item.lat != null && item.lon != null){ // it means that we are in the address list in the coupon details
				final TextView buttonText = (TextView) holder.buy.findViewById(R.id.buy_price);
				buttonText.setText("Map");
				holder.buy.findViewById(R.id.buy_bedollar_logo).setVisibility(View.GONE);
				int size = DpiPxConverter.pixelToDpi(mContext, 40);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size,size);
				lp.setMargins(0, 0, DpiPxConverter.pixelToDpi(mContext, 10), 0);				
				holder.buy.setLayoutParams(lp);
				holder.buy.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {						
						String title = (item.title != null) ? item.title : "Here";
						String geoUriString = "geo:0,0?q="+item.lat+","+item.lon+" ("+title+")";  						
						Uri geoUri = Uri.parse(geoUriString);  
						Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
						mContext.startActivity(mapCall);
					}				
				});
								
			}else holder.buy.setVisibility(View.GONE);
		
			if(item.distance != null){
				holder.thirdrow.setText(item.distance);
			}else holder.thirdrow.setVisibility(View.GONE);
			
			if(item.imgUrl != null){
				holder.img.setTag(item.imgUrl);
				imageManager.displayImage(item.imgUrl, mContext, holder.img);
			}else{
				holder.img.setVisibility(View.GONE);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return v;	
	}
	
	public void interruptImageManager(){
		 imageManager.interrupThread();
	}
}
