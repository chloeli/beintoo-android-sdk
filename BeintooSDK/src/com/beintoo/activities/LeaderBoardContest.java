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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.EntryCouplePlayer;
import com.beintoo.wrappers.Player;

public class LeaderBoardContest extends Dialog implements OnClickListener{
	Dialog current;
	Context currentContext;
	final double ratio;
	Map<String, List<EntryCouplePlayer>> leader;
	
	public LeaderBoardContest(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);
		setContentView(R.layout.contestselection);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		currentContext = ctx;
		
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.leaderboard);

		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));

		
		try {			
			showLoading();
			startLoading();
		}catch(Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx,e);}	
	 
		final BeButton b = new BeButton(ctx);
		Button general = (Button) findViewById(R.id.generaleader);
		
		//general.setWidth(ctx.getApplicationContext().getResources().getDisplayMetrics().widthPixels/2);		
		general.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
		general.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooApp app = new BeintooApp();							
							leader = app.TopScore(null, 0);
							UIhandler.sendEmptyMessage(0);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            		}
				}).start();
			}
        });
		
		Button friends = (Button) findViewById(R.id.friendsleader);
		//friends.setWidth(ctx.getApplicationContext().getResources().getDisplayMetrics().widthPixels/2);
		friends.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		friends.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooApp app = new BeintooApp();				
							Player player = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", currentContext));
							leader = app.TopScoreByUserExt(null, 0, player.getUser().getId());							
							UIhandler.sendEmptyMessage(0); 
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            		}
				}).start();
			}
        });
	}
	
	public void startLoading (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				BeintooApp app = new BeintooApp();							
    				leader = app.TopScore(null, 0);					
					UIhandler.sendEmptyMessage(0);
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();
	}
	
	public void loadContestTable(){
		
		TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
		table.setColumnStretchable(1, true);
		table.removeAllViews();
		
		BeButton b = new BeButton(getContext());
		
		final ArrayList<View> rowList = new ArrayList<View>();
		Iterator<?> it = leader.entrySet().iterator();
		int count = 0;
	    while (it.hasNext()) {
	    	@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<EntryCouplePlayer>> pairs = (Map.Entry<String, ArrayList<EntryCouplePlayer>>) it.next();
	    	List<EntryCouplePlayer> arr = pairs.getValue();	
	    	
	    	if(arr.get(0).getEntry().getPlayerScore().get(pairs.getKey()).getContest().isPublic() == true){
		    	TableRow row = createRow(arr.get(0).getEntry().getPlayerScore().get(pairs.getKey()).getContest().getName(), getContext());
		    	
		    	if(count % 2 == 0)
		    		row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				else
					row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				
		    	TableLayout.LayoutParams tableRowParams=
	    		  new TableLayout.LayoutParams
	    		  (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
	    		row.setLayoutParams(tableRowParams);
		    	row.setId(count);
				rowList.add(row);
				View spacer = createSpacer(getContext(),1,1);
				spacer.setId(-100);
				rowList.add(spacer);
				View spacer2 = createSpacer(getContext(),2,1);
				spacer2.setId(-100);
				rowList.add(spacer2);
		    	count++;	
	    	}
	    }
	    
	    for (View row : rowList) {
		      row.setPadding(0, 0, 0, 0);	      
		      //row.setBackgroundColor(Color.argb(200, 51, 51, 51));
		      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
		    	  row.setOnClickListener(this);
		      table.addView(row);
		}
	}
	
	public TableRow createRow(String txt, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER_VERTICAL);
		  
		  TextView text = new TextView(activity);
		  text.setText(txt);
		  text.setPadding((int)(ratio*10), 0, 0, 0);
		  text.setTextColor(Color.parseColor("#545859"));
		  text.setTextSize(14);
		  text.setGravity(Gravity.CENTER_VERTICAL);
		  text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				  RelativeLayout.LayoutParams.FILL_PARENT));
		  
		  RelativeLayout main = new RelativeLayout(row.getContext());
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				  RelativeLayout.LayoutParams.FILL_PARENT);
		  params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		  main.setLayoutParams(params);
		  
		  ImageView arrow = new ImageView(activity);
		  arrow.setImageResource(R.drawable.barrow);
		  arrow.setLayoutParams(params);
		  arrow.setPadding(0,0,(int)(ratio * 5),0);
		  
		  main.addView(arrow, params);
		  main.addView(text);
		  
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 35)));
		  
		   
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
		
	public void resetButtons(){
		Button general = (Button) findViewById(R.id.generaleader);		
		general.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));	
		Button friends = (Button) findViewById(R.id.friendsleader);
		friends.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));
	}
	
	// CALLED WHEN THE USER SELECT A USER IN THE TABLE
	public void onClick(View v) {
		final int selectedRow = v.getId();
		//final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
		/*new Thread(new Runnable(){      
    		public void run(){
    			try{ */     				
    				PreferencesHandler.saveString("selectedContest", ""+selectedRow, currentContext);
    				UIhandler.sendEmptyMessage(1);
    			/*}catch (Exception e){
    				e.printStackTrace();
    			}
    			//dialog.dismiss();
    		}
		}).start(); */		
	}
	
	private void showLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int)(100*ratio), 0, 0);		
		TableRow row = new TableRow(getContext());
		row.setLayoutParams(params);
		row.setGravity(Gravity.CENTER);
		row.addView(pb);
		TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
		table.setColumnStretchable(0, false);
		table.removeAllViews();
		table.addView(row);
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == 0)
				  loadContestTable();
			  else if(msg.what == 1){
				  LeaderBoard leaderboard = new LeaderBoard(currentContext,leader);
				  leaderboard.show();
			  }
			  super.handleMessage(msg);
		  }
	};
	
}
