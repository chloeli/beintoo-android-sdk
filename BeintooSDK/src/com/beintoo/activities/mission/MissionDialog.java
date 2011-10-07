package com.beintoo.activities.mission;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooAchievements;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.CustomFonts;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.wrappers.Mission;
import com.beintoo.wrappers.Player;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MissionDialog extends Dialog implements OnClickListener{
	private Context currentContext;
	private Dialog current;
	private Player player; 
	private String deviceUUID; 
	private Mission mission;
	
	public static int MISSION_STATUS = 1;
	public static int MISSION_COMPLETED = 2;
	
	public MissionDialog(Context context, Mission m, final int status, Player p, String _deviceUUID){
		super(context, R.style.ThemeBeintoo);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(195, 0, 0, 0)));		
		setContentView(R.layout.mission);
		currentContext = context;			
		current = this;
		player = p;
		deviceUUID = _deviceUUID;
		mission = m;
		
		applyTypeface();
		ImageManager imageManager = new ImageManager(currentContext);
		try{
			
			// SETUP DIALOG TITLE, BUTTONS FOR MISSION TYPE (NEW, STATUS, COMPLETED)
			TextView textView1 = (TextView) findViewById(R.id.textView1);
			TextView textView2 = (TextView) findViewById(R.id.textView2);
			Button bt1 = (Button) findViewById(R.id.button1);
			Button bt2 = (Button) findViewById(R.id.button2);			
			bt1.setId(1);
			bt2.setId(2);
			bt1.setOnClickListener(this);
			bt2.setOnClickListener(this);			
			
			if(m.isNew()){ // NEW MISSION
				textView1.setText(currentContext.getString(R.string.missionwelcome));				
				textView2.setText(currentContext.getString(R.string.missionwelcomeweek));
				bt1.setText(currentContext.getString(R.string.missionlater));
				bt2.setText(currentContext.getString(R.string.missionjoin));				
			}else if(!m.isNew() && m.getStatus().equals("REGULAR")){ // RUNNING MISSION, STATUS 
				textView1.setText(currentContext.getString(R.string.missionstatus));
				textView2.setText(currentContext.getString(R.string.missionwelcomeweek));
				bt1.setVisibility(View.GONE);
				bt2.setText("OK");
				if(m.getVgood() == null){
					findViewById(R.id.red).setVisibility(View.VISIBLE);
					TextView red = (TextView) findViewById(R.id.redtw);				
					red.setText(Html.fromHtml(currentContext.getString(R.string.missionachieveonly)));
				}
			}else if(!m.isNew() && m.getStatus().equals("OVER")){ // MISSION ACCOMPLISHED
				textView1.setText(currentContext.getString(R.string.missionaccomplished));
				textView2.setText("");
				bt1.setVisibility(View.GONE);
				if(m.getVgood() != null){
					bt2.setText(currentContext.getString(R.string.missionredeem));
				}else{
					bt2.setText("OK");
				}					
				bt2.setId(3);
				TextView textView7 = (TextView)findViewById(R.id.textView7);
				textView7.setText(currentContext.getString(R.string.missionreward));
			}
			
			
			StringBuilder title = new StringBuilder(m.getPlayerAchievements().get(0).getAchievement().getApp().getName());
			if(m.getPlayerAchievements().get(0).getPercentage() != null){
				title.append("<font color=\"#FFFFFF\"> - ");
				title.append(m.getPlayerAchievements().get(0).getPercentage());
				title.append("%</font>");
			}
			
			TextView firstach = (TextView) findViewById(R.id.textView3);
			firstach.setText(Html.fromHtml(title.toString()));
			TextView firstachdesc = (TextView) findViewById(R.id.textView4);
			firstachdesc.setText(m.getPlayerAchievements().get(0).getAchievement().getDescription());
			
			ImageView img1 = (ImageView) findViewById(R.id.imageView1);
			img1.setTag(m.getPlayerAchievements().get(0).getAchievement().getImageURL());
			imageManager.displayImage(m.getPlayerAchievements().get(0).getAchievement().getImageURL(), currentContext, img1);
			
			if(m.getSponsoredAchievements().size() == 1){ // SPONSORED ACHIEVEMENT
				title = new StringBuilder(m.getSponsoredAchievements().get(0).getAchievement().getApp().getName()); 
				TextView secondach = (TextView) findViewById(R.id.textView5);
				if(m.getSponsoredAchievements().get(0).getPercentage() != null){
					title.append("<font color=\"#FFFFFF\"> - ");
					title.append(m.getSponsoredAchievements().get(0).getPercentage());
					title.append("%</font>");
				}
				
				if(m.getSponsoredAchievements().get(0).getAchievement().getApp().getDownload_url() != null){
					secondach.setMovementMethod(LinkMovementMethod.getInstance());
					title.append("<font color=\"#FFFFFF\"> - <a href=\"");
					title.append(m.getSponsoredAchievements().get(0).getAchievement().getApp().getDownload_url().get("ANDROID"));
					title.append("\">");
					title.append(currentContext.getString(R.string.missiondownload));
					title.append("</a></font>");
				}	
				
				secondach.setText(Html.fromHtml(title.toString()));
				TextView secondachdesc = (TextView) findViewById(R.id.textView6);
				secondachdesc.setText(m.getSponsoredAchievements().get(0).getAchievement().getDescription());
				ImageView img2 = (ImageView) findViewById(R.id.imageView2);
				img2.setTag(m.getSponsoredAchievements().get(0).getAchievement().getImageURL());
				imageManager.displayImage(m.getSponsoredAchievements().get(0).getAchievement().getImageURL(), currentContext, img2);
			}else if(m.getPlayerAchievements().size() > 1){ // TWO ACHIEVEMENTS OF THE SAME APP
				title = new StringBuilder(m.getPlayerAchievements().get(1).getAchievement().getName()); 
				TextView secondach = (TextView) findViewById(R.id.textView5);
				if(m.getSponsoredAchievements().get(0).getPercentage() != null){
					title.append("<font color=\"#FFFFFF\"> - ");
					title.append(m.getPlayerAchievements().get(1).getPercentage());
					title.append("%</font>");	
				}
	
				secondach.setText(Html.fromHtml(title.toString()));
				TextView secondachdesc = (TextView) findViewById(R.id.textView6);
				secondachdesc.setText(m.getPlayerAchievements().get(1).getAchievement().getDescription());
				ImageView img2 = (ImageView) findViewById(R.id.imageView2);
				img2.setTag(m.getPlayerAchievements().get(1).getAchievement().getImageURL());
				imageManager.displayImage(m.getPlayerAchievements().get(1).getAchievement().getImageURL(), currentContext, img2);
			}else{
				findViewById(R.id.secondAchievement).setVisibility(View.GONE);
			}
			
			
			if(m.getVgood() != null){
				TextView prizetitle = (TextView) findViewById(R.id.textView7);
				prizetitle.setText(Html.fromHtml(currentContext.getString(R.string.missionachieve)));			
				TextView vgoodesc = (TextView) findViewById(R.id.textView8);				
				vgoodesc.setText(m.getVgood().getDescriptionSmall());
				ImageView img3 = (ImageView) findViewById(R.id.imageView3);
				img3.setTag(m.getVgood().getImageUrl());
				imageManager.displayImage(m.getVgood().getImageUrl(), currentContext, img3);
			}else{
				findViewById(R.id.vgood).setVisibility(View.GONE);							
			}
			
			
			if(m.getVgood() != null && player.getUser() == null){
				//findViewById(R.id.redeemmail).setVisibility(View.VISIBLE);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private int toDip (int px) {
		double ratio = (currentContext.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);		
		return (int)(ratio*px);
	}
	
	private void applyTypeface(){
		TextView a = (TextView) findViewById(R.id.textView1);
		TextView b = (TextView) findViewById(R.id.textView2);
		TextView c = (TextView) findViewById(R.id.textView3);
		TextView d = (TextView) findViewById(R.id.textView4);
		TextView e = (TextView) findViewById(R.id.textView5);
		TextView f = (TextView) findViewById(R.id.textView6);
		TextView g = (TextView) findViewById(R.id.textView7);
		TextView h = (TextView) findViewById(R.id.textView8);
		TextView b1 = (TextView) findViewById(R.id.button1);
		TextView b2 = (TextView) findViewById(R.id.button2);
		TextView red = (TextView) findViewById(R.id.redtw);
		Button bt1 = (Button) findViewById(R.id.button1);
		Button bt2 = (Button) findViewById(R.id.button2);
		Typeface marke = CustomFonts.handWriteFont(currentContext);
		a.setTypeface(marke);
		b.setTypeface(marke);
		c.setTypeface(marke);
		d.setTypeface(marke);
		e.setTypeface(marke);
		f.setTypeface(marke);
		g.setTypeface(marke);
		h.setTypeface(marke);
		b1.setTypeface(marke);
		b2.setTypeface(marke);		
		bt1.setTypeface(marke);		 
		bt2.setTypeface(marke);
		red.setTypeface(marke);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == 1){ // left button (later)
			// refuse challenge
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						BeintooAchievements ba = new BeintooAchievements();
						ba.refuseMission(player.getGuid(), deviceUUID);
					}catch(Exception e){
						e.printStackTrace();
					}
				}				
			}).start();
			
			current.dismiss();
		}else if(v.getId() == 2){ // right button (join now/ok)
			current.dismiss();
		}else if(v.getId() == 3){ // right button (GET IT REAL)
			if(mission.getVgood() != null){
				BeintooBrowser bb = new BeintooBrowser(currentContext, mission.getVgood().getGetRealURL());
				bb.show();
			}
			current.dismiss();
			
			// hide
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						BeintooAchievements ba = new BeintooAchievements();
						ba.hideMission(player.getGuid(), deviceUUID);
					}catch(Exception e){
						e.printStackTrace();
					}
				}				
			}).start();
		}
	}	
}
