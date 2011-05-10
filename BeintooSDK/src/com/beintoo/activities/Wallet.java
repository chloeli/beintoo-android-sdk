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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.Vgood;

public class Wallet extends Dialog implements OnClickListener{
	static Dialog current;
	
	Vgood [] vgood;
	
	final double ratio;
	
	private final int OPEN_BROWSER = 1;
	private final int LOAD_TABLE = 2;
	
	public Wallet(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.wallet);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		
		// SET DIALOG TITLE
		TextView title = (TextView)findViewById(R.id.dialogTitle);
		title.setText(R.string.wallet);
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		
		// SETTING UP BUTTONS
		final BeButton b = new BeButton(ctx);
		Button toConvert = (Button) findViewById(R.id.toConvertGoods);		
		toConvert.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
		toConvert.setOnClickListener(new Button.OnClickListener(){
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
            				BeintooVgood newvgood = new BeintooVgood();            				
            				// GET THE CURRENT LOGGED PLAYER
            				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
            				vgood = newvgood.showByUser(p.getUser().getId(), null, false);
            				UIhandler.sendEmptyMessage(LOAD_TABLE);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            		}
				}).start();
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
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
            				BeintooVgood newvgood = new BeintooVgood();            				
            				// GET THE CURRENT LOGGED PLAYER
            				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
            				vgood = newvgood.showByUser(p.getUser().getId(), null, true);
            				UIhandler.sendEmptyMessage(LOAD_TABLE);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            		}
				}).start();
			}
        });
		
		
		showLoading();
		
		startLoading();
		
	}
	
	private void startLoading (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				BeintooVgood bv = new BeintooVgood();
    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
    				vgood = bv.showByUser(p.getUser().getId(), null, false);
    				UIhandler.sendEmptyMessage(LOAD_TABLE);
    			}catch (Exception e){}
    		}
		}).start();	
	}
	
	public void loadWallet(){
		try {			
			if(vgood.length > 0)
				prepareWallet();
			else { // NO VGOODS SHOW A MESSAGE
				TextView noGoods = new TextView(getContext());
				noGoods.setText(getContext().getString(R.string.walletNoGoods));
				noGoods.setTextColor(Color.GRAY);
				noGoods.setPadding(20,15,0,0);						
				TableLayout table = (TableLayout) findViewById(R.id.table);
				table.removeAllViews();
				table.addView(noGoods);
			}
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , current.getContext(),e);}
	}
	
	public void prepareWallet(){
		TableLayout table = (TableLayout) findViewById(R.id.table);

		table.removeAllViews();
		
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	    for (int i = 0; i < vgood.length; i++){
	    	
    		final LoaderImageView image = new LoaderImageView(getContext(), vgood[i].getImageUrl(),(int)(ratio *80),(int)(ratio *80));
    		
    		TableRow row = createRow(image, vgood[i].getName(),vgood[i].getEnddate(), table.getContext());
			row.setId(count);
			rowList.add(row);
			BeButton b = new BeButton(getContext());
			if(count % 2 == 0)
	    		row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
	    	count++;
	    }
	    
	    for (View row : rowList) {
		      row.setPadding(0, 0, 0, 0);	
		      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
		    	  row.setOnClickListener(this);
		      table.addView(row);
		}
	}
	
	public TableRow createRow(View image, String name, String end,  Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding((int)(ratio*10), 0, 5, 0);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);

		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  TextView nameView = new TextView(activity);
		  nameView.setText(name);
		  nameView.setMaxLines(2);
		  nameView.setPadding(5, 0, 0, 0);
		  nameView.setTextColor(Color.parseColor("#545859"));
		  nameView.setTextSize(14);
		  nameView.setMaxWidth(50);
		  TextView enddate = new TextView(activity);
		  try { // try catch for SimpleDateFormat parse			  
			  SimpleDateFormat curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
			  curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));			  
			  Date endDate = curFormater.parse(end);			  
			  curFormater.setTimeZone(TimeZone.getDefault());
			  
			  enddate.setText(activity.getString(R.string.challEnd)+" "+DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(endDate));
			  enddate.setTextSize(12);
			  enddate.setMaxLines(2);
			  enddate.setPadding(5, 0, 0, 0);
			  enddate.setTextColor(Color.parseColor("#787A77"));
		  } catch (Exception e){e.printStackTrace();}
		  
		  row.addView(image);
		  main.addView(nameView);
		  main.addView(enddate);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 90)));
		  
		  return row;
	}
		
	private View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color == 1)
			  spacer.setBackgroundColor(Color.parseColor("#8F9193"));
		  else if(color == 2)
			  spacer.setBackgroundColor(Color.WHITE);

		  return spacer;
	}

	public void onClick(View v) {
		final int selectedRow = v.getId();
		final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				PreferencesHandler.saveString("openUrl", vgood[selectedRow].getShowURL(), current.getContext());		
    				UIhandler.sendEmptyMessage(OPEN_BROWSER);
    			}catch (Exception e){
    			}
    			dialog.dismiss();
    		}
		}).start();	
	}
	
	public void resetButtons(){
		Button converted = (Button) findViewById(R.id.convertedGoods);		
		converted.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));	
		Button toConvert = (Button) findViewById(R.id.toConvertGoods);
		toConvert.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));
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
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		table.addView(row);
	}
	
	private Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  switch(msg.what){
			  	case OPEN_BROWSER:
			  		BeintooBrowser bb = new BeintooBrowser(current.getContext());
					bb.show();						        
			  	break;
			  	case LOAD_TABLE:
			  		loadWallet();
				break;			  	
			  }
			super.handleMessage(msg);
		  }
	};
	
}
