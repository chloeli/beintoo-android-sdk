package com.beintoo.vgood;

import java.util.ArrayList;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodChooseOne;
import com.google.gson.Gson;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VGoodGetList extends Dialog implements OnClickListener{
	Dialog current;
	double ratio;
	VgoodChooseOne vgoodList;
	
	public VGoodGetList(Context ctx, final VgoodChooseOne vgoodlist) {
		super(ctx,R.style.ThemeBeintoo);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vgoodlist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		current = this;
		vgoodList = vgoodlist;
		 
		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		try {
			prepareList();	
		}catch (Exception e){e.printStackTrace();}
	}
	
	
	public void prepareList(){
		TableLayout table = (TableLayout) findViewById(R.id.table);

		table.removeAllViews();
		
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	    for (int i = 0; i < vgoodList.getVgoods().size(); i++){
	    	
    		final LoaderImageView image = new LoaderImageView(getContext(), vgoodList.getVgoods().get(i).getImageUrl(),(int)(ratio *70),(int)(ratio *70));
    		
    		TableRow row = createRow(image, vgoodList.getVgoods().get(i).getName(),vgoodList.getVgoods().get(i).getDescription(),vgoodList.getVgoods().get(i).getEnddate(), table.getContext());
			row.setId(count);
			rowList.add(row);
			BeButton b = new BeButton(getContext());
			if(count % 2 == 0)
	    		row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 120),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
	    	count++;
	    }
	    
	    for (View row : rowList) {
		      row.setPadding(0, 0, 0, 0);	
		      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
		    	  row.setOnClickListener(this);
		      table.addView(row);
		}
	}
	
	public TableRow createRow(View image, String goodtitle, String gooddescription, String end,  Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding((int)(ratio*10), 0, 0, 0);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);

		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setPadding((int)(ratio*5), 0, (int)(ratio*10), (int)(ratio*10));
		  main.setOrientation(LinearLayout.VERTICAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  LinearLayout firstinnerrow = new LinearLayout(row.getContext());
		  firstinnerrow.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  firstinnerrow.setPadding(0, (int)(ratio*15), 0, 0);
		  firstinnerrow.setOrientation(LinearLayout.HORIZONTAL);
		  firstinnerrow.setGravity(Gravity.CENTER_VERTICAL);
		  
		  
		  TextView title = new TextView(activity);
		  title.setText(goodtitle);
		  title.setMaxLines(3);
		  title.setPadding((int)(ratio*10), 0, 0, 0);
		  title.setTextColor(Color.parseColor("#000000"));
		  title.setGravity(Gravity.CENTER_VERTICAL);
		  title.setTextSize(14);
		  title.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  
		  TextView description = new TextView(activity);		  
		/*  if(gooddescription.length() > 146)
			  gooddescription =  gooddescription.substring(0, 146) + "..."; */ 		  		  
		  description.setText(gooddescription);
		  description.setEllipsize(TextUtils.TruncateAt.END);
		  description.setMaxLines(3);
		  description.setLines(3);
		  description.setPadding((int)(ratio*10), (int)(ratio*8), 0, 0);
		  description.setTextColor(Color.parseColor("#333133"));
		  description.setTextSize(14);
		  
		  
		  firstinnerrow.addView(image);
		  firstinnerrow.addView(title);
		  
		  main.addView(firstinnerrow);
		  main.addView(description);
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  return row;
	}
	
	// CALLED WHEN A ROW IS CLICKED
	public void onClick(final View v) {
		try {
			Vgood vgood = vgoodList.getVgoods().get(v.getId());
			VGoodGetDialog getVgood = new VGoodGetDialog(current.getContext(), vgood);
			Beintoo.currentDialog = getVgood;
			getVgood.show();
			current.dismiss();
		
			// ASSIGN THE VGOOD IF IS LOGGED
			Player loggedPlayer = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", current.getContext()), Player.class);
			if(loggedPlayer.getUser() != null){
				BeintooVgood bv = new BeintooVgood();	    					
				bv.acceptVgood(vgood.getId(), loggedPlayer.getUser().getId(), null);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color == 1)
			  spacer.setBackgroundColor(Color.parseColor("#8F9193"));
		  else if(color == 2)
			  spacer.setBackgroundColor(Color.WHITE);

		  return spacer;
	}
	
}
