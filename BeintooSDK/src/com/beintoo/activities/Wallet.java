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

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Vgood;
import com.google.gson.Gson;

public class Wallet extends Dialog implements OnClickListener{
	Dialog current;
	Vgood [] vgood;
	public Wallet(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.wallet);
		current = this;
		
		try {
			vgood = new Gson().fromJson(PreferencesHandler.getString("wallet", getContext()), Vgood[].class);
			if(vgood.length > 0)
				loadWallet();
			else { // NO VGOODS SHOW A MESSAGE
				TextView noGoods = new TextView(getContext());
				noGoods.setText("You don't have any virtual goods");
				noGoods.setTextColor(Color.GRAY);
				noGoods.setPadding(20,0,0,0);						
				TableLayout table = (TableLayout) findViewById(R.id.table);
				table.addView(noGoods);
			}
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx);}
		
		Button close = (Button) findViewById(R.id.close);
		BeButton b = new BeButton(ctx);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));	    
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();				
			}
		});
	}
	
	public void loadWallet(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		//table.removeAllViews();
		
			
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	    for (int i = 0; i < vgood.length; i++){
    		final LoaderImageView image = new LoaderImageView(getContext(), vgood[i].getImageUrl(),90,90);
    		
    		TableRow row = createRow(image, vgood[i].getName(),vgood[i].getEnddate(), table.getContext());
			row.setId(count);
			rowList.add(row);
			if(i%2 == 0) row.setBackgroundColor(Color.parseColor("#d4e7eb"));
			/*View spacer = createSpacer(getContext(),0,10);
			spacer.setId(-100);
			rowList.add(spacer); */
	    	count++;
	    }
	    
	    for (View row : rowList) {
		      row.setPadding(0, 0, 0, 0);	
		      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
		    	  row.setOnClickListener(this);
		      table.addView(row);
		}
	}
	
	public static TableRow createRow(View image, String name, String end,  Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding(10, 5, 5, 5);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);

		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  
		  
		  TextView nameView = new TextView(activity);
		  nameView.setText(name);
		  nameView.setMaxLines(2);
		  nameView.setPadding(5, 0, 0, 0);
		  nameView.setTextColor(Color.parseColor("#83be56"));
		  nameView.setTypeface(null,Typeface.BOLD);
		  nameView.setMaxWidth(50);
		  TextView enddate = new TextView(activity);
		  enddate.setText("End date:\n"+end);
		  enddate.setTextSize(12);
		  enddate.setPadding(5, 0, 0, 0);
		  enddate.setTextColor(Color.parseColor("#787A77"));
		  
		  row.addView(image);
		  main.addView(nameView);
		  main.addView(enddate);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  
		  return row;
	}
	
	@SuppressWarnings("unused")
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);

		 // spacer.setPadding(50,50,50,50);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color != 0)
			  spacer.setBackgroundColor(Color.LTGRAY);

		  return spacer;
	}

	public void onClick(View v) {
		int selectedRow = v.getId();
		PreferencesHandler.saveString("openUrl", vgood[selectedRow].getShowURL(), v.getContext());
		BeintooBrowser bb = new BeintooBrowser(v.getContext());
		bb.show();
		
		
	}
}
