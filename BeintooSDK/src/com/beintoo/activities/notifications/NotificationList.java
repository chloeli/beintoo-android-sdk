package com.beintoo.activities.notifications;

import java.util.ArrayList;
import java.util.List;

import com.beintoo.R;
import com.beintoo.activities.Challenges;
import com.beintoo.activities.Forums;
import com.beintoo.activities.Friends;
import com.beintoo.activities.MessagesList;
import com.beintoo.activities.UserAchievements;
import com.beintoo.activities.Wallet;
import com.beintoo.activities.alliances.UserAlliance;
import com.beintoo.beintoosdk.BeintooNotification;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DpiPxConverter;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.wrappers.Notification;
import com.beintoo.wrappers.Player;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NotificationList extends Dialog implements OnItemClickListener, OnScrollListener{
	private final Dialog current;	
	private final Context context;	
	
	private ListView listView;	
	private NotificationListAdapter adapter;
	
	private ArrayList<NotificationListItem> notificationItems;
	private List<Notification> notifications;
	
	private Player p; 
	
	private final int CONNECTION_ERROR = 3;	
	private final int MAX_REQ_ROWS = 10;
	private int currentPageRows = 0;   	
	private int currentScrollPos = 0;
	private boolean isLoading = false;
	private boolean hasReachedEnd = false;
	
	public NotificationList(Context context) {
		super(context, R.style.ThemeBeintoo);
		this.current = this;
		this.context = context;
		
		setContentView(R.layout.genericlist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.notifications);
				
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 40),BDrawableGradient.BAR_GRADIENT));
		
	    LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
        content.addView(progressLoading());      
	    
        notificationItems = new ArrayList<NotificationListItem>();
        
	    try {
	    	p = Current.getCurrentPlayer(getContext());	    	    	
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    
	    findViewById(R.id.firstrow).setVisibility(View.GONE);
	    
	    listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(LinearLayout.GONE);
        listView.setCacheColorHint(0);
	    listView.setOnScrollListener(this);
	    listView.setOnItemClickListener(this);
	    listView.addHeaderView(new View(context), null, false); // WITHOUT HEADER THE FOOTER IS NOT SHOWN
	    listView.setScrollingCacheEnabled(false);
	    
	    startFirstLoading();	
	}
	
	public void startFirstLoading (){		
		notifications = new ArrayList<Notification>();				
		loadData(currentPageRows, MAX_REQ_ROWS);						
	}
	
	public void loadMore(){		
		currentPageRows++;
		loadData(currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS);		
	}
	
	public void reloadData(){
		notificationItems 	= new ArrayList<NotificationListItem>();
		notifications 		= new ArrayList<Notification>();
		
		listView.setVisibility(View.GONE);
		LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
		content.findViewWithTag(1000).setVisibility(LinearLayout.VISIBLE); 	    		
		
		int numRows = MAX_REQ_ROWS;		
		if(currentPageRows > 0)
			numRows = (currentPageRows+1)*MAX_REQ_ROWS;
		
		isLoading = true;
		loadData(0, numRows);						
	}
	
	private void loadData (final int start, final int rows) {
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
					BeintooNotification bn = new BeintooNotification();            									
					final List<Notification> tmp = bn.getNotifications(p.getGuid(), start, rows);
					
					notifications.addAll(tmp);	
			
					if(tmp.size() == 0){
						hasReachedEnd = true;
					}
										
					for(int i = start; i < notifications.size(); i++){						
						String id, text, date, img, url, type;
						id = notifications.get(i).getId();
						text = notifications.get(i).getLocalizedMessage();
						date = notifications.get(i).getCreationdate();
						img = notifications.get(i).getImage_url();
						type = notifications.get(i).getType();		
						url = notifications.get(i).getUrl();
						boolean unread = false;
						
						if(notifications.get(i).getStatus().equals("UNREAD")) unread = true;
						NotificationListItem item = new NotificationListItem(id, img, text, date, type, url, unread);
						notificationItems.add(item); 														
					}			
					
					UIhandler.post(new Runnable(){
						@Override
						public void run() {
							try {
								if(start == 0){
									adapter = new NotificationListAdapter(current.getContext(), notificationItems);
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
    				UIhandler.sendEmptyMessage(CONNECTION_ERROR);
    			}
    		}
		}).start();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {		
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
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		try {			
			manageNotificationType(notificationItems.get(position-1));
			if(notificationItems.get(position-1).isUnread){				
				notificationItems.get(position-1).isUnread = false;
				adapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private ProgressBar progressLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
		double ratio = DpiPxConverter.getRatio(context); 
		params.setMargins(0, (int)(ratio * 100), 0, 0);
		params.gravity = Gravity.CENTER;
		params.height = (int)(ratio * 45);
		params.width = (int)(ratio * 45);
		pb.setLayoutParams(params); 
		pb.setTag(1000);
		return pb;
	}
	
	private void manageNotificationType(NotificationListItem item){
		String type = item.type;
		if(type.equals("MESSAGE_NEW")){
			MessagesList ml = new MessagesList(context);
			ml.show();
		}else if(type.equals("FRIENDSHIP_GOT")){
			Friends f = new Friends(context, null, Friends.FRIENDSHIP_REQUESTS, R.style.ThemeBeintoo);
			f.show();
		}else if(type.equals("CHALLENGE_INVITED")){
			Challenges c = new Challenges(context);
			c.show();
		}else if(type.equals("CHALLENGE_ACCEPTED")){
			Challenges c = new Challenges(context, true);
			c.openSectionFromOutside(Challenges.ACCEPTED);
			c.show();
		}else if(type.equals("CHALLENGE_WON") || type.equals("CHALLENGE_LOST")){
			Challenges c = new Challenges(context, true);
			c.openSectionFromOutside(Challenges.ENDED);
			c.show();
		}else if(type.equals("ACHIEVEMENT_UNLOCKED")){
			UserAchievements achievements = new UserAchievements(getContext());
			achievements.show();
		}else if(type.equals("MISSION_COMPLETED")){
			
		}else if(type.equals("ALLIANCE_ACCEPTED")){
			if(p.getAlliance() != null){
				UserAlliance ua = new UserAlliance(context, UserAlliance.SHOW_ALLIANCE, p.getAlliance().getId());
				ua.show();
			}
		}else if(type.equals("ALLIANCE_ADMIN_PENDING")){
			if(p.getAlliance() != null){
				UserAlliance ua = new UserAlliance(context, UserAlliance.PENDING_REQUESTS, p.getAlliance().getId());
				ua.show();
			}
		}else if(type.equals("FORUM_REPLIED")){			
			if(p.getUser() != null){					  
				  StringBuilder sb = new StringBuilder("http://appsforum.beintoo.com/?apikey=");
			      sb.append(DeveloperConfiguration.apiKey);
			      sb.append("&userExt=");
			      sb.append(p.getUser().getId());
			      sb.append("#main"); 
				  Forums f = new Forums(getContext(),sb.toString());				  				  
				  f.show();
			  }			
		}else if(type.equals("VGOOD_ASSIGNED") || type.equals("VGOOD_GIFTED")){
			Wallet w = new Wallet(context);
			w.show();			
		}else{ // WEB VIEW NOTIFICATION			
			if(item.url != null){
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(item.url));
				context.startActivity(browserIntent);				
			}				
		}			
	}
	
	private void markAllAsRead(final String lastNotificationId){
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				BeintooNotification bn = new BeintooNotification();
    				bn.markAllAsRead(p.getGuid(), lastNotificationId);
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();	
	}
	
	@Override
	public void onBackPressed() {		
		// STOPPING IMAGE MANAGER THREAD	
		if(adapter != null && adapter.imageManager != null)
			adapter.imageManager.interrupThread();
		if(notificationItems.size() > 0){ // MARK ALL AS READ						
			markAllAsRead(notificationItems.get(0).id);
		}
		super.onBackPressed();
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
	
	public class NotificationListItem{
		String id;
		String imgUrl;
		String message;
		String date;	
		String type;
		String url;
		boolean isUnread;
		
		public NotificationListItem(String id, String imgUrl, String message, String date, String type, String url, boolean isUnread) {
			super();
			this.id = id;
			this.imgUrl = imgUrl;
			this.message = message;
			this.date = date;
			this.type = type;
			this.url = url;
			this.isUnread = isUnread;
		}
	}
}
