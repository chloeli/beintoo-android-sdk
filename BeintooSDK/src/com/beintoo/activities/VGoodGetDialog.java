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
package com.beintoo.activities;

import java.util.Collection;

import com.beintoo.R;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.Vgood;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;

public class VGoodGetDialog extends Dialog{
	Dialog current;
	public VGoodGetDialog(Context ctx, final Vgood vgood) {
		super(ctx, R.style.ThemeBeintooOn);	
		setContentView(R.layout.vgood);
		current = this;
		
		LoaderImageView vgoodpict = (LoaderImageView) findViewById(R.id.vgoodpict);		
		TextView title = (TextView) findViewById(R.id.couponname);
		TextView shortdesc = (TextView) findViewById(R.id.shortdesc);
		TextView endate = (TextView) findViewById(R.id.endate);
		
		vgoodpict.setImageDrawable(vgood.getImageUrl());
		title.setText(vgood.getName());
		shortdesc.setText(vgood.getDescriptionSmall());
		endate.setText(vgood.getEnddate());
		 
		Collection<User> users = vgood.getWhoAlsoConverted();
		
		LinearLayout alsoconverterLayout = (LinearLayout) findViewById(R.id.alsoconverted);
		if(users != null) {
			int count = 0;
			for(User u : users){
				
				LinearLayout ll = new LinearLayout (getContext());
				ll.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setGravity(Gravity.CENTER);
				ll.setPadding(0, 0, 10, 0);
				final LoaderImageView image = new LoaderImageView(getContext(), u.getUsersmallimg());
				ll.addView(image);
				TextView nick = new TextView(getContext());
				nick.setText(u.getNickname());
				ll.addView(nick);
				
				alsoconverterLayout.addView(ll);
				count++;
				if(count == 3) break;
			}
			
		}
		
		Button closevgood = (Button) findViewById(R.id.closevgood);
		BeButton b = new BeButton(ctx);
		closevgood.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));	    
		closevgood.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();
			}
		});
		
		Button getVgood = (Button) findViewById(R.id.getcoupon);
		getVgood.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				PreferencesHandler.saveString("openUrl", vgood.getGetRealURL(), v.getContext());
				BeintooBrowser bb = new BeintooBrowser(v.getContext());
				bb.show();
				current.dismiss();
			}
		});
				
	}

}
