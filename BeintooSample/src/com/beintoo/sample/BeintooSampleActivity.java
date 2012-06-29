package com.beintoo.sample;

import com.beintoo.R;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BGetVgoodListener;
import com.beintoo.main.Beintoo.BSubmitScoreListener;
import com.beintoo.wrappers.VgoodChooseOne;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class BeintooSampleActivity extends Activity {		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Beintoo.setApiKey("YOUR-APIKEY-HERE");
        
        // THIS SET THE TRY BEINTOO VIEW WITH THE REWARD TEMPLATE
        // IF YOU DO NOT WANT IT JUST REMOVE THIS LINE
        Beintoo.setTryBeintooTemplate(Beintoo.TRY_BEINTOO_REWARD);
        
        // START BEINTOO BUTTON
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				Beintoo.BeintooStart(v.getContext(), true);										
			} 			
        });  
        
        Button getvgood = (Button) findViewById(R.id.getVgood);
	    getvgood.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				// GET A REWARD AND SHOW IT AS A DIALOG
            	Beintoo.GetVgood(BeintooSampleActivity.this, null, false, null, Beintoo.GET_VGOOD_ALERT, gvl);
			}
        }); 
	     	    
	    Button getvgoodlist = (Button) findViewById(R.id.vgoodlist);
	    getvgoodlist.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {	
				// GET A REWARD AND SHOW IT AS A BANNER, HERE WITHOUT A CALLBACK
				LinearLayout l = (LinearLayout) findViewById(R.id.testlayout);
            	Beintoo.GetVgood(BeintooSampleActivity.this, true, l, Beintoo.VGOOD_NOTIFICATION_BANNER);          				
			}
        });
	    
	    Button submit = (Button) findViewById(R.id.submit);
	    submit.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {	
				// SUBMIT 1 POINT AND NOTIFY THE PLAYER IN THE BOTTOM OF THE SCREEN
				Beintoo.submitScore(BeintooSampleActivity.this, 1, true, Gravity.BOTTOM,null);
			}
        }); 
	    
	    Button submitbal = (Button) findViewById(R.id.submitbal);
	    submitbal.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				// SUBMIT SCORE WITH A THRESHOLD, IF THE PLAYER REACHED THAT THRESHOLD IT ASSIGN A REWARD.
				//
				// WE SUBMIT 4 POINTS AND SET THE TRESHOLD TO 16, WHEN THE PLAYER REACHES
				// 16 POINTS IT WILL AUTOMATICALLY RECEIVE A REWARD
				LinearLayout l = (LinearLayout) findViewById(R.id.testlayout);
				Beintoo.submitScoreWithVgoodCheck(BeintooSampleActivity.this, 4, 16, null, true, l, Beintoo.VGOOD_NOTIFICATION_ALERT, sl,gvl);     
			}
        });
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		Beintoo.playerLogin(this);
	}
    
 
    // DEFINING LISTENERS FOR CALLBACKS
    
    // GET VGOOD CALLBACK
    final BGetVgoodListener gvl = new BGetVgoodListener(){

		@Override
		public void onComplete(VgoodChooseOne v) {			
			Log.d("BeintooSample", "Vgood assigned");
		}

		@Override
		public void isOverQuota() {
			Log.d("BeintooSample", "Vgood overquota");
		}

		@Override
		public void nothingToDispatch() {
			Log.d("BeintooSample", "Vgood nothing to dispatch");
		}

		@Override
		public void onError() {
			Log.d("BeintooSample", "Vgood error");
		}        	
    };
    
    // SUBMITSCORE CALLBACK
    final BSubmitScoreListener sl = new BSubmitScoreListener(){

		@Override
		public void onComplete() {
			Log.d("BeintooSample", "Submit Score complete");
		}

		@Override
		public void onBeintooError(Exception e) {
			Log.d("BeintooSample", "Submit Score error");
		}
    };
}