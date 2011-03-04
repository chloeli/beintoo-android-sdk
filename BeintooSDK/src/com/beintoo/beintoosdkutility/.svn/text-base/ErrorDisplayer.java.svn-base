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
package com.beintoo.beintoosdkutility;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class ErrorDisplayer {
	public static final String CONN_ERROR = "Connection error.\nPlease check your Internet connection.";
	
	public static void showConnectionError (String Message, final Context ctx){
		Handler handler = new Handler(){		     
		     //this method will handle the calls from other threads.       
		     public void handleMessage(Message msg) {		             
		          Toast.makeText(ctx, msg.getData().getString("SOMETHING"), Toast.LENGTH_LONG).show();
		     }
		};
        //create the message for the handler 
	   Message status = handler.obtainMessage();
	   Bundle data = new Bundle();
	   data.putString("SOMETHING", Message);
	   status.setData(data);
	         
	   handler.sendMessage(status);		
	}
	
	public static void showConnectionErrorOnThread (String Message, final Context ctx){
		Looper.prepare();
		showConnectionError (Message,ctx);	         
	    Looper.loop();		
	}
}
