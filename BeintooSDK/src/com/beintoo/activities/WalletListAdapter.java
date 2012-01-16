package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.activities.Wallet.ListWalletItem;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ImageManager;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WalletListAdapter extends ArrayAdapter<ListWalletItem>{
	private List<ListWalletItem> list;
	private Context currentContext;
	private final double ratio;	
	public ImageManager imageManager;
	private final SimpleDateFormat curFormater;
	
	public WalletListAdapter(Context context, ArrayList<ListWalletItem> list) {
		super(context, 0, list);
		this.list = list;
		this.currentContext = context;
		this.ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		imageManager = new ImageManager(context);
		curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
		curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		try {			
			final ListWalletItem item = list.get(position);	
			View v = new CustomAdapterView(currentContext,item);
			BeButton b = new BeButton(getContext());
			if(position % 2 == 0)
	    		v.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				v.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			return v;		
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class CustomAdapterView extends LinearLayout {
		public CustomAdapterView(Context context, final ListWalletItem l)
		{
			super(context);			
			this.setPadding((int)(ratio * 10), (int)(ratio * 10), 0, (int)(ratio * 10));
			try {
				ImageView img = new ImageView(currentContext);
				int size = (int) (60 * currentContext.getResources().getDisplayMetrics().density + 0.5f);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
				params.rightMargin = (int)(ratio * 10);
				img.setLayoutParams(params);
				params.gravity = Gravity.CENTER;
				img.setTag(l.imgUrl);
				
				imageManager.displayImage(l.imgUrl, currentContext, img);
				
				LinearLayout rowContent = new LinearLayout(currentContext);
				rowContent.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				rowContent.setOrientation(LinearLayout.VERTICAL);
				
				String date;									
				Date msgDate = curFormater.parse(l.date);			
				curFormater.setTimeZone(TimeZone.getDefault());			
				date = getContext().getString(R.string.challEnd)+ " "+(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(msgDate));
								
				TextView titleView = new TextView(currentContext);
				String title = l.title;
				if(title.length() > 90)
					title = title.substring(0, 90).trim() + "...";
				titleView.setText(Html.fromHtml(title));		  
				titleView.setPadding(0, 0, 0, 0);
				titleView.setTextColor(Color.parseColor("#545859"));
				titleView.setTextSize(14);
				titleView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
								  
				TextView dateView = new TextView(currentContext);		
				dateView.setText(Html.fromHtml(date));		  
				dateView.setPadding(0, 0, 0, 0);
				dateView.setTextColor(Color.parseColor("#545859"));
				dateView.setTextSize(12);
				dateView.setPadding(0, (int)(ratio * 10), 0, 0);
				dateView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				
				rowContent.addView(titleView);
				rowContent.addView(dateView);				
				
				LinearLayout row = new LinearLayout(currentContext);			
				row.addView(img);
				row.addView(rowContent);
				
				addView(row);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	} 
	
	@Override
	public ListWalletItem getItem(int position) {
		return super.getItem(position);
	}

}
