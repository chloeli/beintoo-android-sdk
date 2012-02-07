package com.beintoo.beintoosdkui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.beintoo.R;
import com.beintoo.R.color;
import com.beintoo.beintoosdkutility.DpiPxConverter;

import com.beintoo.wrappers.Player;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BeintooListViewDialogES extends Dialog implements OnScrollListener, OnItemClickListener{
	
	protected List<Object> objectList;
	protected Player player;	
	protected ArrayList<Object> objectItems;
	protected ArrayAdapter<Object> adapter;
	protected ListView listView;
	protected Context mContext;
	
	protected String optionalEmptyMessage = null;
	
	// if true, the loader is shown only on the listview, not on the entire view	
	protected boolean hideOnlyListViewOnLoading = false;
	
	protected Runnable mRunnable;
	
	// paginating params
	protected int currentPageRows = 0;   	
	protected int currentScrollPos = 0;
	protected int start = 0;
	protected int rows = 0;
	protected int MAX_REQ_ROWS = 10;
	protected boolean isLoading = false;
	protected boolean hasReachedEnd = false;
	protected double ratio;
	
	public BeintooListViewDialogES(Context context) {
		super(context, R.style.ThemeBeintoo);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	public void setupListView(){
		listView = (ListView) findViewById(R.id.listView);        
        listView.setCacheColorHint(0);	    	    
	    listView.setScrollingCacheEnabled(false);
	    listView.setOnScrollListener(this);
	    listView.setOnItemClickListener(this);
	    listView.addFooterView(loadingFooter());		    
	}
	
	/**
	 * main entry
	 * @param reset
	 */
	public void startFirstLoading (boolean reset){
		objectItems = new ArrayList<Object>();
		objectList = new ArrayList<Object>();
		if(reset)
			reset();
		setupListView();		
		
		if(!hideOnlyListViewOnLoading){
			findViewById(R.id.container).setVisibility(View.GONE);
		}else{
			listView.setVisibility(View.INVISIBLE);
		}
					
		((RelativeLayout)findViewById(R.id.goodcontent)).addView(progressLoading());					
		loadData(currentPageRows, MAX_REQ_ROWS);						
	}
	
	public void startFirstLoading (){
		startFirstLoading(false);
	}
	
	public void loadMore(){		
		currentPageRows++;
		loadData(currentPageRows*MAX_REQ_ROWS, MAX_REQ_ROWS);		
	}
	
	private void loadData (final int start, final int rows) {
		this.start = start;
		this.rows = rows;
		isLoading = true;		
		
		new Thread(mRunnable).start();		
	}

	@SuppressWarnings("unchecked")
	public void updateListView(@SuppressWarnings("rawtypes") ArrayAdapter a){
		try {
			// if it's the first loading add the arrayadapter
			if(start == 0 && a != null){
				adapter = a;
				adapter.setNotifyOnChange(false);
				listView.setAdapter(adapter);			
			}
			
			// notify the data loaded 
			adapter.notifyDataSetChanged();		
			
			// if it's the first loading set the listview visible and set the empty message if empty and setted 
			if(start == 0){						
				listView.setVisibility(View.VISIBLE);
				if(objectItems.size() == 0 && this.optionalEmptyMessage != null)				
					addEmptyMessage();
			}else{ 	// remove the footer loading if it's endless scrolling loading						
				listView.findViewWithTag(2000).setVisibility(View.GONE);
			}
			
			if(hasReachedEnd){ // if is the end of the list remove the loader on the footer				
				listView.removeFooterView(listView.findViewWithTag(2000));
			}
			
			if(start == 0) // remove the big loader 
				hideLoading();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {		
		try {					
			currentScrollPos = firstVisibleItem;
			if(!isLoading && !hasReachedEnd){										
				if(firstVisibleItem > 0 && totalItemCount > 0 && (firstVisibleItem > totalItemCount - visibleItemCount-1)){					
					isLoading = true;	
					if(listView != null && listView.getFooterViewsCount() > 0)
						listView.findViewWithTag(2000).setVisibility(View.VISIBLE);		            
					loadMore();
				}		
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void reset(){
		isLoading = false;
		hasReachedEnd = false;
		currentPageRows = 0;
		currentScrollPos = 0;
		start = 0;
		rows = 0;		
		try {
			RelativeLayout l = (RelativeLayout)findViewById(R.id.goodcontent);
			for(int i = 0; i < l.getChildCount(); i++){ // remove all the content added except the listview				
				if(!(l.getChildAt(i) instanceof ListView))
					l.removeView(l.getChildAt(i));
			}
			if(listView != null && listView.getFooterViewsCount() > 0)
				listView.removeFooterView(listView.findViewWithTag(2000));
			
			if(adapter != null){				
				adapter.clear();
				adapter.notifyDataSetChanged();

				// check if the adapter is implementing ImageManager
				// if true we have to stop the image loading thread
				try {
					Method m = adapter.getClass().getMethod("interruptImageManager", new Class[] {} );
					m.invoke(adapter);
				}catch(Exception e){
					e.printStackTrace();
				}

				adapter = null;				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}
	
	private LinearLayout loadingFooter(){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(35, 35);
		ProgressBar pb = new ProgressBar(mContext);					
		pb.setLayoutParams(params);	            	   
		pb.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progress));
        LinearLayout loading = new LinearLayout(mContext);
        loading.addView(pb);
        loading.setPadding(0, 10, 0, 10);
        loading.setGravity(Gravity.CENTER);
        loading.setTag(2000);
        loading.setVisibility(View.GONE);
        return loading;
	}
	
	
	
	private ProgressBar progressLoading (){
		ProgressBar pb = new ProgressBar(mContext);		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		double ratio = DpiPxConverter.getRatio(mContext);
		params.topMargin = (int)(ratio*100);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		
		params.height = (int)(ratio * 45);
		params.width = (int)(ratio * 45);		
		pb.setLayoutParams(params); 
		pb.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progress));
		pb.setTag(1000);
		return pb;
	}
	
	public void hideLoading(){				
		RelativeLayout l = (RelativeLayout)findViewById(R.id.goodcontent);
		l.removeView(l.findViewWithTag(1000));
		if(hideOnlyListViewOnLoading){
			listView.setVisibility(View.VISIBLE);
		}    	
	}

	private void addEmptyMessage(){
		listView.setVisibility(View.GONE);		
		TextView message = new TextView(mContext);
		message.setText(this.optionalEmptyMessage);		
		message.setGravity(Gravity.CENTER);
		message.setTextColor(color.gray_text);
		int padding = DpiPxConverter.pixelToDpi(mContext, 15);
		message.setPadding(padding, padding, 0, 0);
		((RelativeLayout)findViewById(R.id.goodcontent)).addView(message, 0);
	}
	
	public void setOptionalEmptyMessage(String message){
		this.optionalEmptyMessage = message;
	}
	
	public void setDisableEndlessScrolling(){
		this.hasReachedEnd = true;
	}
	
	public void setHideOnlyListViewOnLoading(){
		this.hideOnlyListViewOnLoading = true;
	}
	
	public void setMaxRequestRow(Integer rows){
		this.MAX_REQ_ROWS = rows;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
}
