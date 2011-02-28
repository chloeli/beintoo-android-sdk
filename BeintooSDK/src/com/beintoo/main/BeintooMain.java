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
package com.beintoo.main;



import com.beintoo.R;
import com.beintoo.activities.VGoodGetDialog;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.PlayerScore;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class BeintooMain extends Activity {   
	Context ctx;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*UserLogin userSelection = new UserLogin(Beintoo.this);            					
		userSelection.show();*/
		ctx = this;
		setContentView(R.layout.maintest);
		Beintoo.setApiKey("f272a5ee2fe7193a59c874a8cb66a61");
		 //BeintooPlayer pla = new BeintooPlayer();   
 	      //System.out.println(player.getGuid());  
	      //System.out.println(pla.playerLogout("hhsagug").getMessage());
	      //System.out.println(pla.getScore("3F41E0C7FC037CDCB8FB36416FC55592").getBalance());
	     //BeintooVgood vgoodHandler = new BeintooVgood();
	     //Vgood v = new Vgood();
	     //v = vgoodHandler.getVgood("3F41E0C7FC037CDCB8FB36416FC55592", null, "45.481393","9.14303" ,"100", false);
	     //v = vgoodHandler.getVgood("3F41E0C7FC037CDCB8FB36416FC55592", null, null,null ,null, false);
	    // Player[] arr = pla.getPlayersByDeviceUDID(DeviceId.getUniqueDeviceId(getApplicationContext()));
		 
		 /*SALVO NELLE SHARED PREFERENCES HashMap<String,String> savedUsers = new HashMap<String,String>();
		 savedUsers.put("count1", "asfhsafhash9fah8");
		 savedUsers.put("count2", "bnnnmnnmn");
		 
		 System.out.println(gson.toJson(savedUsers));
		 */
		//PreferencesHandler.saveString("currentPlayer",null,this);
		//PreferencesHandler.saveBool("isLogged",false,this);
		
	    System.out.println("Device id:"+DeviceId.getUniqueDeviceId(getApplicationContext()));
	    System.out.println("User random id:"+PreferencesHandler.getString("guid", this));
	    System.out.println("Saved player: "+PreferencesHandler.getString("currentPlayer", getApplicationContext())+" ---- isLogged:" +PreferencesHandler.getBool("isLogged", getApplicationContext())); 
	    /*System.out.print("login:");
		pla.playerLogin(PreferencesHandler.getString("guid", getApplicationContext()), null, 
				DeviceId.getUniqueDeviceId(getApplicationContext()), null, null);
	    */
	 /*   for (Window window: Window.)  {
	    	
	    }*/
	   Beintoo.playerLogin(this);
	   PlayerScore p = Beintoo.getPlayerScore(this);
	   if(p != null){
		   System.out.println("Last Score: "+p.getLastscore());
		   System.out.println("Best Score: "+p.getBestscore());
		   System.out.println("Balance: "+p.getBalance());
	   } 
	   // BeintooLocationManager mng = new BeintooLocationManager(getApplicationContext());
	    //System.out.println("posizionme: "+mng.getUserLocation());
	    
	    Button bt = (Button) findViewById(R.id.button1);
	    bt.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				Beintoo.BeintooStart(ctx);
			} 
        });  
	    
	    Button getvgood = (Button) findViewById(R.id.getVgood);
	    getvgood.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				Beintoo.GetVgood(ctx);				
			}
        }); 
	    
	    Button submit = (Button) findViewById(R.id.submit);
	    submit.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				Beintoo.submitScore(getApplicationContext(), 100, true);
			}
        }); 
	    
	    Button submitbal = (Button) findViewById(R.id.submitbal);
	    submitbal.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				Beintoo.submitScore(getApplicationContext(), 100, 50, true);
			}
        });
	}
    
    /*@Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	super.onConfigurationChanged(newConfig);
    }
    
   

	@Override
	protected void onPause() {	
		super.onPause();
	}
 	*/

    
}
