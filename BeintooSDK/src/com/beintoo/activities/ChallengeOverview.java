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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DpiPxConverter;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.wrappers.Challenge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChallengeOverview extends Dialog{
	private Challenge reqChallenge = null;
	private Dialog current;
	private final int PENDING = 0;
	private final int ACCEPTED = 1;
	private final int ENDED = 2;
	private final View clickedRow;
	private final ImageManager imageManager;
	
	public final static int DIRECTION_DEST = 1;
	public final static int DIRECTION_SOURCE = 2;
	public final static int CHALLENGE_48_HOURS = 3;
	
	public ChallengeOverview(Context context, Challenge requested, View clickedRow, int typeOfChallenge, int challengestatus) {
		super(context);
		reqChallenge = requested;
		current = this;
		this.clickedRow = clickedRow;		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.challengecreate);
		
		findViewById(R.id.imageView1).setVisibility(View.GONE);
		findViewById(R.id.imageView2).setVisibility(View.GONE);
		findViewById(R.id.imageView3).setVisibility(View.GONE);
		imageManager = new ImageManager(context);
		
		if(reqChallenge != null){
			try{
				LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.challengedialog, null, false);				
				BDrawableGradient ltgray = new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 113),BDrawableGradient.LIGHT_GRAY_GRADIENT);
				int p = DpiPxConverter.pixelToDpi(context, 15);				
				if(typeOfChallenge == DIRECTION_DEST){
					findViewById(R.id.row2).setVisibility(View.GONE);
					findViewById(R.id.row3).setVisibility(View.GONE);			
					((LinearLayout)findViewById(R.id.row1create)).addView(content, 
							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					findViewById(R.id.row1create).setVisibility(View.VISIBLE);
					findViewById(R.id.row1).setBackgroundDrawable(ltgray);					
					findViewById(R.id.row1textcontent).setPadding(0, p, p, p);
					((TextView)findViewById(R.id.bigtext)).setText("VS");
				}else if(typeOfChallenge == DIRECTION_SOURCE){
					findViewById(R.id.row1).setVisibility(View.GONE);
					findViewById(R.id.row3).setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.row2create)).addView(content, 
							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					findViewById(R.id.row2create).setVisibility(View.VISIBLE);
					findViewById(R.id.row2).setBackgroundDrawable(ltgray);					
					findViewById(R.id.row2textcontent).setPadding(0, p, p, p);
					((TextView)findViewById(R.id.bigtext)).setText("VS");
				}else if(typeOfChallenge == CHALLENGE_48_HOURS){
					findViewById(R.id.row1).setVisibility(View.GONE);
					findViewById(R.id.row2).setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.row3create)).addView(content, 
							new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					findViewById(R.id.row3create).setVisibility(View.VISIBLE);
					findViewById(R.id.row3).setBackgroundDrawable(ltgray);					
					findViewById(R.id.row3textcontent).setPadding(0, p, p, p);
					((TextView)findViewById(R.id.bigtext)).setText("48h");
				}
				
				BDrawableGradient gray = new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 95),BDrawableGradient.GRAY_GRADIENT);
				ltgray = new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 58),BDrawableGradient.LIGHT_GRAY_GRADIENT);
				findViewById(R.id.details).setBackgroundDrawable(gray);
				findViewById(R.id.you).setBackgroundDrawable(ltgray);
				findViewById(R.id.other).setBackgroundDrawable(ltgray);
				((Button)findViewById(R.id.accept)).setShadowLayer(0.1f, 0, -1.0f, Color.BLACK);
				((Button)findViewById(R.id.refuse)).setShadowLayer(0.1f, 0, -1.0f, Color.BLACK);
				BeButton b = new BeButton(context);
				Button accept = (Button)findViewById(R.id.accept);
				Button refuse = (Button)findViewById(R.id.refuse);
				accept.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
				refuse.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BAR_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 37), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));				
								
				ImageView img1 = ((ImageView)findViewById(R.id.fromimg));
				ImageView img2 = ((ImageView)findViewById(R.id.toimg));
				img1.setTag(reqChallenge.getPlayerFrom().getUser().getUserimg());
				img2.setTag(reqChallenge.getPlayerTo().getUser().getUserimg());
				imageManager.displayImage(reqChallenge.getPlayerFrom().getUser().getUserimg(), context, img1);
				imageManager.displayImage(reqChallenge.getPlayerTo().getUser().getUserimg(), context, img2);
								
				TextView fromname 	= (TextView) findViewById(R.id.fromname);				
				TextView toname 	= (TextView) findViewById(R.id.toname);								
				fromname.setText(reqChallenge.getPlayerFrom().getUser().getNickname());				
				toname.setText(reqChallenge.getPlayerTo().getUser().getNickname());
								
				((TextView)findViewById(R.id.contest)).setText(Html.fromHtml("Contest: <font color=\"#000000\">"+reqChallenge.getContest().getName()+"</font>"));
				((TextView)findViewById(R.id.amountbet)).setText(Html.fromHtml(context.getString(R.string.challBet)
						+" <font color=\"#000000\">"+reqChallenge.getPrice()+"</font> beDollars"));
				((TextView)findViewById(R.id.amountwin)).setText(Html.fromHtml(context.getString(R.string.challCanWin)
						+" <font color=\"#000000\">"+reqChallenge.getPrize()+"</font> beDollars"));
				if(reqChallenge.getTargetScore() != null){
					((TextView)findViewById(R.id.goalpoints)).setText(Html.fromHtml(context.getString(R.string.challPointsTarget)
							+" <font color=\"#000000\">"+reqChallenge.getTargetScore()+"</font>"));
				}else{
					findViewById(R.id.goalpoints).setVisibility(View.GONE);
				}
				
				if(challengestatus == PENDING){
					findViewById(R.id.buttons).setVisibility(View.VISIBLE);
					accept.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							respondChallenge(reqChallenge.getPlayerTo().getUser().getId(),
									reqChallenge.getPlayerFrom().getUser().getId(), "ACCEPT", reqChallenge.getContest().getCodeID());
						}					
					});
					refuse.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							respondChallenge(reqChallenge.getPlayerTo().getUser().getId(),
									reqChallenge.getPlayerFrom().getUser().getId(), "REFUSE", reqChallenge.getContest().getCodeID());
						}					
					});
				}else if(challengestatus == ACCEPTED){
					String feed = context.getString(R.string.challPoints);
					if(reqChallenge.getContest() != null && reqChallenge.getContest().getFeed() != null)
						feed = reqChallenge.getContest().getFeed();
					((TextView)findViewById(R.id.frompoints)).setText(Html.fromHtml(feed+": <font color=\"#000000\">"+reqChallenge.getPlayerFromScore().toString()+"</font>"));
					((TextView)findViewById(R.id.frompoints)).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.topoints)).setText(Html.fromHtml(feed+": <font color=\"#000000\">"+reqChallenge.getPlayerToScore().toString()+"</font>"));
					((TextView)findViewById(R.id.topoints)).setVisibility(View.VISIBLE);
				}else if(challengestatus == ENDED){					
					if(reqChallenge.getWinner().getUser().getId().equals(reqChallenge.getPlayerFrom().getUser().getId()))
						((TextView)findViewById(R.id.fromwinner)).setVisibility(View.VISIBLE);
					else
						((TextView)findViewById(R.id.towinner)).setVisibility(View.VISIBLE);
					
					String feed = context.getString(R.string.challPoints);
					if(reqChallenge.getContest() != null && reqChallenge.getContest().getFeed() != null)
						feed = reqChallenge.getContest().getFeed();
					((TextView)findViewById(R.id.frompoints)).setText(Html.fromHtml(feed+": <font color=\"#000000\">"+reqChallenge.getPlayerFromScore().toString()+"</font>"));
					((TextView)findViewById(R.id.frompoints)).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.topoints)).setText(Html.fromHtml(feed+": <font color=\"#000000\">"+reqChallenge.getPlayerToScore().toString()+"</font>"));
					((TextView)findViewById(R.id.topoints)).setVisibility(View.VISIBLE);
				}
				
				if(challengestatus == ACCEPTED || challengestatus == ENDED){					
					SimpleDateFormat curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss", Locale.ENGLISH); 
					curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
					
					Date startDate = curFormater.parse(reqChallenge.getStartdate());
					Date endDate = curFormater.parse(reqChallenge.getEnddate());
					curFormater.setTimeZone(TimeZone.getDefault());
					TextView sDate = (TextView) findViewById(R.id.startdate);
					TextView eDate = (TextView) findViewById(R.id.enddate);
					sDate.setText(Html.fromHtml(context.getString(R.string.challStart)+ " <font color=\"#000000\">"+
							DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(startDate)+ "</font>"));					
					eDate.setText(Html.fromHtml(context.getString(R.string.challEnd)+ " <font color=\"#000000\">"+
							DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.getDefault()).format(endDate)+ "</font>"));	
					sDate.setVisibility(View.VISIBLE);
					eDate.setVisibility(View.VISIBLE);
				}				
			}catch(Exception e){e.printStackTrace();}
		}else{ // dismiss if error
			this.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {		
		// STOPPING IMAGE MANAGER THREADS	
		if(imageManager != null){
			imageManager.interrupThread();
		}
		super.onBackPressed();
	}
	
	private void respondChallenge (final String userExtFrom, final String userExtTo, final String action, final String codeID){
		current.dismiss();
		if(action.equals("ACCEPT"))
			MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.challAccepted),Gravity.BOTTOM);
		else
			MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.challRefused),Gravity.BOTTOM);
		clickedRow.setVisibility(View.GONE);
		
		new Thread(new Runnable(){      
    		public void run(){
    			try{             				
    				BeintooUser user = new BeintooUser();
    				user.challenge(userExtFrom, userExtTo, action, codeID); 
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    			
    		}
		}).start();
	}
}
