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
package com.beintoo.beintoosdkutility;

import com.beintoo.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessageDisplayer {
	public static void showMessage (Context ctx, String message){
		Toast toast = new Toast(ctx);
		RoundRectShape rect = new RoundRectShape( new float[] {15,15, 15,15, 15,15, 15,15}, null, null); 
		ShapeDrawable bg = new ShapeDrawable(rect); 
		bg.getPaint().setColor(Color.argb(178, 0, 145, 183));

		LinearLayout ll = new LinearLayout(ctx);
		ll.setLayoutParams(new LinearLayout.LayoutParams(
		          LinearLayout.LayoutParams.WRAP_CONTENT,
		          LinearLayout.LayoutParams.WRAP_CONTENT
		      ));
		ll.setPadding(10, 10, 10, 10);
		ll.setBackgroundDrawable(bg);
		
		ImageView logo = new ImageView(ctx);		
		logo.setImageResource(R.drawable.beintoobtn);
		
		TextView messageView = new TextView(ctx);
		messageView.setPadding(10,0,0,0);
		messageView.setText(message);
		messageView.setTextColor(Color.WHITE);
		messageView.setTypeface(Typeface.DEFAULT_BOLD, 0);
		 
		ll.addView(logo);
		ll.addView(messageView);
		
		toast.setGravity(Gravity.CENTER, 0, 20);
		toast.setView(ll);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
}