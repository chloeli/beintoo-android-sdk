package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooMessages;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.UsersMessage;
import android.view.View.OnClickListener;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * WORKING
 * 
 */
public class MessagesList extends Dialog implements OnClickListener{
	Dialog current;
	private double ratio;	
	private List<UsersMessage> messages;
	private int currentStartPosition = 0;
	
	private final int FIRST_LOADING = 0;
	private final int NEXT_LOADING = 1;
	
	public MessagesList(Context context) {
		super(context, R.style.ThemeBeintoo);
		current = this;
		
		setContentView(R.layout.messageslist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.messages);
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		Button newmessage = (Button) findViewById(R.id.newmessage);
	    BeButton b = new BeButton(context);
	    newmessage.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
	    newmessage.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
	    newmessage.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
																
			}
		}); 	
	    
	    
	    messages = new ArrayList<UsersMessage>();
	    
	    try{
			showLoading(FIRST_LOADING);
			loadData(currentStartPosition, FIRST_LOADING);
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , context,e);}
		
		
	} 
	
	private void loadData (final int start, final int action) {
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
					BeintooMessages bm = new BeintooMessages();            				
					// GET THE CURRENT LOGGED PLAYER
					Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
					List<UsersMessage> tmp = bm.getUserMessages(p.getUser().getId(), null,start, 10);
					messages.addAll(tmp);					
					UIhandler.sendEmptyMessage(action);
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();
	}
	
	private void loadTableRows () {
		try{
			TableLayout table = (TableLayout) findViewById(R.id.table);	
			final ArrayList<View> rowList = new ArrayList<View>();
			
			addSpacerLines(rowList);
			
		    for (int i = currentStartPosition; i < messages.size(); i++){
		    	final LoaderImageView image;
		    	String nick;
		    	String date;
		    	String msgtext;
				image = new LoaderImageView(getContext(),messages.get(i).getUserFrom().getUserimg(),(int)(ratio * 65),(int)(ratio * 65));
				nick = "<b>"+getContext().getString(R.string.messagefrom)+"</b> "+messages.get(i).getUserFrom().getNickname();
				
				SimpleDateFormat curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss"); 
				curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));			
				Date msgDate = curFormater.parse(messages.get(i).getCreationdate());			
				curFormater.setTimeZone(TimeZone.getDefault());			
				date = "<b>"+getContext().getString(R.string.messagedate)+"</b> "+(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(msgDate));
				
				msgtext = messages.get(i).getText();
				
				boolean unread = false;
				if(messages.get(i).equals("UNREAD")) unread = true;
				
	    		
	    		TableRow row = createRow(image, nick,date,msgtext, unread, table.getContext());
	    		    		
				row.setId(i);
				rowList.add(row);
				row.setOnClickListener(this);
				
				BeButton b = new BeButton(getContext());
				if(i % 2 == 0)
		    		row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.LIGHT_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				else
					row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				
				addSpacerLines(rowList);
		    }		    
		 
		    loadMore(table.getContext(),rowList);
		    
		    for (View row : rowList) {		 
			      table.addView(row);
			}
		    
		}catch (Exception e){e.printStackTrace();}
	}
	
	
	private TableRow createRow(View image, String name, String date, String text, boolean isUnread, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER_VERTICAL);
		  
		  image.setPadding(15, 4, 10, 4);		  
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.HORIZONTAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  LinearLayout fromData = new LinearLayout(row.getContext());
		  fromData.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  fromData.setOrientation(LinearLayout.VERTICAL);
		  fromData.setGravity(Gravity.CENTER_VERTICAL);
		  
		  
		  TextView nameView = new TextView(activity);		  
		  nameView.setText(Html.fromHtml(name));		  
		  nameView.setPadding(0, 0, 0, 0);
		  nameView.setTextColor(Color.parseColor("#545859"));
		  nameView.setTextSize(14);
		  nameView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  if(isUnread) nameView.setTypeface(Typeface.SERIF,Typeface.BOLD);
		  
		  TextView dateView = new TextView(activity);		
		  dateView.setText(Html.fromHtml(date));		  
		  dateView.setPadding(0, 0, 0, 0);
		  dateView.setTextColor(Color.parseColor("#545859"));
		  dateView.setTextSize(14);
		  dateView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  if(isUnread) dateView.setTypeface(Typeface.SERIF,Typeface.BOLD);
		  
		  TextView textView = new TextView(activity);
		  InputFilter[] fArray = new InputFilter[1];
		  fArray[0] = new InputFilter.LengthFilter(32);
		  textView.setFilters(fArray);
		  textView.setText(text);		  
		  textView.setPadding(0, 10, 0, 0);
		  textView.setTextColor(Color.parseColor("#787A77"));
		  textView.setTextSize(14);
		  textView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  
		  fromData.addView(nameView);
		  fromData.addView(dateView);
		  fromData.addView(textView);
		 
		  main.addView(image);
		  main.addView(fromData);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 90)));

		  return row;
	}
	
	private TableRow loadMore(Context context, ArrayList<View> views) {
		TableRow row = new TableRow(context);
		row.setGravity(Gravity.CENTER);
		
		TextView moreView = new TextView(context);		
		moreView.setText("Load more");
		moreView.setTextColor(Color.parseColor("#545859"));
		moreView.setTextSize(14);
		moreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		moreView.setGravity(Gravity.CENTER);
		
		row.addView(moreView,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 90)));
		row.setId(-200);
		
		row.setOnClickListener(this);
		views.add(row);
		
		addSpacerLines(views);
		
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
	
	private void addSpacerLines (ArrayList<View> views){
		View spacer = createSpacer(getContext(),1,1);
		spacer.setId(-100);
		views.add(spacer);
		View spacer2 = createSpacer(getContext(),2,1);
		spacer2.setId(-100);
		views.add(spacer2);
	}
	
	private void showLoading (final int which){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		if(which == FIRST_LOADING)
			pb.setPadding(0, (int)(100*ratio), 0, 0);
		else
			pb.setPadding(0, (int)(5*ratio), 0, (int)(5*ratio));
		
		pb.setLayoutParams(params);
		pb.setId(5000);
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setGravity(Gravity.CENTER);
		table.addView(pb);
	} 
	
	private void clearTable(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();
	}
	
	private void removeView(View v){
		TableLayout table = (TableLayout) findViewById(R.id.table);	
		table.removeView(v);
	}
	
	private void removeProgress (){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		View v = table.findViewById(5000);
		table.removeView(v);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()>=0){ 
			System.out.println(v.getId());
		}else if(v.getId() == -200){ // LOAD MORE
			removeView(v);
			showLoading(NEXT_LOADING);			
			currentStartPosition+=10;
			loadData(currentStartPosition, NEXT_LOADING);
		}
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  switch(msg.what){
				  case FIRST_LOADING:					  
					  clearTable();
					  loadTableRows();
				  break;
				  case NEXT_LOADING:
					  loadTableRows();
					  removeProgress();
				  break;	
			  }
			  super.handleMessage(msg);
		  }
	};
}
