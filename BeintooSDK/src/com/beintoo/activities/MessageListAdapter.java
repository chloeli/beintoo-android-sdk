package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.activities.MessagesList.ListMessageItem;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ImageManager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageListAdapter extends ArrayAdapter<ListMessageItem>{
	private List<ListMessageItem> list;
	private Context currentContext;
	private final double ratio;	
	public ImageManager imageManager;
	private final SimpleDateFormat curFormater;
	private final BeButton gradientRow;
	
	public MessageListAdapter(Context context, ArrayList<ListMessageItem> list) {
		super(context, 0, list);
		this.list = list;
		this.currentContext = context;
		this.ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
		curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
		imageManager = new ImageManager(context);
		gradientRow = new BeButton(getContext());
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		try {			
			final ListMessageItem item = list.get(position);	
			View v = new CustomAdapterView(currentContext,item);			
			if(position % 2 == 0)
	    		v.setBackgroundDrawable(gradientRow.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 67),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				v.setBackgroundDrawable(gradientRow.setPressedBackg(
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
		public CustomAdapterView(Context context, final ListMessageItem l)
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
				
				String msgtext, date, from;			
				from = "<b>"+getContext().getString(R.string.messagefrom)+"</b> "+l.from;						
	
				Date msgDate = curFormater.parse(l.date);			
				curFormater.setTimeZone(TimeZone.getDefault());			
				date = "<b>"+getContext().getString(R.string.messagedate)+"</b> "+(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(msgDate));
				
				msgtext = l.text;
				msgtext = msgtext.replaceAll("\\<.*?>","");
				msgtext = msgtext.replaceAll("\\&.*?\\;", "");
				
				
				TextView nameView = new TextView(currentContext);		  
				nameView.setText(Html.fromHtml(from));		  
				nameView.setPadding(0, 0, 0, 0);
				nameView.setTextColor(Color.parseColor("#545859"));
				nameView.setTextSize(14);
				nameView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				if(l.isUnread){
					 nameView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
				}
				  
				TextView dateView = new TextView(currentContext);		
				dateView.setText(Html.fromHtml(date));		  
				dateView.setPadding(0, 0, 0, 0);
				dateView.setTextColor(Color.parseColor("#545859"));
				dateView.setTextSize(14);
				dateView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				if(l.isUnread){
					 dateView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
				}
				  
				TextView textView = new TextView(currentContext);
				InputFilter[] fArray = new InputFilter[1];
				fArray[0] = new InputFilter.LengthFilter(32);
				textView.setFilters(fArray);
				textView.setText(msgtext);
				textView.setPadding(0, 10, 0, 0);
				textView.setTextColor(Color.parseColor("#787A77"));
				textView.setTextSize(14);
				textView.setSingleLine(true);
				textView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				if(l.isUnread){
					textView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
					textView.setTextColor(Color.BLACK);
				}
				  
				rowContent.addView(nameView);
				rowContent.addView(dateView);
				rowContent.addView(textView);
				
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
	public ListMessageItem getItem(int position) {
		return super.getItem(position);
	}

}
