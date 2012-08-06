package com.beintoo.activities;

import com.beintoo.R;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DpiPxConverter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TutorialPostSignup extends Dialog{

	final Dialog mCurrent;
	final Context mContext;
	
	public TutorialPostSignup(Context context) {
		super(context, R.style.ThemeBeintoo);
		mCurrent = this;
		mContext = context;
		setContentView(R.layout.tutorial_post_signup);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,DpiPxConverter.pixelToDpi(mContext, 47),BDrawableGradient.BAR_GRADIENT));
		
		BeButton b = new BeButton(context);
		Button finish = (Button) findViewById(R.id.finishTutorial);
		int pixels = DpiPxConverter.pixelToDpi(context, 50);
		finish.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		finish.setBackgroundDrawable(
				b.setPressedBackg(
			    		new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
		
		((TextView) findViewById(R.id.play)).setText(Html.fromHtml(context.getResources().getString(R.string.tutorial_play)));
		((TextView) findViewById(R.id.earn)).setText(Html.fromHtml(context.getResources().getString(R.string.tutorial_earn)));
		((TextView) findViewById(R.id.redeem)).setText(Html.fromHtml(context.getResources().getString(R.string.tutorial_redeem)));
		
		int padding = DpiPxConverter.pixelToDpi(context, 10);
		((LinearLayout)findViewById(R.id.actionsContainer)).setPadding(padding, padding, padding, 0);
		
		if(context.getApplicationContext().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
			((LinearLayout)findViewById(R.id.actionsContainer)).setOrientation(LinearLayout.VERTICAL);
			((LinearLayout)findViewById(R.id.playContainer)).setOrientation(LinearLayout.HORIZONTAL);
			((LinearLayout)findViewById(R.id.earnContainer)).setOrientation(LinearLayout.HORIZONTAL);
			((LinearLayout)findViewById(R.id.redeemContainer)).setOrientation(LinearLayout.HORIZONTAL);			
        }
		
		finish.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				BeintooHome beintooHome = new BeintooHome(getContext());
				beintooHome.show();				
			  	mCurrent.dismiss();						
			}
		});
	}

}
