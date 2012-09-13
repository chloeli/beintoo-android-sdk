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
package com.beintoo.vgood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import android.widget.LinearLayout;


import com.beintoo.R;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.main.Beintoo.BGetVgoodListener;
import com.beintoo.wrappers.VgoodChooseOne;

public class BeintooRecomDialogHTML extends Dialog{
	Context ctx;
	private Dialog current;
	VgoodChooseOne vgood;
	private WebView webview;
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private boolean hasShownDialog = false;
	private BGetVgoodListener gvl;
	
	public BeintooRecomDialogHTML(Context context, VgoodChooseOne v, BGetVgoodListener listener) {
		super(context, R.style.ThemeBeintooVgoodFullScreen);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
		ctx = context;
		vgood = v;
		current = this;
		gvl = listener;
		
		webview = new WebView(ctx);
		webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.setContentView(webview);
	    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    
	    DebugUtility.showLog("DIALO");
	}
	
	public void loadAlert(){
		new Thread(new Runnable(){     					
			public void run(){ 
				try{
					webview.setVerticalScrollBarEnabled(false);
					webview.setHorizontalScrollBarEnabled(false);
					//webview.setFocusableInTouchMode(false);
					//webview.setFocusable(false);
											
					webview.setWebViewClient(new WebViewClient() {						
					    @Override
						public void onLoadResource(WebView view, String url) {
							super.onLoadResource(view, url);
						}

						public boolean shouldOverrideUrlLoading(WebView view, String url) {
					    	Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
							ctx.startActivity(browserIntent);
							UIhandler.sendEmptyMessage(HIDE_DIALOG);
					        return true;
					    }
					    
					    @Override
						public void onReceivedError(WebView view,
								int errorCode, String description,
								String failingUrl) {
							System.out.println(description + " "+ errorCode);
							super.onReceivedError(view, errorCode, description, failingUrl);
						}

						public void onPageFinished(WebView view, String url){
							if(!hasShownDialog){
								UIhandler.sendEmptyMessage(SHOW_DIALOG);
								hasShownDialog = true;
							}
						}
					});
					
					webview.setWebChromeClient(new WebChromeClient(){
						@Override
						public void onCloseWindow(WebView window) {
							dismiss();
							super.onCloseWindow(window);
						}

						@Override
						public void onConsoleMessage(String message,
								int lineNumber, String sourceID) {							
							System.out.println(""+message);
							super.onConsoleMessage(message, lineNumber, sourceID);
						}
						
					});
					
					webview.setOnTouchListener(new View.OnTouchListener() {						
					    public boolean onTouch(View v, MotionEvent event) {
					      return (event.getAction() == MotionEvent.ACTION_MOVE);
					    }
					});
						
					webview.loadDataWithBaseURL(null, vgood.getVgoods().get(0).getContent(), vgood.getVgoods().get(0).getContentType(), "UTF-8", null);
					
				}catch (Exception e){e.printStackTrace();}
			} 
		}).start();
	}
	
	/*private int toDip (int px) {
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		
		return (int)(ratio*px);
	}
	
	public void onClick(View v) {
		try {
			if(v.getId() == 0){ // REFUSE
				this.cancel();
			}else if(v.getId() == 1){ // ACCEPT
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(vgood.getVgoods().get(0).getGetRealURL()));
				ctx.startActivity(browserIntent);
				this.cancel();
			}
		}catch(Exception e){}
	}*/
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			 if(msg.what == SHOW_DIALOG){
				  current.show();

				  if(gvl != null)
					  gvl.onComplete(vgood);
			  }else if(msg.what == HIDE_DIALOG)
				  current.dismiss();
		  }
	};
}
