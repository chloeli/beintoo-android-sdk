package com.beintoo.activities;

import com.beintoo.R;
import com.beintoo.main.Beintoo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class tryDialog extends AlertDialog.Builder{

	public tryDialog(final Context context) {
		super(context);
		ImageView img = new ImageView(context);
		img.setImageResource(R.drawable.logobtn);
		TextView message = new TextView(context);
		message.setPadding(20, 20, 10, 20);
		message.setText("Beintoo transforms your passion for apps into real rewards.\nIt's extremely easy!\n" +
				"Simple use your Facebook account to login and you will be rewarded with exceptional virtual and real goods while" +
				" using apps and playing games.");
		final LinearLayout layout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        layout.addView(img);
        layout.addView(message);
        
		this.setView(layout)
	       .setCancelable(false)
	       .setPositiveButton("Try it!", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                Beintoo.BeintooStart(context);
	           }
	       })
	       .setNegativeButton("No thanks!", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	           }
	       });
	       
		//AlertDialog alert = this.create();
		//alert.show();
	}

	@Override
	public Builder setView(View view) {
		// TODO Auto-generated method stub
		return super.setView(view);
	} 
	
}
