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
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BDisplayAdListener;
import com.beintoo.wrappers.VgoodChooseOne;

public class BeintooAdDialogHTML extends Dialog{
	private Context mContext;
	private Dialog current;
	VgoodChooseOne vgood;
	private WebView webview;
	public final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	public boolean hasShownDialog = false;
	private BDisplayAdListener badl;
	
	public BeintooAdDialogHTML(Context context, VgoodChooseOne v, BDisplayAdListener listener) {
		super(context, R.style.ThemeBeintooVgoodFullScreen);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
		mContext = context;
		vgood = v;
		current = this;
		badl = listener;
		
		webview = new WebView(mContext);
		webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.setContentView(webview);
	    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}
	
	public void loadAlert(){
		new Thread(new Runnable(){     					
			public void run(){ 
				try{
					webview.setVerticalScrollBarEnabled(false);
					webview.setHorizontalScrollBarEnabled(false);
					webview.setWebViewClient(new WebViewClient() {						
					    @Override
						public void onLoadResource(WebView view, String url) {
							super.onLoadResource(view, url);
						}

						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							DebugUtility.showLog("URL: "+url);
							if(!vgood.getVgoods().get(0).isOpenInBrowser()){
								BeintooBrowser bb = new BeintooBrowser(mContext, url.toString());
								bb.show();
							}else{
								Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
								mContext.startActivity(browserIntent);
							}
							UIhandler.sendEmptyMessage(HIDE_DIALOG);
					        return true;
					    }
					    
					    @Override
						public void onReceivedError(WebView view,
								int errorCode, String description,
								String failingUrl) {
					    	DebugUtility.showLog(description + " "+ errorCode);
							super.onReceivedError(view, errorCode, description, failingUrl);
						}

						public void onPageFinished(WebView view, String url){
							if(!hasShownDialog){
								UIhandler.sendEmptyMessage(SHOW_DIALOG);
								hasShownDialog = true;
							}		
							if(badl != null)
								badl.onAdFetched();
				    		Beintoo.adIsReady = true;
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
							DebugUtility.showLog(""+message);
							super.onConsoleMessage(message, lineNumber, sourceID);
						}
						
					});
					
					webview.setOnTouchListener(new View.OnTouchListener() {						
					    public boolean onTouch(View v, MotionEvent event) {
					      return (event.getAction() == MotionEvent.ACTION_MOVE);
					    }
					});
						
					webview.loadDataWithBaseURL(null, vgood.getVgoods().get(0).getContent(), vgood.getVgoods().get(0).getContentType(), "UTF-8", null);					
				}catch (Exception e){if(DebugUtility.isDebugEnable) e.printStackTrace();}
			} 
		}).start();		
	}
	
	public Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			 if(msg.what == SHOW_DIALOG && vgood != null){
				  current.show();				  
				  if(badl != null)
					  badl.onAdDisplay();
				  Beintoo.adIsReady = false;
			  }else if(msg.what == HIDE_DIALOG)
				  current.dismiss();
		  }
	};
}
