package com.google.android.apps.markers;

import nxr.tpad.lib.TPad;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.dsandler.apps.markers.R;

public class TPadSampler {
	private Bitmap dataBitmap; // holds current texture data
	private TPad tPad;
	private View view;
	private Brush currentBrush;
	
	public TPadSampler(TPad tPad, View view) {
		this.tPad = tPad;
		this.view = view;
	}
	public void handleEvent(MotionEvent event) {
		// Values from 0.0f-1.0f are 0-100% tPad activation
		float frictionLevel; 
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//start motion
				frictionLevel = currentBrush.startGesture(event);
				break;
			case MotionEvent.ACTION_MOVE:
				//continue motion
				frictionLevel = currentBrush.continueGesture(event);
				break;
			case MotionEvent.ACTION_UP:
				//end motion
				frictionLevel = currentBrush.endGesture(event);
				break;
			default:
				// send half friction
				frictionLevel = currentBrush.defaultFriction();
		}
		tPad.sendFriction(frictionLevel);
	}
	
	/**
	 * Loads a bitmap from the resources folder.
	 * @author bucci
	 * @param uri : could be any unique identifying int, such as R.drawable.filter 
	 */
	public boolean loadBitmapFromResources(int uri) {
		this.dataBitmap = BitmapFactory.decodeResource(view.getResources(), uri);
		return true;
	}
}
