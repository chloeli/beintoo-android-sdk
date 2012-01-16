package com.beintoo.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.GeolocationPermissions.Callback;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DebugUtility;

public class SmartWebUi extends Dialog implements android.webkit.GeolocationPermissions.Callback{
	WebView webview;
	final Dialog current;
	public String openUrl = null;
	private LinearLayout loading;
	
	public SmartWebUi(Context ctx, String url) {
		super(ctx, R.style.ThemeBeintoo);		
		current = this;		
		setContentView(R.layout.smartwebui);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		webview = (WebView) findViewById(R.id.webview);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			
		webview.getSettings().setJavaScriptEnabled(true);
		
		 // ADD ZOOM CONTROLS
		 final FrameLayout.LayoutParams ZOOM_PARAMS =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				 																	ViewGroup.LayoutParams.WRAP_CONTENT,
				 																	Gravity.BOTTOM);
		 final View zoom = webview.getZoomControls();
		 webview.addView(zoom, ZOOM_PARAMS);
		
		// CLEAR ALL PREVIOUS COOKIES BEFORE LOGIN
		clearAllCookies();
		
		WebSettings ws = webview.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setGeolocationEnabled(true);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);
		ws.setPluginsEnabled(true);
		ws.setLoadWithOverviewMode(true);
		ws.setUseWideViewPort(true);
		
		setLoading();
		
		webview.setWebChromeClient(new WebChromeClient() {
			public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {				
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}		
		}); 
		
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.equals("http://www.beintoo.com/m/sdkandroid/dismisssmartwebui")){
					current.dismiss();
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			public void onReceivedError(WebView view, int errorCode, 
					String description, String failingUrl) {
					Toast.makeText(getContext(), "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			
			public void onPageFinished(WebView view, String url){
				webview.removeView(loading);
				String title = webview.getTitle();
				TextView t = (TextView)findViewById(R.id.dialogTitle);
				t.setText(title);
			}			
		});
		
		webview.loadUrl(url);		
		// DEBUG CALLED URL
		DebugUtility.showLog(url);	
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
	
	private void setLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		
		loading = new LinearLayout(current.getContext());
		loading.setBackgroundColor(Color.parseColor("#dadfe2"));
		loading.setGravity(Gravity.CENTER);
		loading.setLayoutParams(new  
				LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		loading.addView(pb);
		loading.setTag(100);
		webview.addView(loading);
	}

	private void clearAllCookies (){ // NOW NOT USED
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
	
	@SuppressWarnings("unused")
	private Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			 
	  		
			super.handleMessage(msg);
		  }
	};
	
}
