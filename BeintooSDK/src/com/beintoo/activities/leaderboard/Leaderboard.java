package com.beintoo.activities.leaderboard;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.beintoo.R;
import com.beintoo.activities.ChallengeCreate;
import com.beintoo.activities.Friends;
import com.beintoo.activities.UserProfile;
import com.beintoo.activities.alliances.UserAlliance;
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooAlliances;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.LeaderboardContainer;
import com.beintoo.wrappers.Player;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class Leaderboard extends Dialog implements OnItemClickListener, OnScrollListener{
	
	private Dialog current;
	private Context currentContext;
	
	private ArrayList<String> usersExts;
	private ArrayList<String> usersNicks;
	private ArrayList<Leaders> leaderslist;
	private Map<String, LeaderboardContainer> leaders;
	
	private String codeID; // THE CODE ID OF THE SELECTED CONTEST
	private String leader_kind;	
	
	private ImageManager imageManager;
	private ListView listView;
	private LeaderboardAdapter adapter;
	
	private Player player = null;
	private Leaders currentUser = null;
	
	private final double ratio;	
	private final int MAX_REQ_ROWS = 20;
	private int currentPageRows = 1;   
		
	private boolean isLoading = false;
	private boolean hasReachedEnd = false;
	
	
	public Leaderboard(Context context, String kind, String selectedContest) {
		super(context, R.style.ThemeBeintoo);
		setContentView(R.layout.leaderboardlist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		this.current 		= this;
		this.currentContext = context;		
		this.leader_kind 	= kind;
		this.codeID 		= selectedContest;
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);	
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		 
        listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(LinearLayout.GONE);
        listView.setCacheColorHint(0);
        
		LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
        content.addView(progressLoading());       
		
		LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
		tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		if(!Beintoo.isLogged(context)){ // IF THE USER IS NOT LOGGED HIDE TIPS FOR CHALLENGE AND SHOW SIGNUP BUTTON
			tip.setVisibility(LinearLayout.GONE);
			LinearLayout signup = (LinearLayout) findViewById(R.id.signupPlayer);
			signup.addView(SignupLayouts.signupRow(context, Beintoo.FEATURE_LEADERBOARD));
			signup.setVisibility(View.VISIBLE);	
		}
		
		if(leader_kind != null && leader_kind.equals("ALLIANCES")){
			findViewById(R.id.tip).setVisibility(View.GONE);
			findViewById(R.id.spacer).setVisibility(View.GONE);
		}
		
		imageManager 	= new ImageManager(currentContext);		
		usersExts 		= new ArrayList<String>();
		usersNicks 		= new ArrayList<String>();		                
        
		listView.setOnItemClickListener(this);        
        listView.setOnScrollListener(this);
        
        this.firstLoading();
	}
	
	private void firstLoading(){
		new Thread(new Runnable(){      
    		public void run(){
    			try{        				
    				player = Current.getCurrentPlayer(currentContext);
    				      
    				if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){
    					BeintooApp app = new BeintooApp();
    					String userExt = null;
        				if(player != null && player.getUser() != null)
        					userExt = player.getUser().getId(); 
    					leaders = app.getLeaderboard(userExt, leader_kind, codeID, 0, MAX_REQ_ROWS);    					
    				}else if(leader_kind.equals("ALLIANCES")){    					
    					BeintooAlliances ba = new BeintooAlliances();
    					String allianceExtId = null;
        				if(player != null && player.getAlliance() != null)
        					allianceExtId = player.getAlliance().getId();    					
    					leaders = ba.getLeaderboard(allianceExtId, null, null, 0, MAX_REQ_ROWS, codeID);
    				}
    				
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							try {
								LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);
								content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
								leaderslist = getLeaders();						        
								listView.setVisibility(LinearLayout.VISIBLE);
						        
						        // SET THE CURRENT USER
								if(currentUser != null){    									
						        	View header = setCurrentUserRow();
						        	listView.addHeaderView(header, null, false);
						        }else listView.addHeaderView(new View(currentContext), null, false); // empty view, without we can't add a footer :/
						        
								adapter = new LeaderboardAdapter(currentContext, R.layout.leaderboarditem, leaderslist);
						        listView.setAdapter(adapter);
								
						        // IF WE ARE IN CLOSEST SCROLL THE LISTVIEW TO THE USER POSITION
								if(leader_kind != null && leader_kind.equals("CLOSEST")){									
									int currentUserPos = -1;
									for(int i = 0; i < leaderslist.size(); i++){
										if(leaders.get(codeID) != null && leaders.get(codeID).getCurrentUser() != null && leaders.get(codeID).getLeaderboard() != null)
											if(leaders.get(codeID).getCurrentUser().getUser().getNickname().equals(leaders.get(codeID).getLeaderboard().get(i).getUser().getNickname())){
												currentUserPos = i;
												break;
											}
									}
									
									listView.setSelectionFromTop(currentUserPos, (int)(ratio*140));
								}
							}catch(Exception e){ 
								e.printStackTrace(); 
							}
						}
					});
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();
		
	};
	
	private ArrayList<Leaders> getLeaders (){
		final ArrayList<Leaders> lead = new ArrayList<Leaders>();
		
		try {					
			Iterator<?> it = leaders.entrySet().iterator();					
			// SET TITLE
			TextView contestName = (TextView)findViewById(R.id.dialogTitle);
	
			int count = 0;
			
			
		    while (it.hasNext()) {	     	
		    	@SuppressWarnings("unchecked")
				Map.Entry<String, LeaderboardContainer> pairs = (Map.Entry<String, LeaderboardContainer>) it.next();		    	
			    LeaderboardContainer arr = pairs.getValue();			    		
		    	//if(selectedContest == count){
		    		contestName.setText(arr.getContest().getName());
		    		String feed = arr.getContest().getFeed();
		    		codeID = pairs.getKey();
		    		 
		    		// ADD THE CURRENT USER
		    		if(arr.getCurrentUser() != null && arr.getCurrentUser().getUser() != null){
		    			currentUser = new Leaders(arr.getCurrentUser().getUser().getUsersmallimg(),
		    					arr.getCurrentUser().getUser().getNickname(), arr.getCurrentUser().getScore().toString(),
			    				feed,arr.getCurrentUser().getPos().toString(), true);
		    		}else if(arr.getCurrentUser() != null && arr.getCurrentUser().getAlliance() != null){
		    			currentUser = new Leaders(null,
		    					arr.getCurrentUser().getAlliance().getName(), arr.getCurrentUser().getScore().toString(),
			    				feed,arr.getCurrentUser().getPos().toString(), true);
		    		}
		    		
		    		// ADD THE LEADERBOARD USERS
			    	for(int i = 0; i<arr.getLeaderboard().size(); i++){
			    		if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){
			    			Leaders l = new Leaders(arr.getLeaderboard().get(i).getUser().getUsersmallimg(),
				    				arr.getLeaderboard().get(i).getUser().getNickname(),
				    				arr.getLeaderboard().get(i).getScore().toString(),
				    				feed,arr.getLeaderboard().get(i).getPos().toString(), false);
				    		lead.add(l);
			    		}else if(leader_kind.equals("ALLIANCES")){
			    			Leaders l = new Leaders(null,
				    				arr.getLeaderboard().get(i).getAlliance().getName(), arr.getLeaderboard().get(i).getScore().toString(),
				    				feed,arr.getLeaderboard().get(i).getPos().toString(), false);
				    		lead.add(l);			    			
			    		}
			    		
			    		// ADD THE USER EXT TO THE USERS ARRAY
			    		if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){
			    			usersExts.add(arr.getLeaderboard().get(i).getUser().getId());
							usersNicks.add(arr.getLeaderboard().get(i).getUser().getNickname());
			    		}else if(leader_kind.equals("ALLIANCES")){
			    			// ADD THE ALLIANCE ID TO THIS ARRAY AND USE IT IN THE ON ITEM CLICK
			    			usersExts.add(arr.getLeaderboard().get(i).getAlliance().getId());
			    		}
			    	}
		    	}
		    	
		    	count++;
		    //}
		    
		    return lead;
		}catch (Exception e){
			e.printStackTrace();
			return lead;
		}
	}

	public void startLoadingMore (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{       		
    				Map<String, LeaderboardContainer> moreleaders = null;
    				if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){
    					BeintooApp app = new BeintooApp();
    					String userExt = null;
        				if(player != null && player.getUser() != null)
        					userExt = player.getUser().getId();        				
    					moreleaders = app.getLeaderboard(userExt, leader_kind, codeID, currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS);
    				}else if(leader_kind.equals("ALLIANCES")){
    					BeintooAlliances ba = new BeintooAlliances();
    					String allianceExtId = null;
        				if(player != null && player.getAlliance() != null)
        					allianceExtId = player.getAlliance().getId();    					
    					moreleaders = ba.getLeaderboard(allianceExtId, null, null, currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS, codeID);
    				}
    				
    				if(moreleaders != null && moreleaders.size() > 0){
    					loadMore(moreleaders);
    					UIHandler.post(new Runnable(){
    						@Override
    						public void run() {
    							listView.removeFooterView(listView.findViewWithTag(2000));
    							adapter.notifyDataSetChanged();							
    					}}); 
    				}else{
    					hasReachedEnd = true;    					
    					UIHandler.post(new Runnable(){
    						@Override
    						public void run() {
    							listView.removeFooterView(listView.findViewWithTag(2000));							
    					}});
    				}
    				
    				isLoading = false;
    			}catch(Exception e){e.printStackTrace();}
    		}
		}).start();	
	}
	
	private void loadMore(Map<String, LeaderboardContainer> moreleaders){		
		try {
			Iterator<?> it = moreleaders.entrySet().iterator();		
			int count = 0;
		    while (it.hasNext()) {	    	
		    	@SuppressWarnings("unchecked")
				Map.Entry<String, LeaderboardContainer> pairs = (Map.Entry<String, LeaderboardContainer>) it.next();		    	
			    LeaderboardContainer arr = pairs.getValue();			    	
			    	
	    		String feed = arr.getContest().getFeed();
	    		codeID = pairs.getKey();
	    		// ADD THE LEADERBOARD USERS
	    		for(int i = 0; i<arr.getLeaderboard().size(); i++){
	    			if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){	    				
	    				Leaders l = new Leaders(arr.getLeaderboard().get(i).getUser().getUsersmallimg(),
			    				arr.getLeaderboard().get(i).getUser().getNickname(), arr.getLeaderboard().get(i).getScore().toString(),
			    				feed,arr.getLeaderboard().get(i).getPos().toString(), false);
		    			leaderslist.add(l);
		    		}else if(leader_kind.equals("ALLIANCES")){
		    			Leaders l = new Leaders(null,
			    				arr.getLeaderboard().get(i).getAlliance().getName(), arr.getLeaderboard().get(i).getScore().toString(),
			    				feed,arr.getLeaderboard().get(i).getPos().toString(), false);
			    		leaderslist.add(l);		    			
		    		}
	    			
					// ADD THE USER EXT TO THE USERS ARRAY	    			
	    			if(leader_kind == null || leader_kind.equals("FRIENDS")){		    		
	    				usersExts.add(arr.getLeaderboard().get(i).getUser().getId());
						usersNicks.add(arr.getLeaderboard().get(i).getUser().getNickname());
	    			}else if(leader_kind.equals("ALLIANCES")){
		    			// ADD THE ALLIANCE ID TO THIS ARRAY AND USE IT IN THE ON ITEM CLICK
		    			usersExts.add(arr.getLeaderboard().get(i).getAlliance().getId());
		    		}
		    	}	 	
		    	count++;
		    }
		    currentPageRows++;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {			
		try {
			if(leader_kind == null || leader_kind.equals("FRIENDS") || leader_kind.equals("CLOSEST")){
				if(listView.getHeaderViewsCount() > 0)
					position--;
				try {
					if(player != null && player.getUser() != null){
						if(!player.getUser().getId().equals(usersExts.get(position))){			    						
							showOptionAlert(position);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(leader_kind.equals("ALLIANCES")){
				if(player != null && player.getUser() != null){
					UserAlliance ua = new UserAlliance(currentContext, UserAlliance.SHOW_ALLIANCE, usersExts.get(position-1));
					ua.show();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private View setCurrentUserRow(){
		View header1 = getLayoutInflater().inflate(R.layout.leaderboarditem, null, false);
		header1.findViewById(R.id.alliance).setVisibility(View.GONE); // alliance in user not implemented now
		
		TextView p = (TextView) header1.findViewById(R.id.position);
		p.setTextColor(Color.WHITE);
		p.setText(currentUser.position);		
		TextView nickname = (TextView) header1.findViewById(R.id.nickname);
		nickname.setText(currentUser.name);
		TextView score = (TextView) header1.findViewById(R.id.score);
		
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());		
		String formattedScore = nf.format(Double.parseDouble(currentUser.score));
		
		if(currentUser.feed == null)
			score.setText(currentContext.getString(R.string.leadScore)+"\n"+formattedScore);
		else			
			score.setText(currentUser.feed+":\n"+formattedScore);
		
		header1.findViewById(R.id.image).setVisibility(LinearLayout.GONE);
		header1.findViewById(R.id.whiteline).setVisibility(LinearLayout.GONE);
		try {
			if(currentUser.imageUrl != null){
				ImageView imageView = new ImageView(currentContext);				
				imageView.setTag(currentUser.imageUrl);
				int size = (int) (50 * currentContext.getResources().getDisplayMetrics().density + 0.5f);
				imageView.setLayoutParams(new LinearLayout.LayoutParams(size,size));				
				imageManager.displayImage(currentUser.imageUrl, currentContext, imageView);
				
				LinearLayout a = (LinearLayout)header1.findViewById(R.id.item);
				a.addView(imageView,0);
			}else{
				header1.findViewById(R.id.item).setPadding((int)(ratio * 5), (int)(ratio * 5), 0, (int)(ratio * 5));
			}
		}catch(Exception e){ }
		
		LinearLayout positionView = (LinearLayout) header1.findViewById(R.id.post);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,(int)(ratio * 35));
		params.setMargins(0, 0, (int)(ratio*10), 0);
		positionView.setLayoutParams(params);
		positionView.setPadding((int)(ratio * 5), 0, (int)(ratio * 5), 0);
		positionView.setMinimumHeight((int)(ratio * 35));
		positionView.setMinimumWidth((int)(ratio * 35));
	
		positionView.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT));
		header1.findViewById(R.id.item).setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.LIGHT_GRAY_GRADIENT));

		LinearLayout spacer = (LinearLayout) header1.findViewById(R.id.space);
		spacer.setVisibility(LinearLayout.VISIBLE);

		return header1;
	}
	
	public void showOptionAlert(final int row){
		
		final CharSequence[] items = {current.getContext().getString(R.string.leadSendChall), 
				current.getContext().getString(R.string.leadViewProfile), current.getContext().getString(R.string.leadAddFriend)};
		AlertDialog.Builder builder = new AlertDialog.Builder(current.getContext());
		builder.setTitle(current.getContext().getString(R.string.vgoodchoosedialog));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {		    	
		        if(item == 0){ 
		        	try {		        		
		        		ChallengeCreate cc = new ChallengeCreate(current.getContext(), ChallengeCreate.SHOW_ENTRY, null, usersExts.get(row));
		        		cc.codeID = codeID;
		        		cc.show();		        		
		        	}catch(Exception e){e.printStackTrace();}
		        }else if(item == 1){ 
		        	try {
		        		UserProfile userProfile = new UserProfile(getContext(),usersExts.get(row));		        		
		    			userProfile.show();
		        	}catch(Exception e){e.printStackTrace();}
		        }else if(item == 2){ 
		        	try {
		        		Friends f = new Friends(current.getContext(),current,0,0);
		        		f.friendshipThread(usersExts.get(row), "invite");
		        	}catch(Exception e){e.printStackTrace();}		        	
		        }
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
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
	
	private LinearLayout loadingFooter(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));				
		pb.setLayoutParams(params);	            	            
        LinearLayout loading = new LinearLayout(currentContext);
        loading.addView(pb);
        loading.setPadding(0, 10, 0, 10);
        loading.setGravity(Gravity.CENTER);
        loading.setTag(2000);
        return loading;
	}
	
	public class Leaders {
		String imageUrl;
		String name;
		String score;
		String feed;
		String position;
		boolean isCurrentUser;
		
		public Leaders(String image, String name, String score, String feed,String position, boolean isCurrentUser) {
			this.imageUrl 		= image;
			this.name 			= name;
			this.score 			= score;
			this.feed 			= feed;
			this.position 		= position;
			this.isCurrentUser 	= isCurrentUser;
		}
	} 

	@Override
	public void onBackPressed() {		
		// STOPPING IMAGE MANAGER THREADS
		try {
			if(adapter != null && adapter.getImageManager() != null)
				adapter.getImageManager().interrupThread();
			if(imageManager != null)
				imageManager.interrupThread();
		}catch (Exception e){
			e.printStackTrace();
		}		
		super.onBackPressed();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)  {
		try {
			if(leader_kind != null && leader_kind.equals("CLOSEST"))
				return;
			
			if(!isLoading && !hasReachedEnd){
				if(firstVisibleItem != 0 && (firstVisibleItem > totalItemCount - visibleItemCount-1)){
					isLoading=true;
		            listView.addFooterView(loadingFooter());
					startLoadingMore();
				}		
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
	}
	
	Handler UIHandler = new Handler(){};
}
