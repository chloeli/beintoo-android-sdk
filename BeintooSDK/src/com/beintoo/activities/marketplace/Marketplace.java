package com.beintoo.activities.marketplace;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.main.managers.LocationMManager;
import com.beintoo.wrappers.Player;
import com.google.beintoogson.Gson;

public class Marketplace extends Dialog implements android.webkit.GeolocationPermissions.Callback{
	WebView webview;
	final Dialog current;
	public String openUrl = null;
	public Marketplace(Context ctx, Player player) {
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
		webview.addJavascriptInterface(new BeintooJavaScriptInterface(getContext()), "Beintoo");
		
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
		
		String url = getMarketplaceUrl(player);		
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
	
	private String getMarketplaceUrl(Player player){
		String web = BeintooSdkParams.webUrl;
	  	if(BeintooSdkParams.internalSandbox) 
			web = BeintooSdkParams.sandboxWebUrl;
		Uri.Builder marketplaceUrl = Uri.parse(web+"m/marketplace.html").buildUpon();
		if(player.getGuid() != null)
			marketplaceUrl.appendQueryParameter("guid", player.getGuid());
		marketplaceUrl.appendQueryParameter("apikey", DeveloperConfiguration.apiKey);
		if(Beintoo.virtualCurrencyData != null){
			marketplaceUrl.appendQueryParameter("developer_user_guid", Beintoo.getVirtualCurrencyDevUserId());
			marketplaceUrl.appendQueryParameter("virtual_currency_amount", Beintoo.getVirtualCurrencyUserBalance());
		}
		
		marketplaceUrl.appendQueryParameter("os_source", "android"); 
		
		String locationParamsUrl = getSavedPlayerLocationParams(marketplaceUrl.toString());
		
		return locationParamsUrl;
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
	
	public class BeintooJavaScriptInterface {
	    Context mContext; 

	    BeintooJavaScriptInterface(Context c) {
	        mContext = c;
	    }

	    public void setPlayer(String player) {
	    	Player p = new Gson().fromJson(player, Player.class);
	    	Current.setCurrentPlayer(mContext, p);
	    	PreferencesHandler.saveBool("isLogged", true, getContext());
	    }
	}

	public void invoke(String origin, boolean allow, boolean remember) {
		// TODO Auto-generated method stub		
	}
}
