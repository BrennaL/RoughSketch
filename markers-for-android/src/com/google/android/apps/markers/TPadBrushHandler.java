package com.google.android.apps.markers;

import nxr.tpad.lib.TPad;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.dsandler.apps.markers.R;

public class TPadBrushHandler {
	private TPad tPad;
	private View view;
	private Brush currentBrush;
	private Resources resources;
	
	public Brush defaultBrush;
	public Brush eraseBrush;
	public Brush sandBrush;
	public Brush pen;
	
	public TPadBrushHandler(TPad tPad, View view) {
		this.tPad = tPad;
		this.view = view;
		this.resources = view.getResources();
		
		defaultBrush = new DefaultBrush(resources);
		sandBrush = new SandBrush(resources);
		pen = new StaticPen(resources);
		
		currentBrush = defaultBrush;
		
	}
	
	public void changeBrush(Brush b) {
		currentBrush = null;
		currentBrush = b;
	}
	
	public void handleEvent(MotionEvent event, boolean tpadOnOff) {
		// Values from 0.0f-1.0f are 0-100% tPad activation
		// if the tpad is off, send 0 friction. 
		System.out.println("the tpad is " + tpadOnOff);
		if (!tpadOnOff)
			tPad.turnOff();
		else{
			currentBrush.handleEvent(event);
			try {
				//Log.d("FDebug", "BrushFriction : " + currentBrush.getPixelBuffer()[0]);
				tPad.sendFrictionBuffer(currentBrush.getPixelBuffer());
			}
			catch (Exception e) {
				Log.d("FDebug", "Send friction buffer failed.");
			}
		}
	}
}
