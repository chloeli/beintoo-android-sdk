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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;


public class BDrawableGradient extends Drawable{
	final public static int BAR_GRADIENT = 1;
	final public static int GRAY_GRADIENT = 2;
	final public static int LIGHT_GRAY_GRADIENT = 3;
	final public static int HIGH_GRAY_GRADIENT = 4;
	final public static int BLU_BUTTON_GRADIENT = 5;
	final public static int BLU_ROLL_BUTTON_GRADIENT = 6;
	final public static int GRAY_ROLL_BUTTON_GRADIENT = 7;
	final public static int BLU_ROUNDED_BUTTON_GRADIENT = 8;
	final public static int BLU_ROLL_ROUNDED_BUTTON_GRADIENT = 9;
	final public static int NOTIFICATION_BAR_GRADIENT = 10;
	final public static int NOTIFICATION_BAR_CIRCLE_NUMBER = 11;
	final public static int NOTIFICATION_BAR_ALERT_CIRCLE = 12;
	
	int whichGradient;
	int width;
	int height;
	public BDrawableGradient(int w, int h, int function){
		whichGradient = function;
		width = w;
		height = h;
	}
	public void draw(Canvas canvas) {		
		if(whichGradient == BAR_GRADIENT) // THE BAR GRADIENT
			beintooBarGradient(width,height,canvas);
		else if(whichGradient == GRAY_GRADIENT) // THE Gray GRADIENT
			beintooGrayGradient(height,canvas);
		else if(whichGradient == LIGHT_GRAY_GRADIENT) // THE LIGHT Gray GRADIENT
			beintooLightGrayGradient(height,canvas);		
		else if(whichGradient == HIGH_GRAY_GRADIENT) // THE HIGH Gray
			beintooHighGrayGradient(height,canvas);
		else if(whichGradient == BLU_BUTTON_GRADIENT) // THE BLU BUTTON
			beintooButtonGradient(height,canvas);
		else if(whichGradient == BLU_ROUNDED_BUTTON_GRADIENT) // THE BLU RONDED BUTTON
			beintooRoundedButtonGradient(height, canvas);		
		else if(whichGradient == BLU_ROLL_BUTTON_GRADIENT) // THE BLU ROLLOVER BUTTON
			beintooButtonGradientRollover(height,canvas);	
		else if(whichGradient == BLU_ROLL_ROUNDED_BUTTON_GRADIENT)// THE BLU ROLLOVER ROUNDED BUTTON
			beintooRoundedButtonGradientRollover(height,canvas);	
		else if(whichGradient == GRAY_ROLL_BUTTON_GRADIENT) // THE GRAY ROLLOVER BUTTON
			beintooButtonGrayGradientRollover(height,canvas);
		else if(whichGradient == NOTIFICATION_BAR_GRADIENT) // THE NOTIFICATION BAR GRADIENT
			beintooNotificationBarGradient(height,canvas);
		else if(whichGradient == NOTIFICATION_BAR_CIRCLE_NUMBER) // THE NOTIFICATION BAR CIRCLE NUMBER 
			beintooNotificationBarCircleGradient(height,canvas);
		else if(whichGradient == NOTIFICATION_BAR_ALERT_CIRCLE) // THE NOTIFICATION BAR CIRCLE ALERT !
			beintooNotificationAlertImageCircleGradient(height, canvas);
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}
	
	public Canvas beintooGrayGradient(int h, Canvas canvas){ // USED FOR ROWS
		Paint p = new Paint();
		int [] colors = {0xffCBCFD3, 0xffB6BECA};
		float [] positions = {0.0f,0.5f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooLightGrayGradient(int h, Canvas canvas){ // USED FOR ROWS
		Paint p = new Paint();
		int [] colors = {0xffE2E2E2, 0xffCBCFD1};
		float [] positions = {0.0f,0.5f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p); 
	    return canvas;
	}
	
	public Canvas beintooHighGrayGradient(int h, Canvas canvas){ // USED FOR SELECTION
		Paint p = new Paint();
		int [] colors = {0xffC6CACE, 0xff9EA6AF};
		float [] positions = {0.0f,0.5f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p); 
	    return canvas;
	}
	
	public Canvas beintooButtonGradient(int h, Canvas canvas){
		Paint p = new Paint();
		int [] colors = {0xff8292A8, 0xff576B87,0xff4C627F, 0xff3C5271 };
		float [] positions = {0.0f,0.5f,0.5f,1.0f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooRoundedButtonGradient(int h, Canvas canvas){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		int [] colors = {0xff8292A8, 0xff576B87,0xff4C627F, 0xff3C5271 };
		float [] positions = {0.0f,0.5f,0.5f,1.0f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));
	    final Rect rect = new Rect(0, 0, canvas.getClipBounds().width(), canvas.getClipBounds().height());
        final RectF rectF = new RectF(rect);                
	    canvas.drawRoundRect(rectF, 8,8, p);
	    return canvas;
	}
	
	public Canvas beintooButtonGradientRollover(int h, Canvas canvas){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(Color.parseColor("#324C68"));
		canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooRoundedButtonGradientRollover(int h, Canvas canvas){
		Paint p = new Paint();
		p.setColor(Color.parseColor("#324C68"));
		final Rect rect = new Rect(0, 0, canvas.getClipBounds().width(), canvas.getClipBounds().height());
        final RectF rectF = new RectF(rect);                
	    canvas.drawRoundRect(rectF, 8,8, p);		
	    return canvas;
	}
	
	public Canvas beintooButtonGrayGradientRollover(int h, Canvas canvas){
		Paint p = new Paint();
		p.setColor(Color.parseColor("#6D84A2"));
		canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooBarGradient(int w, int h, Canvas canvas){
		Paint p = new Paint();				
		int [] colors = {0xffB0BCCC, 0xff889BB3,0xff8094AE, 0xff6D84A2 };
		float [] positions = {0.0f,0.5f,0.5f,1.0f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooNotificationBarGradient(int h, Canvas canvas){
		Paint p = new Paint();				
		int [] colors = {0xffB0BCCC, 0xff6D84A2};
		float [] positions = {0.0f,0.5f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    canvas.drawPaint(p);
	    return canvas;
	}
	
	public Canvas beintooNotificationBarCircleGradient(int h, Canvas canvas){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);				
		int [] colors = {0xff798AA3, 0xff485E7D, 0xff405576, 0xff2E4466};
		float [] positions = {0.0f, 0.5f, 0.51f, 1f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    
	    final Rect rect = new Rect(0, 0, canvas.getClipBounds().width(), canvas.getClipBounds().height());
        final RectF rectF = new RectF(rect);                
	    canvas.drawRoundRect(rectF, 5, 5, p);
	     
	    return canvas;
	}
	
	public Canvas beintooNotificationAlertImageCircleGradient(int h, Canvas canvas){
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);				
		int [] colors = {0xff798AA3, 0xff485E7D, 0xff405576, 0xff2E4466};
		float [] positions = {0.0f, 0.5f, 0.51f, 1f};
	    p.setShader(new LinearGradient(0, 0, 0, h, colors, positions,  Shader.TileMode.MIRROR));	    
	    
	    //final Rect rect = new Rect(0, 0, canvas.getClipBounds().width(), canvas.getClipBounds().height());
        //final RectF rectF = new RectF(rect);                
	    canvas.drawCircle(h/2, h/2, h/2, p);
	     
	    return canvas;
	}
	
}
