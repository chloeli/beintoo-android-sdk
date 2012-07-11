package com.beintoo.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beintoo.R;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DebugUtility;

public class Forums extends Dialog {
	WebView webview;
	final Dialog current;
	public String openUrl = null;
	private LinearLayout loading;
	
	public Forums(Context ctx, String url) {
		super(ctx, R.style.ThemeBeintoo);		
		current = this;		
		setContentView(R.layout.smartwebui);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		TextView title = (TextView)findViewById(R.id.dialogTitle);
		title.setText(ctx.getString(R.string.forum));
		
		webview = (WebView) findViewById(R.id.webview);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.clearCache(true);	
		webview.getSettings().setJavaScriptEnabled(true);
				
		WebSettings ws = webview.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setGeolocationEnabled(true);
		ws.setJavaScriptCanOpenWindowsAutomatically(true);
		ws.setPluginsEnabled(true);

		setLoading();
		
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {				
			}
			  
		    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
		    {  
		    	return super.onJsAlert(view, url, message, result);    
		    }
		});
		
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
					Toast.makeText(getContext(), "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			
			public void onPageFinished(WebView view, String url){
				webview.removeView(loading);
			}			
		});
		
		webview.setInitialScale(1);
		webview.loadUrl(url);		
		// DEBUG CALLED URL
		DebugUtility.showLog(url);
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
}
