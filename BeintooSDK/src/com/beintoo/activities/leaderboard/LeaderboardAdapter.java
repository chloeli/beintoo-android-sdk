package com.beintoo.activities.leaderboard;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.beintoo.R;
import com.beintoo.activities.leaderboard.Leaderboard.Leaders;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ImageManager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LeaderboardAdapter extends ArrayAdapter<Leaders> {
	private final ArrayList<Leaders> leaders;
	public 	final ImageManager imageManager;
	private final Context currentContext;	
	private final double ratio;
	private final BeButton gradientRow;	
	
	public LeaderboardAdapter(Context context, int textViewResourceId, ArrayList<Leaders> leaders) {
		super(context, textViewResourceId, leaders);
		this.leaders = leaders;
		this.currentContext = context;
		this.ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);
		gradientRow = new BeButton(getContext());				
		imageManager = new ImageManager(context);
	}
	
	public static class ViewHolder{
		public TextView nickname;
		public TextView alliance;
		public TextView score;
		public TextView position;
		public ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		
		try {
			if (v == null) {		
				LayoutInflater vi = 
					(LayoutInflater)currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.leaderboarditem, null);
				holder = new ViewHolder();
				holder.nickname = (TextView) v.findViewById(R.id.nickname);
				holder.alliance = (TextView) v.findViewById(R.id.alliance);
				holder.alliance.setVisibility(View.GONE);
				holder.score = (TextView) v.findViewById(R.id.score);
				holder.position = (TextView) v.findViewById(R.id.position);
				holder.image = (ImageView) v.findViewById(R.id.image);
				v.setTag(holder);
			}
			else
				holder=(ViewHolder)v.getTag();
	
			final Leaders leader = leaders.get(position);
			if (leader != null) {
				holder.nickname.setText(leader.name);
				
				NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());		
				String formattedScore = nf.format(Double.parseDouble(leader.score));
				
				if(leader.feed == null)
					holder.score.setText(currentContext.getString(R.string.leadScore)+"\n"+formattedScore);
				else{			
					holder.score.setText(leader.feed+":\n"+formattedScore);
				}
				holder.position.setText(leader.position);
				
				
				try {
					if(leader.imageUrl != null){
						holder.image.setTag(leader.imageUrl);
						imageManager.displayImage(leader.imageUrl, currentContext, holder.image);
					}else{
						holder.image.setVisibility(View.GONE);
						v.findViewById(R.id.item).setPadding((int)(ratio * 5), (int)(ratio * 5), 0, (int)(ratio * 5));
					}
				}catch (Exception e){ e.printStackTrace(); }
				
				LinearLayout positionView = (LinearLayout) v.findViewById(R.id.post);			
				setPositionBackground(positionView);
				setRowBackground(v, position);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return v;
	}
	 
	private void setRowBackground(View v, int position){		
		if(position % 2 == 0)
    		v.findViewById(R.id.item).setBackgroundDrawable(gradientRow.setPressedBackg(
    	    		new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.LIGHT_GRAY_GRADIENT),
    				new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT),
    				new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		else
			v.findViewById(R.id.item).setBackgroundDrawable(gradientRow.setPressedBackg(
		    		new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.GRAY_GRADIENT),
					new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT),
					new BDrawableGradient(0,(int)(ratio * 60),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		
	}
	public ImageManager getImageManager(){
		return imageManager;
	}
	private void setPositionBackground(LinearLayout l){		
		 l.setBackgroundColor(Color.argb(150, 255, 255, 255));
	}
}
