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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BDisplayAdListener;
import com.beintoo.wrappers.VgoodChooseOne;

public class BeintooAdInterstitial extends Activity{
	private Context mContext;
	VgoodChooseOne vgood;
	private WebView webview;
	public boolean hasShownDialog = false;
	private BDisplayAdListener badl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				
		vgood = Beintoo.adlist;
		badl = Beintoo.bdal;
		mContext = this;
		webview = new WebView(mContext);
		webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		webview.setBackgroundColor(Color.TRANSPARENT);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setPluginsEnabled(true);
		
		disableScrollingAndZoom();
		this.setContentView(webview);		
	    loadAlert();
	    getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);	    
	}
	
	private void disableScrollingAndZoom() {
		webview.setHorizontalScrollBarEnabled(false);
		webview.setHorizontalScrollbarOverlay(false);
		webview.setVerticalScrollBarEnabled(false);
		webview.setVerticalScrollbarOverlay(false);
		webview.getSettings().setSupportZoom(false);
    }
	
	public void loadAlert(){
		new Thread(new Runnable(){     					
			public void run(){ 
				try{
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
							finish();
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
								hasShownDialog = true;
							}									
						}
					});
					
					webview.setWebChromeClient(new WebChromeClient(){
						@Override
						public void onCloseWindow(WebView window) {
							//dismiss();
							finish();
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
	
	@Override
	protected void onStart() {
		super.onStart();
		if(badl != null)
			  badl.onAdDisplay();
		  Beintoo.adIsReady = false;
	}

	@Override
	protected void onDestroy() {
		DebugUtility.showLog("DESTROYED");
		super.onDestroy();
	}	
}
