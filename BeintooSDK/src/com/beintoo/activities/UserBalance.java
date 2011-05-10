package com.beintoo.activities;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.UserCredit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class UserBalance extends Dialog{
	private Dialog current;
	private double ratio;
	private List<UserCredit> balance;
	
	public UserBalance(Context context) {
		super(context,R.style.ThemeBeintoo);
		current = this; 
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userbalance);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));		
		TextView titleBar = (TextView)findViewById(R.id.dialogTitle);
		titleBar.setText(current.getContext().getString(R.string.balance));

		startLoading();
	}

	private void startLoading(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		showLoading();
		loadData();
	}
	
	private void loadData () {
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
    				BeintooUser u = new BeintooUser();    				
    				balance = u.getUserBalance(p.getUser().getId(), 0, 20);		
					UIhandler.sendEmptyMessage(0);
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();
	}
	
	private void showLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		pb.setPadding(0, (int)(50*ratio), 0, (int)(50*ratio));		
		pb.setLayoutParams(params);
		pb.setId(5000);
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setGravity(Gravity.CENTER);
		table.addView(pb);
	} 
	
	public void loadTable(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();		
				
		final ArrayList<View> rowList = new ArrayList<View>();
		
    	for(int i = 0; i<balance.size(); i++){
    		//final LoaderImageView image = new LoaderImageView(getContext(), getUsersmallimg());
    		final LoaderImageView image = new LoaderImageView(getContext(),balance.get(i).getApp().getImageUrl(),(int)(ratio * 60),(int)(ratio *60));
       		
    		TableRow row = createRow(image, balance.get(i).getApp().getName(),balance.get(i).getCreationdate(),
    				balance.get(i).getValue(),balance.get(i).getReason(), getContext());
			row.setId(i);
			rowList.add(row);
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
			BeButton b = new BeButton(current.getContext());
			if(i % 2 == 0)
	    		row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 70),BDrawableGradient.HIGH_GRAY_GRADIENT)));
	
    	}	 
	    
	    for (View row : rowList) {	      
		      table.addView(row);
		}
	}
	
	public TableRow createRow(View image, String appName,String date, double value, String reason, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding((int)(ratio*0), 0, 0, 0);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout img = new LinearLayout(row.getContext());
		  img.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  img.setOrientation(LinearLayout.HORIZONTAL);
		  img.setGravity(Gravity.CENTER);
		  img.addView(image);
		  
		  row.addView(img,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio*70)));
		 
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  
		  TextView appname = new TextView(activity);
		  appname.setText(appName);
		  appname.setPadding(0, 0, 0, 0);
		  appname.setTextColor(Color.parseColor("#545859"));
		  appname.setMaxLines(1);
		  appname.setTextSize(16);
		  appname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  		    
		  TextView movdate = new TextView(activity);
		  // PARSE DATE AND SET TEXTVIEW
		  try { 			  
			  SimpleDateFormat curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
			  curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));			  
			  Date endDate = curFormater.parse(date);			  
			  curFormater.setTimeZone(TimeZone.getDefault());
			  
			  movdate.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(endDate));	
			  movdate.setPadding(0, 0, 0, 0);
			  movdate.setTextColor(Color.parseColor("#787A77"));
			  movdate.setTextSize(12);
		  } catch (Exception e){e.printStackTrace();}		  
		  movdate.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));		  
		  
		  TextView movreason = new TextView(activity);
		  Field field;
		  try {
			  field = (R.string.class.getField(reason));
			  movreason.setText(activity.getString(field.getInt(R.string.class)));
		  } catch (SecurityException e) {
		  } catch (NoSuchFieldException e) {				
		  } catch (IllegalArgumentException e) {
		  } catch (IllegalAccessException e) {}
		  	
		  movreason.setPadding(0, 0, 0, 0);
		  movreason.setTextColor(Color.parseColor("#545859"));
		  movreason.setMaxLines(2);
		  movreason.setTextSize(12);
		  movreason.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  main.addView(appname);
		  main.addView(movdate);
		  main.addView(movreason); 
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  LinearLayout position = new LinearLayout(row.getContext());
		  position.setGravity(Gravity.CENTER);
		  
		  TextView movvalue = new TextView(activity);
		  String textvalue;
		  if(value > 0) textvalue = "+"+String.format("%02d", (int)value);
		  else textvalue = String.format("%02d", (int)value);
		  movvalue.setText(textvalue);
		  movvalue.setPadding(0, 0, 0, 0);
		  movvalue.setTextColor(Color.GRAY);
		  movvalue.setTextSize(30);
		  position.addView(movvalue);
		  
		  TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
		  params.setMargins(0, 0, 10, 0);
		  row.addView(position,params);
		  
		  return row;
	} 
	
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color == 1)
			  spacer.setBackgroundColor(Color.parseColor("#8F9193"));
		  else if(color == 2)
			  spacer.setBackgroundColor(Color.WHITE);
		  
		  return spacer;
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  loadTable();
			  super.handleMessage(msg);
		  }
	};
}
