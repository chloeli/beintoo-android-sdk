package com.beintoo.main.managers;

import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.SerialExecutor;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BGiveBedollarsListener;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.UserCredit;

public class AppManager {
	private static AtomicLong LAST_OPERATION = new AtomicLong(0);
	private long OPERATION_TIMEOUT = 2000;
	public void giveBedollars(final Context ctx, final Double amount, final Boolean showNotification, final Integer notificationGravity,
			final BGiveBedollarsListener callback) {
		SerialExecutor executor = SerialExecutor.getInstance();
		executor.execute(new Runnable() {
			public void run() {
				synchronized (LAST_OPERATION) {
					try {
						if (System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
							return;

						User u = Current.getCurrentUser(ctx);
						if(u != null){
							BeintooApp ba = new BeintooApp();
							UserCredit uc = ba.giveBedollars(u.getId(), amount);
							if(showNotification != null && showNotification){
								if(uc != null && uc.getValue() > 0){
									final Message msg = new Message();
									Bundle b = new Bundle();
									String message = null;			
									if(amount <= 1){
										message = String.format(ctx.getResources().getString(R.string.giveBedollarsEarned_s), amount.toString());
									}else{
										message = String.format(ctx.getResources().getString(R.string.giveBedollarsEarned), amount.toString());
									}
																	
									b.putString("Message", message);
									if(notificationGravity != null)
			    						b.putInt("Gravity", notificationGravity);
			    					else
			    						b.putInt("Gravity", Gravity.BOTTOM);
									
									msg.setData(b);
									msg.what = Beintoo.SUBMITSCORE_POPUP;
									Beintoo.UIhandler.sendMessage(msg);
								}
							}
							
							if(uc != null && uc.getValue() > 0){
								if (callback != null) {
									callback.onComplete(uc);
								}
							}else if(uc != null && uc.getValue() == 0){
								if (callback != null) {
									callback.onUserOverquota();
								}
							}
						}else{
							if (callback != null) {
								callback.onPlayerNotUser();
							}
						}
						
						LAST_OPERATION.set(System.currentTimeMillis());
					} catch (Exception e) {
						if (callback != null) {
							callback.onError(e);
						}
						e.printStackTrace();
					}
				}
			}
		});
	}
}
