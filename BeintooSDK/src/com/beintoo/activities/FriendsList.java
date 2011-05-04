package com.beintoo.activities;

import java.util.ArrayList;

import com.beintoo.R;

import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FriendsList extends Dialog implements OnClickListener{
	private Dialog current;
	private Dialog previous;
	private double ratio;
	
	public static final int FROM_MESSAGES = 1;
	public static final int FROM_PROFILE = 2;
	
	private int whichSection;
	
	User [] friends;
	
	public FriendsList(Context context, Dialog p, int ws, int theme) {
		super(context,theme);
		current = this; 
		previous = p;
		whichSection = ws;
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendlist);
		
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		
		TextView title = (TextView) findViewById(R.id.dialogTitle);		
		if(ws == FROM_MESSAGES){
			beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.LIGHT_GRAY_GRADIENT));
			title.setTextColor(Color.BLACK);
			title.setText(current.getContext().getString(R.string.friendSel));
		}else if(ws == FROM_PROFILE){
			beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));			
			title.setTextColor(Color.WHITE);
			title.setText(current.getContext().getString(R.string.friendsTitle));
		}
		
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
    				friends = u.getUserFriends(p.getUser().getId(), null);		
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
	
	public void loadFriendsTable(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();		
				
		final ArrayList<View> rowList = new ArrayList<View>();
		
    	for(int i = 0; i<friends.length; i++){
    		//final LoaderImageView image = new LoaderImageView(getContext(), getUsersmallimg());
    		final LoaderImageView image = new LoaderImageView(getContext(),friends[i].getUserimg(),(int)(ratio * 70),(int)(ratio *70));
       		
    		TableRow row = createRow(image, friends[i].getNickname(),
    				friends[i].getName(), getContext());
			row.setId(i);
			rowList.add(row);
			
			row.setOnClickListener(this);
			
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
			BeButton b = new BeButton(current.getContext());
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
	
    	}	 
	    
	    for (View row : rowList) {	      
		      table.addView(row);
		}
	}
	
	public TableRow createRow(View image, String nick,String name, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding(15, 4, 10, 4);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout foto = new LinearLayout(row.getContext());
		  foto.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  foto.setOrientation(LinearLayout.VERTICAL);
		  
		  foto.addView(image);
		  row.addView(foto,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)); //
		 
		  
		 // row.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  TextView nickname = new TextView(activity);		  
		  nickname.setText(nick);
		  nickname.setPadding(5, 0, 0, 0);
		  nickname.setTextColor(Color.parseColor("#545859"));
		  nickname.setMaxLines(1);
		  nickname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		   
		  TextView scoreView = new TextView(activity);		
		  scoreView.setText(name);	
		  scoreView.setPadding(5, 0, 0, 0);
		  scoreView.setTextColor(Color.parseColor("#787A77"));
		  scoreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  	
		  main.addView(nickname);
		  main.addView(scoreView);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 90))); //
		 // row.addView(img);
		  
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
	
	public void onClick(View v) {
		if(whichSection == FROM_MESSAGES){
			((MessagesWrite) previous).setToNickname(friends[v.getId()].getNickname());
			((MessagesWrite)previous).setToExtId(friends[v.getId()].getId());
			current.dismiss();
		}else if(whichSection == FROM_PROFILE){
			UserProfile userProfile = new UserProfile(getContext(),friends[v.getId()].getId());
			userProfile.show();
		}
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  loadFriendsTable();
			  super.handleMessage(msg);
		  }
	};
}
