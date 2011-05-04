package com.beintoo.activities;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooMessages;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessagesWrite extends Dialog{
	private MessagesWrite current = this;
	private Double ratio;
	public String toNickname;
	public String toExtId;
	private final int MESSAGE_SENT = 0;
	private final int MESSAGE_ERROR = 1;

	public MessagesWrite(Context context) {
		super(context, R.style.ThemeBeintoo);
		current = this;

		setContentView(R.layout.messageswrite);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.messagesend);		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*40),BDrawableGradient.BAR_GRADIENT));

		Button send = (Button) findViewById(R.id.sendmessage);
	    BeButton b = new BeButton(context);
	    send.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
	    send.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
	    send.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.messagesending),true);
				new Thread(new Runnable(){      
		    		public void run(){
		    			try{		    				
		    				TextView editto = (TextView)findViewById(R.id.editto);
		    				TextView edittext = (TextView)findViewById(R.id.edittext);
		    				String to = editto.getText().toString().trim();
		    				String text = edittext.getText().toString().trim();
		    				
		    				if(text.length() > 2 && to.length() > 0){
			    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
			    				BeintooMessages bm = new BeintooMessages();
			    				bm.sendMessage(p.getUser().getId(), toExtId,text);
			    				Message msg = new Message();
			    				Bundle b = new Bundle();
			    				b.putString("to", to);
			    				msg.setData(b);
			    				msg.what = MESSAGE_SENT;			    				
			    				UIhandler.sendMessage(msg);			    				
		    				}else {
		    					UIhandler.sendEmptyMessage(MESSAGE_ERROR);
		    				}
		    				dialog.dismiss();
		    			}catch (Exception e){
		    				dialog.dismiss();
		    				e.printStackTrace();
		    			}
		    		}
				}).start();																
			}
		});
	    

	    EditText to = (EditText) findViewById(R.id.editto);
	    to.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				FriendsList mf = new FriendsList(current.getContext(), current, FriendsList.FROM_MESSAGES, android.R.style.Theme_Dialog);
				mf.show();
			}
		});
	}
	
	public void setToNickname(String toNickname) {
		TextView editto = (TextView)findViewById(R.id.editto);
		editto.setText(toNickname);
		this.toNickname = toNickname;
	}

	public void setToExtId(String toExtId) {
		this.toExtId = toExtId;
	}
	
	private void showAlert(){
		AlertDialog alertDialog = new AlertDialog.Builder(current.getContext()).create();
	    alertDialog.setMessage(current.getContext().getString(R.string.messageselect));
	    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	    } }); 
	    alertDialog.show();
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  switch(msg.what){
			  	  case MESSAGE_SENT:
			  		  String to = msg.getData().getString("to");
			  		  MessageDisplayer.showMessage(current.getContext(), 
			  				  current.getContext().getString(R.string.messagesent)+to, Gravity.BOTTOM);
			  		  current.dismiss();
				  break;
			  	  case MESSAGE_ERROR:
			  		showAlert();
				  break;
			  }
			  
			  super.handleMessage(msg);
		  }
	};
}
