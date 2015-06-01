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
	
	public TPadBrushHandler(TPad tPad, View view) {
		this.tPad = tPad;
		this.view = view;
		this.resources = view.getResources();
		currentBrush = new DefaultBrush(resources);
	}
	
	public void changeBrush(Brush b) {
		currentBrush = null;
		currentBrush = b;
	}
	
	public void handleEvent(MotionEvent event) {
		// Values from 0.0f-1.0f are 0-100% tPad activation
		currentBrush.handleEvent(event);
		try {
			tPad.sendFrictionBuffer(currentBrush.getPixelBuffer());
		}
		catch (Exception e) {
			Log.d("FDebug", "Send friction buffer failed.");
		}
	}
}
