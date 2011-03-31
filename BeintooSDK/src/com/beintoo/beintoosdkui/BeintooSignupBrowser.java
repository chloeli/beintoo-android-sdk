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
package com.beintoo.beintoosdkui;

import com.beintoo.R;
import com.beintoo.activities.BeintooHome;
import com.beintoo.activities.UserLogin;

import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BeintooSignupBrowser extends Dialog {
	
	WebView webview;
	final Dialog current;
	private static final int GO_HOME = 1;
	
	public BeintooSignupBrowser(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		current = this;
		setContentView(R.layout.browser);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		webview = (WebView) findViewById(R.id.webview);
		
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		webview.getSettings().setJavaScriptEnabled(true);

		clearAllCookies();
	    
		WebSettings ws = webview.getSettings();
		ws.setJavaScriptEnabled(true);

		Button ok = new Button(getContext());
		webview.addJavascriptInterface(ok, "ok");

		ok.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", "Login...",true);
				/* Do a playerLogin to setup the DeviceUUID and the GUID */
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							BeintooPlayer player = new BeintooPlayer();
							Gson gson = new Gson();
							Player newPlayer = new Player();
							newPlayer = player.getPlayer(PreferencesHandler.getString("guid",getContext()));

							// SET THAT THE PLAYER IS LOGGED IN BEINTOO
							PreferencesHandler.saveBool("isLogged", true, getContext());
							// SAVE THE CURRENT PLAYER
							String jsonUser = gson.toJson(newPlayer);
							PreferencesHandler.saveString("currentPlayer", jsonUser,getContext());
							// FINALLY GO HOME
							UIhandler.sendEmptyMessage(GO_HOME);
							
						} catch (Exception e) {
						}
						dialog.dismiss();
					}
				});
				t.start();

				current.dismiss();
			}
		});
		
		
		Button closebt = new Button(getContext());		
		webview.addJavascriptInterface(closebt, "closebt");
		closebt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {				
				// BACK TO LOGIN
				UserLogin userLogin = new UserLogin(getContext());
				userLogin.show();
				current.dismiss(); 
			}
		});

		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				ProgressBar p = (ProgressBar) findViewById(R.id.progress);				
				p.setProgress(progress);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
					Toast.makeText(getContext(), "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		Button close = (Button) findViewById(R.id.close);
		BeButton b = new BeButton(ctx);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));
		close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();
			}
		});

		webview.setInitialScale(1);
		webview.loadUrl(getUrlOpenUrl());
	}

	private String getUrlOpenUrl() {
		String openUrl = PreferencesHandler.getString("openUrl", getContext());
		if (openUrl != null) {
			return openUrl;
		}
		return "";
	}

	/*public void clearCache() {
		File dir = getCacheDir();
		
		if (dir != null && dir.isDirectory()) {
			try {
				File[] children = dir.listFiles();
				if (children.length > 0) {
					for (int i = 0; i < children.length; i++) {
						File[] temp = children[i].listFiles();
						for (int x = 0; x < temp.length; x++) {
							temp[x].delete();
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}*/
	
	private void clearAllCookies (){
		//clearCache();
		//deleteDatabase("webview.db");
	    //deleteDatabase("webviewCache.db");
	    webview.clearCache(true);
	    webview.clearHistory();
	    CookieSyncManager cookieSyncMngr =
            CookieSyncManager.createInstance(getContext());
	    cookieSyncMngr.startSync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();        
	    cookieManager.removeSessionCookie();
	    cookieSyncMngr.stopSync();	    
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == GO_HOME){
				BeintooHome beintooHome = new BeintooHome(getContext());
				beintooHome.show();							
				current.dismiss();
				Beintoo.currentDialog.dismiss();
			  }
			  super.handleMessage(msg);
		  }
	};
}
