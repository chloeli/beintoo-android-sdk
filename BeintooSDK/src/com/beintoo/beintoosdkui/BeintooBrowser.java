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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.main.managers.LocationMManager;


public class BeintooBrowser extends Dialog implements android.webkit.GeolocationPermissions.Callback{
	WebView webview;
	final Dialog current;
	public String openUrl = null;
	public BeintooBrowser(Context ctx, String url) {
		super(ctx, R.style.ThemeBeintoo);		
		current = this;		
		setContentView(R.layout.browser);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		webview = (WebView) findViewById(R.id.webview);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);		
		
		WebSettings ws = webview.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setGeolocationEnabled(true);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);		
		ws.setLoadWithOverviewMode(true);
		ws.setUseWideViewPort(true);		
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(true);
		
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				ProgressBar p = (ProgressBar) findViewById(R.id.progress);				
				p.setProgress(progress);
			}
			
			public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {				
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}
		});
		
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
					Toast.makeText(getContext(), "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			
			public void onPageFinished(WebView view, String url){
				ProgressBar p = (ProgressBar) findViewById(R.id.progress);
				p.setVisibility(LinearLayout.GONE);
			}
			
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				ProgressBar p = (ProgressBar) findViewById(R.id.progress);
				p.setVisibility(LinearLayout.VISIBLE);
			}
		});
		
		
		
		if(url.contains("beintoo.com")){ // ADD LOCATION COORDINATES IF ON BEINTOO
			String locationParamsUrl = getSavedPlayerLocationParams(url);
			url = locationParamsUrl;
		}
		
		webview.loadUrl(url);		
		// DEBUG CALLED URL
		DebugUtility.showLog(url);
		
		Button close = (Button) findViewById(R.id.close);
		BeButton b = new BeButton(ctx);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));	    
		close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();
			}
		});
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(webview.canGoBack())
	    		webview.goBack();
	    	else
	    		current.dismiss();
	        return false; 
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private String getSavedPlayerLocationParams(String url){
		try {
			Location loc = LocationMManager.getSavedPlayerLocation(getContext());
			if(loc != null){
				Double latitude = loc.getLatitude();
				Double longitude = loc.getLongitude();
				Float accuracy = loc.getAccuracy();
				
				Uri.Builder b = Uri.parse(url).buildUpon();
				b.appendQueryParameter("lat", latitude.toString());
				b.appendQueryParameter("lng", longitude.toString());
				b.appendQueryParameter("acc", accuracy.toString());
				
				return b.toString();
			}else return url;
		}catch (Exception e){
			return url;
		}		
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

	@SuppressWarnings("unused")
	private void clearAllCookies (){
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

	public void invoke(String origin, boolean allow, boolean remember) {
		// TODO Auto-generated method stub
		
	}
}
