package com.beintoo.activities.alliances;

import java.util.ArrayList;
import java.util.List;

import com.beintoo.R;
import com.beintoo.activities.UserProfile;
import com.beintoo.beintoosdk.BeintooAlliances;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Alliance;
import com.beintoo.wrappers.Message;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.beintoogson.Gson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class UserAlliance extends Dialog{
	private Dialog current;
	private Dialog previous;
	
	private Context currentContext;
	
	private ListView listView;
	private LinearLayout content;
	private UserAllianceAdapter adapter;
	
	private final double ratio;
	
	public static final int ENTRY_VIEW = 0;
	public static final int CREATE_ALLIANCE = 1;
	public static final int SHOW_ALLIANCE = 2;
	public static final int PENDING_REQUESTS = 3;
	public static final int ALLIANCES_LIST = 4;
	

	public UserAlliance(Context context, int section, String allianceID) {
		super(context, R.style.ThemeBeintoo);
		setContentView(R.layout.useralliance);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		currentContext = context;
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);	
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(LinearLayout.GONE);
        listView.setCacheColorHint(0);
        
        content = (LinearLayout) findViewById(R.id.goodcontent);
        content.addView(progressLoading());
        
        TextView title = (TextView) findViewById(R.id.dialogTitle);
		title.setTextColor(Color.WHITE);
		
        if(section == ENTRY_VIEW){
        	title.setText(current.getContext().getString(R.string.alliance));
        	loadEntryView();
        }else if(section == CREATE_ALLIANCE){
        	title.setText(current.getContext().getString(R.string.alliancemaincreate));
        	createAllianceForm();
        }else if(section == SHOW_ALLIANCE){
        	showAlliance(allianceID);
        }else if(section == PENDING_REQUESTS){
        	title.setText(current.getContext().getString(R.string.allianceviewpending));
        	pendingRequests();
        }else if(section == ALLIANCES_LIST){
        	title.setText(current.getContext().getString(R.string.alliancemainlist));
        	loadAlliancesList();
        }
	}
	
	public UserAlliance(Context context, int section) {
		this(context, section, null);
	}
		
	private void createAllianceForm(){
		content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
		final LinearLayout txtbox = (LinearLayout) findViewById(R.id.textbox);
		txtbox.setVisibility(View.VISIBLE);
		txtbox.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*50),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		LinearLayout btb = (LinearLayout)findViewById(R.id.actionbutton);
		btb.setVisibility(View.VISIBLE);
		BeButton b = new BeButton(currentContext);
		Button bt = (Button) btb.findViewById(R.id.button1);
		bt.setVisibility(View.VISIBLE);
		bt.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		bt.setText(current.getContext().getString(R.string.alliancemaincreate));
		bt.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));		
	    bt.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
				new Thread(new Runnable(){
					@Override
					public void run() {
						try {
							EditText nameBox = (EditText) txtbox.findViewById(R.id.editText);
							String name = nameBox.getText().toString().trim();
							if(name.length() > 2 && name.length() > 0){
								Gson gson = new Gson();
								Player player = gson.fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);
								BeintooAlliances ba = new BeintooAlliances();
								Alliance alliance = ba.createAlliance(player.getUser().getId(), name, null);
								if(alliance != null){
									player.setAlliance(alliance);
									PreferencesHandler.saveString("currentPlayer", gson.toJson(player), currentContext);
									UIHandler.post(new Runnable(){
										@Override
										public void run() {										
											UserAlliance ua = new UserAlliance(currentContext, UserAlliance.SHOW_ALLIANCE);
											ua.previous = current;
											ua.show();
											current.dismiss();
											previous.dismiss();
										}								
									});
								}
								dialog.dismiss();
							}else{
								dialog.dismiss();
								UIHandler.post(new Runnable(){
									@Override
									public void run() {		
										AlertDialog alertDialog = new AlertDialog.Builder(current.getContext()).create();
									    alertDialog.setMessage(current.getContext().getString(R.string.allianceinvalid));
									    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
									      public void onClick(DialogInterface dialog, int which) {
									    } }); 
									    alertDialog.show();
									}
									
								});								
							}
						}catch (Exception e){ 
							dialog.dismiss();
							e.printStackTrace(); 
						}						
					}					
				}).start();
			}														    
	    });
	}
	
	private void showAlliance(final String allianceID){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					final Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
					BeintooAlliances ba = new BeintooAlliances();
					String allianceExtId;
					if(allianceID != null) 
						allianceExtId = allianceID;
					else
						allianceExtId = p.getAlliance().getId();
					final Alliance a = ba.getAllianceInfo(allianceExtId, 0, 100, null);
					ArrayList<ListItem> l = new ArrayList<ListItem>();
					for(int i = 0; i < a.getUsers().size(); i++){
						ListItem item = new ListItem(a.getUsers().get(i).getNickname(), null, false, false);
						l.add(item);
					}
					adapter = new UserAllianceAdapter(currentContext, l);
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							TextView title = (TextView) findViewById(R.id.dialogTitle);
							title.setText(a.getName());							
							LinearLayout profile = (LinearLayout) findViewById(R.id.allianceprofile);
							profile.setVisibility(View.VISIBLE);
							profile.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*50),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							TextView name = (TextView) profile.findViewById(R.id.name);
							TextView members = (TextView) profile.findViewById(R.id.members);
							TextView admin = (TextView) profile.findViewById(R.id.admin);									
							name.setText(a.getName());
							members.setText(Html.fromHtml(current.getContext().getString(R.string.alliancemembers)+"<font color=\"#000000\">"+a.getMembers().toString()+"</font>"));
							admin.setText(Html.fromHtml(current.getContext().getString(R.string.allianceadmin)+"<font color=\"#000000\">"+a.getAdmin().getNickname()+"</font>"));
							LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
							tip.setVisibility(View.VISIBLE);
							tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							TextView tipmessage = (TextView) findViewById(R.id.tipmessage);
							tipmessage.setText(current.getContext().getString(R.string.allianceviewmembers));
							LinearLayout btb = (LinearLayout)findViewById(R.id.actionbutton);
							btb.setVisibility(View.VISIBLE);
							BeButton b = new BeButton(currentContext);
							
							if(a.getAdmin().getId().equals(p.getUser().getId())){
								Button pending = (Button) btb.findViewById(R.id.button1);
								pending.setText(current.getContext().getString(R.string.allianceviewpending));
								pending.setVisibility(View.VISIBLE);
								pending.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
								pending.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
										new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
										new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));		
								pending.setOnClickListener(new Button.OnClickListener(){
									@Override
									public void onClick(View v) {
										UserAlliance ua = new UserAlliance(currentContext, UserAlliance.PENDING_REQUESTS);
										ua.previous = current;
								        ua.show();
									}														    
							    });
							}
							
							Button leave = (Button) btb.findViewById(R.id.button2);
							leave.setText(current.getContext().getString(R.string.allianceleave));
							leave.setVisibility(View.VISIBLE);
							leave.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
							leave.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_BUTTON_GRADIENT),
									new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
									new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));		
							leave.setOnClickListener(new Button.OnClickListener(){
								@Override
								public void onClick(View v) {
									final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
									new Thread(new Runnable(){
										@Override
										public void run() {
											try {
												BeintooAlliances ba = new BeintooAlliances();
												ba.quitAlliance(p.getAlliance().getId(), p.getUser().getId(), null);
												p.setAlliance(null);
												PreferencesHandler.saveString("currentPlayer", new Gson().toJson(p), currentContext);
												dialog.dismiss();
												UIHandler.post(new Runnable(){
													@Override
													public void run() {
														current.dismiss();
														previous.dismiss();
													}													
												});												
											}catch(Exception e){
												dialog.dismiss();
												e.printStackTrace();
											}
										}
									}).start();
								}														    
						    });
							
							listView.setAdapter(adapter);
							listView.setOnItemClickListener(new OnItemClickListener(){
								@Override
								public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
									UserProfile userProfile = new UserProfile(getContext(),a.getUsers().get(position).getId());
					    			userProfile.show();										
								}								
							});
					        adapter.notifyDataSetChanged();
					        content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
							listView.setVisibility(LinearLayout.VISIBLE);
							
						}						
					});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 *  Load the pending requests view
	 */
	private void pendingRequests(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					final Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
					BeintooAlliances ba = new BeintooAlliances();
					final List<User> ul = ba.showPendingUsersRequests(p.getAlliance().getId(), p.getUser().getId(), "PENDING", 0, 100, null);
					
					final ArrayList<ListItem> l = new ArrayList<ListItem>();
					for(int i = 0; i < ul.size(); i++){
						ListItem item = new ListItem(ul.get(i).getNickname(), null, false, false);
						l.add(item);
					}
					
					adapter = new UserAllianceAdapter(currentContext, l);
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
							tip.setVisibility(View.VISIBLE);
							tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							TextView tipmessage = (TextView) findViewById(R.id.tipmessage);
							tipmessage.setText(current.getContext().getString(R.string.alliancependingtip));							
							listView.setAdapter(adapter);        
					        adapter.notifyDataSetChanged();
					        content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
							listView.setVisibility(LinearLayout.VISIBLE);
							listView.setOnItemClickListener(new OnItemClickListener(){
								@Override
								public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
									confirmUserAlert(ul.get(position).getNickname(), ul.get(position).getId(), p.getAlliance().getId(), p.getUser().getId(), position, ul);
								}								
							});
						}						
					});
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Alliances list and search
	 */
	private void loadAlliancesList(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					final Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
					BeintooAlliances ba = new BeintooAlliances();
					final List<Alliance> la =  ba.listByApp(null, 0, 20, null);
					
					ArrayList<ListItem> l = new ArrayList<ListItem>();
					for(int i = 0; i < la.size(); i++){
						ListItem item = new ListItem(la.get(i).getName(), current.getContext().getString(R.string.alliancemembers)+la.get(i).getMembers().toString(), false, false);
						l.add(item);
					}
					
					adapter = new UserAllianceAdapter(currentContext, l);
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							LinearLayout tip = (LinearLayout) findViewById(R.id.textbox);
							tip.setVisibility(View.VISIBLE);
							tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*50),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							final EditText searchtext = (EditText) findViewById(R.id.editText);
							searchtext.setHint(current.getContext().getString(R.string.alliancesearch));
							searchtext.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
							
							searchtext.setOnEditorActionListener(new EditText.OnEditorActionListener() {
					            @Override
					            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					                if (actionId == EditorInfo.IME_ACTION_SEARCH ) {					                	
					                	content.findViewWithTag(1000).setVisibility(LinearLayout.VISIBLE);
										listView.setVisibility(LinearLayout.GONE);
										final String searched = v.getText().toString();
										new Thread(new Runnable(){
											@Override
											public void run() {
												try{
													BeintooAlliances ba = new BeintooAlliances();
													final List<Alliance> la =  ba.listByApp(searched, 0, 50, null);
													ArrayList<ListItem> l = new ArrayList<ListItem>();
													for(int i = 0; i < la.size(); i++){
														ListItem item = new ListItem(la.get(i).getName(), current.getContext().getString(R.string.alliancemembers)+la.get(i).getMembers().toString(), false, false);
														l.add(item);
													}
													adapter = new UserAllianceAdapter(currentContext, l);
													UIHandler.post(new Runnable(){
														@Override
														public void run() {
															listView.setAdapter(adapter);        
													        adapter.notifyDataSetChanged();
													        
															content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
															listView.setVisibility(LinearLayout.VISIBLE);
														}
													});
													listView.setOnItemClickListener(new OnItemClickListener(){
														@Override
														public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
															requestJoinAlliance(la.get(position).getName(), la.get(position).getId(), p.getUser().getId());
														}								
													});
												}catch(Exception e){
													e.printStackTrace();
												}
											}
										}).start();
										
					                	InputMethodManager imm = (InputMethodManager)currentContext.getSystemService(Context.INPUT_METHOD_SERVICE);
					                    imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
					                    return true;
					                }
					                return false;
					            }

					        });
							listView.setAdapter(adapter);        
					        adapter.notifyDataSetChanged();
					        content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
							listView.setVisibility(LinearLayout.VISIBLE);
							listView.setOnItemClickListener(new OnItemClickListener(){
								@Override
								public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
									requestJoinAlliance(la.get(position).getName(), la.get(position).getId(), p.getUser().getId());
								}								
							});
						}
					});
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@SuppressWarnings("unused") // FRIEND INVITES UNUSED ACTUALLY
	private void loadFriends(){		
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
					BeintooUser u = new BeintooUser();
					User [] friends = u.getUserFriends(p.getUser().getId(), null);
					
					ArrayList<ListItem> l = new ArrayList<ListItem>();
					for(int i = 0; i < friends.length; i++){
						ListItem item = new ListItem(friends[i].getNickname(), null, true, false);
						l.add(item);
					}
					
					final ArrayList<ListItem> listitem = l;
					adapter = new UserAllianceAdapter(currentContext, listitem);
					
					UIHandler.post(new Runnable(){
						@Override
						public void run() {
							LinearLayout txtbox = (LinearLayout) findViewById(R.id.textbox);
							txtbox.setVisibility(View.VISIBLE);
							txtbox.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*50),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
							tip.setVisibility(View.VISIBLE);
							tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
							LinearLayout btb = (LinearLayout)findViewById(R.id.actionbutton);
							btb.setVisibility(View.VISIBLE);
							BeButton b = new BeButton(currentContext);
							Button bt = (Button) btb.findViewById(R.id.button1);
							bt.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
							bt.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
									new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
									new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));		
						    bt.setOnClickListener(new Button.OnClickListener(){
								@Override
								public void onClick(View v) {
									for(int i = 0; i < listitem.size(); i ++){
										System.out.println(listitem.get(i).isChecked);
									}
									
								}														    
						    });
							listView.setAdapter(adapter);        
					        adapter.notifyDataSetChanged();
					        content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
							listView.setVisibility(LinearLayout.VISIBLE);
						}						
					}); 
					 
				}catch(Exception e){
					e.printStackTrace();
				}
			}			
		}).start();
	}
	
	private void loadEntryView(){
		final Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
		BeButton b = new BeButton(getContext());
		LinearLayout container = new LinearLayout(currentContext);
		container.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
		tip.setVisibility(View.VISIBLE);
		tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		TextView tipmessage = (TextView) findViewById(R.id.tipmessage);
		tipmessage.setText(current.getContext().getString(R.string.alliancemaintip));
		
		LinearLayout first;
		if(p.getAlliance() != null && p.getAlliance().getId() != null)
			first = entryRow(R.drawable.yourall, current.getContext().getString(R.string.alliancemainyours), current.getContext().getString(R.string.alliancemainyoursbottom));
		else
			first =  entryRow(R.drawable.createall, current.getContext().getString(R.string.alliancemaincreate), current.getContext().getString(R.string.alliancemaincreatebottom));
		
		first.setBackgroundDrawable(b.setPressedBackg(
	    		new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		first.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(p.getAlliance() != null && p.getAlliance().getId() != null){
					UserAlliance ua = new UserAlliance(currentContext, UserAlliance.SHOW_ALLIANCE);
					ua.previous = current;
					ua.show();
				}else{
					UserAlliance ua = new UserAlliance(currentContext, UserAlliance.CREATE_ALLIANCE);
					ua.previous = current;
					ua.show();
				}				
			}			
		});
		
		container.addView(first);
		container.addView(createSpacer(currentContext,1,1));				
		container.addView(createSpacer(currentContext,2,1));
		
		LinearLayout second = entryRow(R.drawable.alllist, current.getContext().getString(R.string.alliancemainlist), current.getContext().getString(R.string.alliancemainlistbottom));
		second.setBackgroundDrawable(b.setPressedBackg(
	    		new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int)(ratio * 80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		second.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				UserAlliance ua = new UserAlliance(currentContext, UserAlliance.ALLIANCES_LIST);
				ua.previous = current;
				ua.show();			
			}			
		});
		container.addView(second);
		container.addView(createSpacer(currentContext,1,1));				
		container.addView(createSpacer(currentContext,2,1));
		
		TextView tipText = new TextView(currentContext);
		tipText.setPadding(0, (int)(ratio * 15), 0, 0);
		tipText.setGravity(Gravity.CENTER_HORIZONTAL);
		tipText.setTextColor(Color.parseColor("#787A77"));
		tipText.setText(current.getContext().getString(R.string.alliancemaintextfooter));
		container.addView(tipText);
		
		content.findViewWithTag(1000).setVisibility(LinearLayout.GONE);
		content.addView(container);				
	}
	
	private LinearLayout entryRow(int resImg, String topText, String bottomText){
		LinearLayout container = new LinearLayout(currentContext);
		container.setOrientation(LinearLayout.HORIZONTAL);
		container.setGravity(Gravity.CENTER_VERTICAL);
		container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(ratio * 80)));
		
		ImageView icon = new ImageView(currentContext);
		icon.setImageResource(resImg);
		icon.setPadding((int)(ratio * 10), 0, (int)(ratio * 10), 0);
		
		LinearLayout textcontainer = new LinearLayout(currentContext);		
		textcontainer.setOrientation(LinearLayout.VERTICAL);
		
		TextView top = new TextView(currentContext);
		top.setText(topText);
		top.setTextColor(Color.parseColor("#545859"));		
		TextView bottom = new TextView(currentContext);
		bottom.setTextColor(Color.parseColor("#787A77"));
		bottom.setText(bottomText);
		bottom.setPadding(0, 0, (int)(ratio*5), 0);
		textcontainer.addView(top);
		textcontainer.addView(bottom);
		
		container.addView(icon);
		container.addView(textcontainer);
		
		return container;		
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
	
	private void confirmUserAlert(final String name, final String userExt, final String allianceExtId, final String adminExtId, final int position, final List<User> ul){
		AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
		builder.setMessage(String.format(currentContext.getString(R.string.alliancependindialog), name))
		       .setCancelable(false)
		       .setPositiveButton(currentContext.getString(R.string.allianceaccept), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
						new Thread(new Runnable(){
							@Override
							public void run() {
								try {
									BeintooAlliances ba = new BeintooAlliances();
									Message msg = ba.acceptPendingUser(allianceExtId, adminExtId, userExt, null);									
									if(msg != null){
										UIHandler.post(new Runnable(){
											@Override
											public void run() {
												adapter.remove(adapter.getItem(position));
												ul.remove(position);
												adapter.notifyDataSetChanged();
												MessageDisplayer.showMessage(currentContext, String.format(currentContext.getString(R.string.alliancependingaccepted), name), Gravity.BOTTOM);											
											}										
										});
									}
								}catch (Exception e){ 									
									e.printStackTrace(); 
								}						
							}					
						}).start();
		           }
		       })
		       .setNegativeButton(currentContext.getString(R.string.alliancerefuse), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   new Thread(new Runnable(){
							@Override
							public void run() {
								try {
									BeintooAlliances ba = new BeintooAlliances();
									Message msg = ba.removeUser(allianceExtId, adminExtId, userExt, null);
									if(msg != null){
										UIHandler.post(new Runnable(){
											@Override
											public void run() {
												adapter.remove(adapter.getItem(position));
												ul.remove(position);
												adapter.notifyDataSetChanged();
												MessageDisplayer.showMessage(currentContext, String.format(currentContext.getString(R.string.alliancependingrefused), name), Gravity.BOTTOM);											
											}										
										});
									}
								}catch (Exception e){ 									
									e.printStackTrace(); 
								}						
							}					
						}).start();
		           }
		       }).setCancelable(true).create().show();
	}
	
	private void requestJoinAlliance(final String name, final String extID, final String userExt) {		
		
		final CharSequence[] items = {current.getContext().getString(R.string.alliancesendreq), 
				current.getContext().getString(R.string.alliancedetails)};
		AlertDialog.Builder builder = new AlertDialog.Builder(current.getContext());
		builder.setTitle(current.getContext().getString(R.string.vgoodchoosedialog));
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		        if(item == 0){ 
		        	try {
		        		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		        		builder.setMessage(String.format(getContext().getString(R.string.alliancerequestdialog), name))
		        		       .setCancelable(false)
		        		       .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
		        		           public void onClick(DialogInterface dialog, int id) {		 		        		        	   		       			
		        		        	   new Thread(new Runnable(){
		        			       			@Override
		        			       			public void run() {
		        			       				try {
		        			       					BeintooAlliances ba = new BeintooAlliances();
		        			       					Message msg = ba.joinAlliance(extID, userExt, null);
		        			       					if(msg != null){
		        			       						UIHandler.post(new Runnable(){
		        			       							@Override
		        			       							public void run() {
		        			       								MessageDisplayer.showMessage(currentContext, String.format(currentContext.getString(R.string.alliancerequestsent), name), Gravity.BOTTOM);											
		        			       							}										
		        			       						});
		        			       					}
		        			       				}catch (Exception e){ 									
		        			       					e.printStackTrace(); 
		        			       				}						
		        			       			}					
		        			       		}).start();
		        		           }
		        		       })
		        		       .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
		        		           public void onClick(DialogInterface dialog, int id) {
		        		        	   
		        		           }
		        		       });
		        		AlertDialog alert = builder.create();
		        		alert.show();
		        	}catch(Exception e){e.printStackTrace();}
		        }else if(item == 1){
		        	UserAlliance ua = new UserAlliance(currentContext, UserAlliance.SHOW_ALLIANCE, extID);
					ua.show();
		        }
		    }
		}).create().show();
	}
	
	private ProgressBar progressLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int)(ratio * 100), 0, 0);
		params.gravity = Gravity.CENTER;
		params.height = (int)(ratio * 45);
		params.width = (int)(ratio * 45);
		pb.setLayoutParams(params); 
		pb.setTag(1000);
		return pb;
	}
	
	public class ListItem {
		String leftText;
		String rightText;
		boolean showCheck;
		boolean isChecked;
		
		public ListItem(String l, String r, boolean showCheck, boolean isChecked){
			this.leftText = l;
			this.rightText = r;
			this.showCheck = showCheck;
			this.isChecked = isChecked;
		}
	}
	
	Handler UIHandler = new Handler(){};
}
