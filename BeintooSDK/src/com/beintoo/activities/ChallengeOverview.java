package com.beintoo.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.wrappers.Challenge;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChallengeOverview extends Dialog{
	Challenge reqChallenge = null;
	final int ACCEPTED = 1;
	final int ENDED = 2;
	
	public ChallengeOverview(Context context, Challenge requested, int typeOfChallenge) {
		super(context);
		reqChallenge = requested;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.challengeoverview);
		
		if(reqChallenge != null){
			try{
				TextView contest = (TextView) findViewById(R.id.contname);
				contest.setText(reqChallenge.getContest().getName());
				
				TextView status = (TextView) findViewById(R.id.status);
				status.setText(reqChallenge.getStatus());
				
				LinearLayout frompict = (LinearLayout) findViewById(R.id.frompict);
				LoaderImageView from = new LoaderImageView(getContext(),reqChallenge.getPlayerFrom().getUser().getUserimg(),70,70);
				frompict.addView(from);
				
				LinearLayout topict = (LinearLayout) findViewById(R.id.topict);
				LoaderImageView to = new LoaderImageView(getContext(),reqChallenge.getPlayerTo().getUser().getUserimg(),70,70);
				topict.addView(to);
				
				TextView fromNick = (TextView) findViewById(R.id.fromnick);
				fromNick.setText(reqChallenge.getPlayerFrom().getUser().getNickname());
				TextView toNick = (TextView) findViewById(R.id.tonick);
				toNick.setText(reqChallenge.getPlayerTo().getUser().getNickname());
				 
				TextView fromScore = (TextView) findViewById(R.id.scorefrom);
				fromScore.setText(reqChallenge.getPlayerFromScore().toString());				
				TextView toScore = (TextView) findViewById(R.id.scoreto);
				toScore.setText(reqChallenge.getPlayerToScore().toString());
				
				SimpleDateFormat curFormater = new SimpleDateFormat("d-MMM-y HH:mm:ss"); 
				curFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date startDate = curFormater.parse(reqChallenge.getStartdate());
				Date endDate = curFormater.parse(reqChallenge.getEnddate());
				curFormater.setTimeZone(TimeZone.getDefault());
				TextView sDate = (TextView) findViewById(R.id.startdate);
				sDate.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(startDate));
				TextView eDate = (TextView) findViewById(R.id.enddate);
				eDate.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(endDate));
				
				if(typeOfChallenge == ENDED){	
					TextView winner = (TextView) findViewById(R.id.winner);
					winner.setText(reqChallenge.getWinner().getUser().getNickname());
					TextView prize = (TextView) findViewById(R.id.prize);
					prize.setText(reqChallenge.getPrize().toString());
				}else {
					LinearLayout win = (LinearLayout) findViewById(R.id.winll);
					win.setVisibility(LinearLayout.INVISIBLE);
				}
			}catch(Exception e){e.printStackTrace();}
		}else{ // dismiss if error
			System.out.println("NULL");
			this.dismiss();
		}
	}
}
