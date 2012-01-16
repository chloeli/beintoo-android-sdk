package com.beintoo.activities.notifications;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.activities.notifications.NotificationList.NotificationListItem;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ImageManager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationListAdapter extends ArrayAdapter<NotificationListItem>{
	private List<NotificationListItem> list;
	private Context currentContext;
	private final double ratio;	
	public ImageManager imageManager;
	private final SimpleDateFormat curFormater;
	private final BeButton gradientRow;
	public NotificationListAdapter(Context context, ArrayList<NotificationListItem> list) {
		super(context, 0, list);
		this.list = list;
		this.currentContext = context;
		this.ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		imageManager = new ImageManager(context);
		curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH);
		curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
		gradientRow = new BeButton(getContext());				
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		try {			
			final NotificationListItem item = list.get(position);	
			View v = new CustomAdapterView(currentContext,item);
			
			if(!item.isUnread)
	    		v.setBackgroundDrawable(gradientRow.setPressedBackg(
	    	    		new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.LIGHT_GRAY_GRADIENT),
	    				new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT),
	    				new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				v.setBackgroundDrawable(gradientRow.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			
			return v;		
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	private class CustomAdapterView extends LinearLayout {
		public CustomAdapterView(Context context, final NotificationListItem l)
		{
			super(context);			
			this.setPadding((int)(ratio * 7), (int)(ratio * 7), 0, (int)(ratio * 7));
			try {
				ImageView img = new ImageView(currentContext);
				int size = (int) (50 * currentContext.getResources().getDisplayMetrics().density + 0.5f);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
				params.rightMargin = (int)(ratio * 10);
				img.setLayoutParams(params);
				params.gravity = Gravity.CENTER;
				if(l.imgUrl != null){
					img.setTag(l.imgUrl);				
					imageManager.displayImage(l.imgUrl, currentContext, img);
				}else{
					img.setImageResource(R.drawable.general_image);
				}
				
				LinearLayout rowContent = new LinearLayout(currentContext);				
				rowContent.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				rowContent.setOrientation(LinearLayout.VERTICAL);
				
				String date;													 						
				Date msgDate = curFormater.parse(l.date);			
				curFormater.setTimeZone(TimeZone.getDefault());			
				date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(msgDate);				
								  
				TextView dateView = new TextView(currentContext);		
				dateView.setText(date);		  				
				dateView.setTextColor(Color.parseColor("#545859"));
				dateView.setTextSize(12);
				dateView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				
				TextView textView = new TextView(currentContext);
				textView.setText(l.message);				
				textView.setTextColor(Color.parseColor("#787A77"));
				textView.setPadding(0, 0, (int)(ratio*10), 0);
				textView.setTextSize(14);				
				textView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				if(l.isUnread){
					textView.setTextColor(Color.BLACK);
				}
				
				rowContent.addView(textView);
				rowContent.addView(dateView);
				
				LinearLayout row = new LinearLayout(currentContext);
				row.setGravity(Gravity.CENTER_VERTICAL);
				row.addView(img);
				row.addView(rowContent);
				
				addView(row);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	} 
	
	@Override
	public NotificationListItem getItem(int position) {
		return super.getItem(position);
	}

}
