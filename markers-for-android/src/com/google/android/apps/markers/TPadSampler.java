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
	public void startGesture(MotionEvent event) {
		tPad.sendFriction(currentBrush.startGesture(event));
	}
	public void continueGesture(MotionEvent event) {
		tPad.sendFriction(currentBrush.endGesture(event));
	}
	public void endGesture(MotionEvent event) {
		tPad.sendFriction(currentBrush.continueGesture(event));
	}
	public void defaultGesture(MotionEvent event) {
		tPad.sendFriction(currentBrush.defaultFriction());
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
