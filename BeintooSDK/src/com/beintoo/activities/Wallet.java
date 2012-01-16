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


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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


import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.main.Beintoo;

import com.beintoo.vgood.VgoodSendToFriend;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.Vgood;

public class Wallet extends Dialog implements OnItemClickListener, OnScrollListener{

	private ListView listView;	
	private WalletListAdapter adapter;
	private ArrayList<ListWalletItem> vgoodslist;
	
	private Player p; 
	
	private final int MAX_REQ_ROWS = 10;
	private int currentPageRows = 0;   	
	private int currentScrollPos = 0;
	
	@SuppressWarnings("unused") // used for pagination, not yet implemented on API
	private boolean isLoading = false;
	@SuppressWarnings("unused") // used for pagination, not yet implemented on API
	private boolean hasReachedEnd = false;
	
	private Dialog current;		
	private String vgoodExtId;
	private User[] friends;
	private List<Vgood> vgoods;
	private final double ratio;
			
	private final int CONNECTION_ERROR 	= 2;
	private final int SEND_TO_FRIEND 	= 3;
	private final int NOT_REDEEMED 		= 1;
	private final int REDEEMED 			= 2;
	private int currentSection 			= NOT_REDEEMED;
	private View selectedRow;
	
	private boolean onlyconverted = false;
	
	public Wallet(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.walletlist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		
		// SET DIALOG TITLE
		TextView title = (TextView)findViewById(R.id.dialogTitle);
		title.setText(R.string.wallet);
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		
		// Setup layouts
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		// Setup buttons
		final BeButton b = new BeButton(ctx);
		Button toConvert = (Button) findViewById(R.id.toConvertGoods);		
		toConvert.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
		toConvert.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				currentSection = NOT_REDEEMED;
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
					onlyconverted = false;
     				startFirstLoading();
			}            	
        });
		
		Button converted = (Button) findViewById(R.id.convertedGoods);
		converted.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		converted.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				currentSection = REDEEMED;
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				onlyconverted = true;
				startFirstLoading();            	            	
			}
        });
		
		try {
	    	p = Current.getCurrentPlayer(getContext());	    	    	
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    
	    LinearLayout content = (LinearLayout) findViewById(R.id.goodcontent);		
		content.addView(progressLoading());  
	    
	    listView = (ListView) findViewById(R.id.listView);        
        listView.setCacheColorHint(0);
	    listView.setOnScrollListener(this);
	    listView.setOnItemClickListener(this);
	    listView.addHeaderView(new View(current.getContext()), null, false); // WITHOUT HEADER THE FOOTER IS NOT SHOWN
	    startFirstLoading();
	    
	}
	
	@Override
	public void onBackPressed() {
		if(adapter != null && adapter.imageManager != null)
			adapter.imageManager.interrupThread();
		
		super.onBackPressed();
	}

	public void startFirstLoading (){		
		vgoods = new ArrayList<Vgood>();	
		vgoodslist = new ArrayList<ListWalletItem>();
	    hasReachedEnd = false;
	    listView.setVisibility(LinearLayout.GONE);
	    findViewById(R.id.goodcontent).findViewWithTag(1000).setVisibility(LinearLayout.VISIBLE);			  
		loadData(currentPageRows, MAX_REQ_ROWS);							
	}
	
	/*public void loadMore(){		
		currentPageRows++;
		loadData(currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS);		
	}*/
	
	public void reloadData(){
		vgoodslist = new ArrayList<ListWalletItem>();
		vgoods = new ArrayList<Vgood>();
		
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
    				BeintooVgood w = new BeintooVgood();            				
					// GET THE CURRENT LOGGED PLAYER
					final List<Vgood> tmp = w.showByPlayer(p.getGuid(), null, onlyconverted);
					vgoods.addAll(tmp);	
			
					if(tmp.size() == 0){
						hasReachedEnd = true;
					}
										
					for(int i = start; i < vgoods.size(); i++){
						String title = null;
						String date = null;
						String img = null;
						if(vgoods.get(i) != null){
							title = vgoods.get(i).getDescriptionSmall();
							date = vgoods.get(i).getEnddate();
							img = vgoods.get(i).getImageSmallUrl();
						}
						
						ListWalletItem item = new ListWalletItem(img, title, date);
						vgoodslist.add(item); 														
					}			
					
					
					UIhandler.post(new Runnable(){
						@Override
						public void run() {
							try {
								if(start == 0){
									adapter = new WalletListAdapter(current.getContext(), vgoodslist);
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
	
	/* PAGINATION NOT IMPLEMENTED IN API'S YET */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)  {
		/*try {			
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
		}*/
	}	
	
	/*private LinearLayout loadingFooter(){
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
	}*/
	
	private void showConfirmAlert(final int row){
		final Vgood v = vgoods.get(row-1);
		
		final CharSequence[] items = {current.getContext().getString(R.string.vgoodgetcoupondialog), 
				current.getContext().getString(R.string.vgoodsendgiftdialog)};
		vgoodExtId = v.getId();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(current.getContext());
		builder.setTitle(current.getContext().getString(R.string.vgoodchoosedialog));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        if(item == 0){ // GET COUPON
		        	try {
		        		BeintooBrowser getVgood = new BeintooBrowser(current.getContext(),v.getGetRealURL());
		        		Beintoo.currentDialog = getVgood;
		        		getVgood.show();		        				         		
		        	}catch(Exception e){e.printStackTrace();}
		        }else if(item == 1){ // SEND AS A GIFT
		        	final ProgressDialog  loading = ProgressDialog.show(getContext(), "", getContext().getString(R.string.friendLoading),true);
		        	new Thread(new Runnable(){      
			    		public void run(){
			    			try{     						    				
			    				BeintooUser u = new BeintooUser();
			    				Player p = Current.getCurrentPlayer(current.getContext());
			    				friends = u.getUserFriends(p.getUser().getId(), null);
			    				UIhandler.sendEmptyMessage(SEND_TO_FRIEND);
			    			}catch (Exception e){
			    				e.printStackTrace();
			    			}
			    			loading.dismiss();
			    		}
					}).start();	
		        }
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void resetButtons(){
		Button converted = (Button) findViewById(R.id.convertedGoods);		
		converted.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));	
		Button toConvert = (Button) findViewById(R.id.toConvertGoods);
		toConvert.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));
	}
	
	
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {	
			  switch(msg.what){				 
				  case CONNECTION_ERROR:					  
					  ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , current.getContext(), null);
				  break;
				  case SEND_TO_FRIEND:
				  		VgoodSendToFriend f = new VgoodSendToFriend(getContext(), VgoodSendToFriend.OPEN_FRIENDS_FROM_WALLET, friends);
						f.previous = current;
						f.rowToRemove = selectedRow;			  		
						f.vgoodID = vgoodExtId;
						f.show();
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
		if(currentSection == NOT_REDEEMED){
			showConfirmAlert(position);	
		}else{
			BeintooBrowser bb = new BeintooBrowser(current.getContext(),vgoods.get(position-1).getShowURL());
			bb.show();
		}
	}
	
	public class ListWalletItem {
		String imgUrl;
		String title;
		String date;
		
		public ListWalletItem(String imgUrl, String title, String date) {
			super();
			this.imgUrl = imgUrl;
			this.title = title;
			this.date = date;			
		}
	}
	
}
