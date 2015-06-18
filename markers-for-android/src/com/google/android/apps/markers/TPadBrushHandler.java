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
	private Slate slate;
	public Brush currentBrush;
	private Resources resources;
	
	public Brush defaultBrush;
	public Brush paintBrush;
	public Brush sizeToAlphaBrush;
	public Brush eraseBrush;
	public Brush sandBrush;
	public Brush fingerPaint;
	public Brush fingerFeel;
	public Brush pen;
	
	public TPadBrushHandler(TPad tPad, Slate slate) {
		this.tPad = tPad;

		this.slate = slate;
		this.resources = slate.getResources();
		
		fingerPaint = new FingerPaintBrush(resources, slate);
		fingerFeel = new FingerFeelBrush(resources, slate);
		defaultBrush = new DefaultBrush(resources);
		paintBrush = new PaintBrush(resources,slate);
		sandBrush = new SandBrush(resources);
		pen = new StaticPen(resources);
		eraseBrush = new EraseBrush(resources); 
		sizeToAlphaBrush = new SizeToAlphaBrush(resources, slate);
		
		currentBrush = defaultBrush;
		
	}
	
	
	
	public void changeBrush(Brush b) {
		currentBrush.brushOff();
		currentBrush = null;
		currentBrush = b;
		currentBrush.brushOn();
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
