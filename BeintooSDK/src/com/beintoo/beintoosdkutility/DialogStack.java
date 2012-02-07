package com.beintoo.beintoosdkutility;

import java.util.ArrayList;

import android.app.Dialog;

import com.beintoo.main.Beintoo;

public class DialogStack {
	public static void addToDialogStack(Dialog d){
		if(Beintoo.dialogStack == null)
			Beintoo.dialogStack = new ArrayList<Dialog>();		
		 Beintoo.dialogStack.add(d);
	}
	
	public static void dismissAllDialogStack(){
		if(Beintoo.dialogStack != null){
			for(int i = 0; i<Beintoo.dialogStack.size();i++)
				Beintoo.dialogStack.get(i).dismiss();
			Beintoo.dialogStack = null;
		}
	}	
	
	public static void removeFromDialogStack(Dialog d){
		if(Beintoo.dialogStack != null){
			try {
				Beintoo.dialogStack.remove(d);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
