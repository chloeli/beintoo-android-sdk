package com.beintoo.activities;

import java.util.ArrayList;


import com.beintoo.R;

import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;

import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FriendList extends Dialog implements OnClickListener{
	protected static final int OPEN_FRIENDS_FROM_VGOOD = 1;
	protected static final int OPEN_FRIENDS_FROM_GPS = 2;
	Dialog current;
	Dialog previous;
	String vgoodID;
	Context currentContext;
	ArrayList<String> usersExts;
	ArrayList<String> usersNicks;	
	User [] friends;
	
	public FriendList(Context context, int calledFrom) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendlist);
		current = this;
		currentContext = context;
		usersExts = new ArrayList<String>();
		usersNicks = new ArrayList<String>();
		
		try {
			friends = deserializeFriends();
			if(friends.length > 0)
				loadFriendsTable(calledFrom);
			else { // NO VGOODS SHOW A MESSAGE
				TextView noGoods = new TextView(getContext());
				noGoods.setText("You don't have any friends");
				noGoods.setTextColor(Color.GRAY);
				noGoods.setPadding(20,0,0,0);						
				TableLayout table = (TableLayout) findViewById(R.id.table);
				table.addView(noGoods);
			}
			
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , context);}
		
		
	}
	
	public void loadFriendsTable(int calledFrom){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();		
				
		final ArrayList<View> rowList = new ArrayList<View>();
		
    	for(int i = 0; i<friends.length; i++){
    		final LoaderImageView image = new LoaderImageView(getContext(), friends[i].getUsersmallimg());
    		
       		
    		TableRow row = createRow(image, friends[i].getNickname(),
    				friends[i].getName(), getContext());
			row.setId(i);
			rowList.add(row);
			
			// IF WHE CALLED THE DIALOG FROM SEND AS A GIFT WE NEED THE
			// ROW CLICKABLE TO SEND THE VGOOD AS A GIFT
			if(calledFrom == OPEN_FRIENDS_FROM_VGOOD)
				row.setOnClickListener(this);
			
			View spacer = createSpacer(getContext(),0,2);
			spacer.setId(-100);
			rowList.add(spacer);
			if(i%2 == 0) row.setBackgroundColor(Color.parseColor("#d8eaef"));
			
			// ADD THE USER EXT TO THE USERS ARRAY
			usersExts.add(friends[i].getId());
			usersNicks.add(friends[i].getNickname());
    	}	 
	    
	    for (View row : rowList) {	      
		      table.addView(row);
		}
	}
	
	public User[] deserializeFriends (){		
		String jsonFriends = PreferencesHandler.getString("friends",currentContext);
               
		User[] friends = new Gson().fromJson(jsonFriends, User[].class);
		
		return friends;
	}
	
	public static TableRow createRow(View image, String nick,String name, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER_VERTICAL);
		  
		  image.setPadding(10, 10, 10, 10);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout foto = new LinearLayout(row.getContext());
		  foto.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  foto.setOrientation(LinearLayout.VERTICAL);
		  
		  foto.addView(image);
		  row.addView(foto,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		 
		  
		 // row.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  
		  		 
		  LinearLayout bt = new LinearLayout(row.getContext());
		  bt.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  bt.setOrientation(LinearLayout.VERTICAL);
		  bt.setGravity(Gravity.RIGHT);
		  
		  TextView nickname = new TextView(activity);		  
		  nickname.setText(nick);
		  nickname.setPadding(0, 10, 10, 10);
		  nickname.setTextColor(Color.parseColor("#83be56"));
		  nickname.setMaxLines(1);
		  nickname.setTypeface(null,Typeface.BOLD);
		  nickname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  TextView scoreView = new TextView(activity);		
		  scoreView.setText(name);	
		  scoreView.setPadding(0, 10, 0, 10);
		  scoreView.setTextColor(Color.parseColor("#787A77"));
		  scoreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  	
		  main.addView(nickname);
		  main.addView(scoreView);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  
		  return row;
	}
	
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);

		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color != 0)
			  spacer.setBackgroundColor(Color.LTGRAY);

		  return spacer;
	}
	
	// CALLED WHEN THE USER SELECT A FRIEND TO SEND A GIFT
	public void onClick(View v) {
		  confirmDialog(v.getId());
	}
	
	public void confirmDialog (final int selected) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage("Are you sure you want to send this coupon as a gift to "+usersNicks.get(selected)+"?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   current.dismiss();
		               previous.dismiss();
		        	   new Thread(new Runnable(){      
				    	public void run(){
			    			try{     
			    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
			    				String userFrom = p.getUser().getId();
			    				String userTo = usersExts.get(selected);			    				
			    				BeintooVgood sendVgood = new BeintooVgood();
			    				com.beintoo.wrappers.Message msg = sendVgood.sendAsAGift(vgoodID, userFrom, userTo, null);
			    				if(!msg.getKind().equals("error"))
			    					UIhandler.sendEmptyMessage(selected);
			    			}catch (Exception e){
			    				e.printStackTrace();
			    			}
				    	}
					   }).start();		
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void doneDialog (int i) {
		MessageDisplayer.showMessage(getContext(), "Gift sent to "+usersNicks.get(i));
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  doneDialog(msg.what);
			  super.handleMessage(msg);
		  }
	};
	
}
