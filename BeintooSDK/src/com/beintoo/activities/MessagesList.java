/*******************************************************************************
 * Copyright 2011 Beintoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.beintoo.activities;

import java.util.ArrayList;

import java.util.List;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooMessages;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.UsersMessage;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;


public class MessagesList extends Dialog implements OnItemClickListener, OnScrollListener{
	MessagesList current;
	private double ratio;	
	private List<UsersMessage> messages;

	private final int CONNECTION_ERROR = 3;
	
	private ListView listView;	
	private MessageListAdapter adapter;
	
	private Player p; 
	
	private final int MAX_REQ_ROWS = 10;
	private int currentPageRows = 0;   	
	private int currentScrollPos = 0;
	private boolean isLoading = false;
	private boolean hasReachedEnd = false;
	private int removedItems = 0;
	
	ArrayList<ListMessageItem> messagesItems;
	
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
				MessagesWrite mw = new MessagesWrite(current.getContext());
				mw.show();
			}
		}); 

	    LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
        content.addView(progressLoading());      
	    
        messagesItems = new ArrayList<ListMessageItem>();
        
	    try {
	    	p = Current.getCurrentPlayer(getContext());	    	    	
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    
	    listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(LinearLayout.GONE);
        listView.setCacheColorHint(0);
	    listView.setOnScrollListener(this);
	    listView.setOnItemClickListener(this);
	    listView.addHeaderView(new View(current.getContext()), null, false); // WITHOUT HEADER THE FOOTER IS NOT SHOWN
	    listView.setScrollingCacheEnabled(false);
	    
	    startFirstLoading();
	} 
	 
	@Override
	public void onBackPressed() {
		if(adapter != null && adapter.imageManager != null)
			adapter.imageManager.interrupThread();
		
		super.onBackPressed();
	}

	public void startFirstLoading (){		
		messages = new ArrayList<UsersMessage>();				
		loadData(currentPageRows, MAX_REQ_ROWS);						
	}
	
	public void loadMore(){		
		currentPageRows++;
		loadData(currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS);		
	}
	
	public void reloadData(){
		messagesItems 	= new ArrayList<ListMessageItem>();
		messages 		= new ArrayList<UsersMessage>();
		
		listView.setVisibility(View.GONE);
		LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
		content.findViewWithTag(1000).setVisibility(LinearLayout.VISIBLE); 	    		
		
		int numRows = MAX_REQ_ROWS;		
		if(currentPageRows > 0)
			numRows = (currentPageRows+1)*MAX_REQ_ROWS;
		
		isLoading = true;
		loadData(0, numRows);						
	}
	
	public void setAsRead(int itemPos){
		try {
			if(messagesItems.get(itemPos).isUnread){			
				messagesItems.get(itemPos).isUnread = false;
				adapter.notifyDataSetChanged();
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removeItem(int itemPos){
		try {
			removedItems++;
			messagesItems.remove(itemPos);
			messages.remove(itemPos);
			adapter.notifyDataSetChanged();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void loadData (final int start, final int rows) {
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
					BeintooMessages bm = new BeintooMessages();            				
					// GET THE CURRENT LOGGED PLAYER
					final List<UsersMessage> tmp = bm.getUserMessages(p.getUser().getId(), null, start, rows);
					messages.addAll(tmp);	
			
					if(tmp.size() == 0){
						hasReachedEnd = true;
					}
										
					for(int i = start-removedItems; i < messages.size(); i++){
						String from = null;
						String img = null;
						if(messages.get(i).getUserFrom() != null){
							from = messages.get(i).getUserFrom().getNickname();
							img = messages.get(i).getUserFrom().getUsersmallimg();
						}else if(messages.get(i).getApp() != null){
							from = messages.get(i).getApp().getName();
							img = messages.get(i).getApp().getImageSmallUrl();
						}
					
						boolean unread = false;
						if(messages.get(i).getStatus().equals("UNREAD")) unread = true;
						ListMessageItem item = new ListMessageItem(img, from, 
								messages.get(i).getCreationdate(), messages.get(i).getText(), unread);
						messagesItems.add(item); 														
					}			
					
					
					UIhandler.post(new Runnable(){
						@Override
						public void run() {
							try {
								if(start == 0){
									adapter = new MessageListAdapter(current.getContext(), messagesItems);
									listView.setAdapter(adapter);
								}
								adapter.notifyDataSetChanged();							
								if(start == 0){
									LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
									content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);									
									listView.setVisibility(LinearLayout.VISIBLE);									
								}else{							
									listView.removeFooterView(listView.findViewWithTag(2000));
								}
								
								if(rows > MAX_REQ_ROWS){ // IF WE ARE RELOADING
									listView.setSelection(currentScrollPos);
								}
								
							}catch(Exception e){
								e.printStackTrace();
							}							
						}								
					});					
					isLoading = false;
    			}catch (Exception e){
    				e.printStackTrace();
    				manageConnectionException ();
    			}
    		}
		}).start();
	}
	
	
	private ProgressBar progressLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int)(ratio * 100), 0, 0);
		params.gravity = Gravity.CENTER;
		params.height = (int)(ratio * 45);
		params.width = (int)(ratio * 45);
		pb.setLayoutParams(params); 
		pb.setTag(1000);
		return pb;
	}

	private void manageConnectionException (){
		UIhandler.sendEmptyMessage(CONNECTION_ERROR);		
	}
	
	/* LIST VIEW DELEGATES AND METHODS */	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)  {
		try {			
			currentScrollPos = firstVisibleItem;
			if(!isLoading && !hasReachedEnd){				
				if(firstVisibleItem != 0 && (firstVisibleItem > totalItemCount - visibleItemCount-1)){
					isLoading = true;					
		            listView.addFooterView(loadingFooter());			            
					loadMore();
				}		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private LinearLayout loadingFooter(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));				
		pb.setLayoutParams(params);	            	            
        LinearLayout loading = new LinearLayout(current.getContext());
        loading.addView(pb);
        loading.setPadding(0, 10, 0, 10);
        loading.setGravity(Gravity.CENTER);
        loading.setTag(2000);
        return loading;
	}
	
	public class ListMessageItem {
		String imgUrl;
		String from;
		String date;
		String text;
		boolean isUnread;
		
		public ListMessageItem(String imgUrl, String from, String date,
				String text, boolean isUnread) {
			super();
			this.imgUrl = imgUrl;
			this.from = from;
			this.date = date;
			this.text = text;
			this.isUnread = isUnread;
		}
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  switch(msg.what){				 
				  case CONNECTION_ERROR:					  
					  ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , current.getContext(), null);
				  break;
			  }
			  super.handleMessage(msg);
		  }
	};

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {	
		setAsRead(position-1);
		MessagesRead mr = new MessagesRead(v.getContext(), messages.get(position-1), current, position-1);
		mr.show();		
	}
}
