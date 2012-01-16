package com.beintoo.activities;


import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DpiPxConverter;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.wrappers.Challenge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChallengeCreate extends Dialog implements OnClickListener{
	private final Context context;
	private final Dialog current;
	public Dialog previous;
	
	public String challengeUserExt;
	public String codeID;
	private String userExtFrom = null, userExtTo = null;	
	private String nicknameTo = null;
	private Challenge prerequisite;
	
	/* VIEWS */
	public final static int SHOW_ENTRY = 1;
	public final static int SHOW_BET = 2;
	
	/* CHALLENGE TYPES */
	private final static int DIRECTION_DEST = 1;
	private final static int DIRECTION_SOURCE = 2;
	private final static int CHALLENGE_48_HOURS = 3;
	
	
	public ChallengeCreate(Context c, Integer kind, Integer challengeType, String challengeUserExt) {
		super(c);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);				
		this.context = c;
		this.current = this;
		this.challengeUserExt = challengeUserExt;
		setContentView(R.layout.challengecreate);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
			
		if(kind == SHOW_ENTRY){
			this.setupEntry();
		}else if(kind == SHOW_BET){			
			setupBet(challengeType);			
		}
	}
	
	private void setupEntry(){
		BDrawableGradient lightgray = new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 125),BDrawableGradient.LIGHT_GRAY_GRADIENT);		
		LinearLayout row1 = (LinearLayout) findViewById(R.id.row1);
		row1.setBackgroundDrawable(lightgray);
		row1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {				
				ChallengeCreate cc = new ChallengeCreate(context, ChallengeCreate.SHOW_BET, ChallengeCreate.DIRECTION_DEST, challengeUserExt);	
				cc.codeID = codeID;
				cc.previous = current;
				cc.show();
			}			
		});
		LinearLayout row2 = (LinearLayout) findViewById(R.id.row2);
		row2.setBackgroundDrawable(lightgray);
		row2.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {				
				ChallengeCreate cc = new ChallengeCreate(context, ChallengeCreate.SHOW_BET, ChallengeCreate.DIRECTION_SOURCE, challengeUserExt);	
				cc.codeID = codeID;
				cc.previous = current;
				cc.show();
			}			
		});
		LinearLayout row3 = (LinearLayout) findViewById(R.id.row3);
		row3.setBackgroundDrawable(lightgray);
		row3.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {				
				ChallengeCreate cc = new ChallengeCreate(context, ChallengeCreate.SHOW_BET, ChallengeCreate.CHALLENGE_48_HOURS, challengeUserExt);
				cc.codeID = codeID;
				cc.previous = current;
				cc.show();
			}			
		});
	}
	
	private void setupBet(final int challengeType){
		LinearLayout betContent = null;
		
		if(challengeType == ChallengeCreate.DIRECTION_DEST){
			findViewById(R.id.row2).setVisibility(View.GONE);
			findViewById(R.id.row3).setVisibility(View.GONE);			
			betContent = (LinearLayout) findViewById(R.id.row1create);
			betContent.addView(createChallengeView(context));			
			((TextView)findViewById(R.id.bigtext)).setText("VS");
			((TextView)findViewById(R.id.smalltext)).setText("VS");
		}else if(challengeType == ChallengeCreate.DIRECTION_SOURCE){
			findViewById(R.id.row1).setVisibility(View.GONE);
			findViewById(R.id.row3).setVisibility(View.GONE);
			betContent = (LinearLayout) findViewById(R.id.row2create);
			betContent.addView(createChallengeView(context));
			((TextView)findViewById(R.id.bigtext)).setText("VS");
			((TextView)findViewById(R.id.smalltext)).setText("VS");
		}else if(challengeType == ChallengeCreate.CHALLENGE_48_HOURS){
			findViewById(R.id.row1).setVisibility(View.GONE);
			findViewById(R.id.row2).setVisibility(View.GONE);
			betContent = (LinearLayout) findViewById(R.id.row3create);
			betContent.addView(createChallengeView(context));
			((TextView)findViewById(R.id.bigtext)).setText("48h");
			((TextView)findViewById(R.id.smalltext)).setText("48h");
			((TextView)findViewById(R.id.textpoints)).setText(context.getString(R.string.chall48subtitle));			
			findViewById(R.id.editpoints).setVisibility(View.GONE);
			findViewById(R.id.points).setVisibility(View.GONE);
		}
						
		loadPrerequisite(challengeType);
		betContent.setVisibility(View.VISIBLE);
		
		Button bt = (Button) findViewById(R.id.sendbet);
		bt.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try{ 
					sendChallenge(challengeType);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}			
		});
	}
	
	private LinearLayout createChallengeView(Context c){
		LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.challegebetrow, null, false);
		content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		BDrawableGradient gray = new BDrawableGradient(0,DpiPxConverter.pixelToDpi(c, 225),BDrawableGradient.GRAY_GRADIENT);
		content.setBackgroundDrawable(gray);
		content.findViewById(R.id.sendc).setBackgroundDrawable(new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 223),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		Button sendbet = (Button) content.findViewById(R.id.sendbet);
		sendbet.setShadowLayer(0.1f, 0, -1.0f, Color.BLACK);		
		sendbet.setBackgroundDrawable(
	    		new BeButton(context).setPressedBackg(
			    		new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 45), BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 45), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,DpiPxConverter.pixelToDpi(context, 45), BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
		return content;		
	}
	
	private void loadPrerequisite(final int challengeType){

		findViewById(R.id.betcompile).setVisibility(View.GONE);
		LinearLayout betrow = (LinearLayout) findViewById(R.id.betrow);
				
		final ProgressBar pb = new ProgressBar(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);
		params.setMargins(DpiPxConverter.pixelToDpi(context, 10), DpiPxConverter.pixelToDpi(context, 10), DpiPxConverter.pixelToDpi(context, 10), DpiPxConverter.pixelToDpi(context, 10));
		betrow.setGravity(Gravity.CENTER);
		pb.setLayoutParams(params);	    
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		pb.setId(100);		
		betrow.addView(pb);
		 
		final ImageManager imageManager = new ImageManager(context);
		new Thread(new Runnable(){     					
    		public void run(){ 
    			try{   	
    				
    				userExtFrom = Current.getCurrentPlayer(context).getUser().getId();
    				userExtTo = challengeUserExt;    				
    				BeintooUser bu = new BeintooUser();
    				prerequisite = bu.challenge(userExtFrom, userExtTo, "PREREQUISITE", codeID);
    				
    				nicknameTo = prerequisite.getPlayerTo().getUser().getNickname();
    				
    				final TextView maxbetfrom = (TextView)findViewById(R.id.maxbetfrom);
    				final TextView maxbetto = (TextView)findViewById(R.id.maxbetto);
    				final TextView from = (TextView)findViewById(R.id.from);
    				final TextView to = (TextView)findViewById(R.id.to);
    				final LinearLayout betrow = (LinearLayout) findViewById(R.id.betrow);
    				final ImageView img1 = (ImageView) findViewById(R.id.fromimg);
					final ImageView img2 = (ImageView) findViewById(R.id.toimg);	
					final TextView feed = (TextView)findViewById(R.id.points);
					final EditText bedollars = (EditText)findViewById(R.id.editbet);
					final EditText points = (EditText)findViewById(R.id.editpoints);
					final Integer playerToMaxBeDollars = prerequisite.getPlayerTo().getUser().getBedollars().intValue();
					final Integer playerFromMaxBeDollars = prerequisite.getPlayerFrom().getUser().getBedollars().intValue();
					final Integer absoluteMax = (playerFromMaxBeDollars > playerToMaxBeDollars) ? playerToMaxBeDollars : playerFromMaxBeDollars;
					
    				UIhandler.post(new Runnable(){
						@Override
						public void run() {							
							try {
								maxbetfrom.setText(Html.fromHtml(context.getString(R.string.challmaxbet)+" <b><font color=\"#000000\">" +										
										prerequisite.getPlayerFrom().getUser().getBedollars().intValue()+"</font></b>"));							
								maxbetto.setText(Html.fromHtml(context.getString(R.string.challmaxbet)+" <b><font color=\"#000000\">"+
										prerequisite.getPlayerTo().getUser().getBedollars().intValue()+"</font></b>"));
								from.setText(prerequisite.getPlayerFrom().getUser().getNickname());
								to.setText(prerequisite.getPlayerTo().getUser().getNickname());		
								bedollars.setHint("max "+absoluteMax);
								points.setHint("min "+prerequisite.getPlayerTo().getPlayerScore().get(codeID).getBalance().intValue());
								
								if(prerequisite.getContest() != null && prerequisite.getContest().getFeed() != null)
									feed.setText(prerequisite.getContest().getFeed());
								
								if(challengeType == ChallengeCreate.DIRECTION_DEST){
									Integer minScore = 1;
									if(prerequisite.getPlayerTo().getPlayerScore().get(codeID) != null){
										minScore += prerequisite.getPlayerTo().getPlayerScore().get(codeID).getBalance().intValue();
									}
									points.setHint("min "+minScore);
									
									((TextView)findViewById(R.id.textpoints)).setText(String.format(context.getString(R.string.challhowmanypointyou),nicknameTo));
								}else if(challengeType == ChallengeCreate.DIRECTION_SOURCE){
									Integer minScore = 1;
									if(prerequisite.getPlayerFrom().getPlayerScore().get(codeID) != null){
										minScore += prerequisite.getPlayerFrom().getPlayerScore().get(codeID).getBalance().intValue();
									}
									points.setHint("min "+minScore);
									((TextView)findViewById(R.id.textpoints)).setText(R.string.challhowmanypoint);
								}
																
								betrow.removeView(pb);
								betrow.setGravity(Gravity.LEFT);
								findViewById(R.id.betcompile).setVisibility(View.VISIBLE);	
								img1.setTag(prerequisite.getPlayerFrom().getUser().getUserimg());
								img2.setTag(prerequisite.getPlayerTo().getUser().getUserimg());
								imageManager.displayImage(prerequisite.getPlayerFrom().getUser().getUserimg(), context, img1);
								imageManager.displayImage(prerequisite.getPlayerTo().getUser().getUserimg(), context, img2);																
							}catch(Exception e){
								e.printStackTrace();
							}
						}    					
    				});    				
    				
    			}catch(Exception e){e.printStackTrace();}
    		}
		}).start();				
	}
	
	private void sendChallenge(final int challengeType){
		final EditText bedollarsEdit = (EditText) findViewById(R.id.editbet);
		final EditText pointsEdit = (EditText) findViewById(R.id.editpoints);		
		
		final Integer playerToMaxBeDollars = prerequisite.getPlayerTo().getUser().getBedollars().intValue();
		final Integer playerFromMaxBeDollars = prerequisite.getPlayerFrom().getUser().getBedollars().intValue();		
		final Integer bedollars = (bedollarsEdit.getText().toString().length() > 0) ? Integer.parseInt(bedollarsEdit.getText().toString()) : null;
		final Integer absoluteMax = (playerFromMaxBeDollars > playerToMaxBeDollars) ? playerToMaxBeDollars : playerFromMaxBeDollars;
		
		if(bedollarsEdit.getText().length() < 1){
			showAlertInput(0);
			return;
		}
		if(challengeType != ChallengeCreate.CHALLENGE_48_HOURS && pointsEdit.getText().length() < 1){
			showAlertInput(1);
			return;
		}
		
		if(bedollars > absoluteMax){
			bedollarsEdit.setText(absoluteMax.toString());
		}
		
		final ProgressDialog dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);		
		new Thread(new Runnable(){     					
    		public void run(){ 
    			try{   	
    				BeintooUser bu = new BeintooUser();
    				String kind = null, actor = null;
    				Integer points = null, bedollars = null;
    				if(challengeType == ChallengeCreate.DIRECTION_DEST){
    					kind = "BET";
    					actor = userExtTo;
    					points = Integer.parseInt(pointsEdit.getText().toString());
    					bedollars = Integer.parseInt(bedollarsEdit.getText().toString());
    				}else if(challengeType == ChallengeCreate.DIRECTION_SOURCE){
    					kind = "BET";
    					actor = userExtFrom;
    					points = Integer.parseInt(pointsEdit.getText().toString());
    					bedollars = Integer.parseInt(bedollarsEdit.getText().toString());
    				}else{
    					kind = "CHALLENGE";
    					bedollars = Integer.parseInt(bedollarsEdit.getText().toString());				
    				}
    				 
    				bu.challenge(userExtFrom, userExtTo, "INVITE", kind, codeID, null, actor, points, bedollars);
    				UIhandler.post(new Runnable(){
						@Override
						public void run() {	
							MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.challSent)+nicknameTo,Gravity.BOTTOM);							
						}
    				});
    				dialog.dismiss();
    				current.dismiss();
    				previous.dismiss();
    			}catch(ApiCallException a){
    				final String msg = a.getMessage();
    				UIhandler.post(new Runnable(){
						@Override
						public void run() {	
							MessageDisplayer.showMessage(getContext(), msg, Gravity.BOTTOM);
							dialog.dismiss();
							current.dismiss();
							previous.dismiss();
						}
    				});    				
    			}catch(Exception e){
    				e.printStackTrace();
    				dialog.dismiss();
    				previous.dismiss();
    			}
    		}
		}).start();
	}
	
	private void showAlertInput(int type){
		String msg = null;
		if (type == 0) // bedollars
			msg = context.getString(R.string.challerrorbedollars);
		else if (type == 1) // points
			msg = context.getString(R.string.challerrorpoints);
		
		new AlertDialog.Builder(context).setMessage(msg)
	       .setCancelable(false)
	       .setPositiveButton("Ok", null).create().show();		
	}
	
	@Override
	public void onClick(View v) {
		
	}
	
	Handler UIhandler = new Handler();
}
