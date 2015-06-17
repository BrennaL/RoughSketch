package com.google.android.apps.markers;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;

public class SizeToAlphaBrush extends Brush {
	Slate slate;
	
	public SizeToAlphaBrush(Resources resources, Slate slate) {
		super(resources);
		// Brush settings	
		// Length in number of event calls of a brush gesture 
		super.gestureLength = 1;
		// Initialize the weight array to the length of a brush gesture
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		this.generateWeights();
		// The texture of the brush 
		setDataBitmap(R.drawable.fingertemp);
		this.slate = slate;
	}
	
	@Override
	public void handleEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				slate.mStrokes[0].mRenderer.mPaint.setAlpha(0);
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				int a = 50;
				int b = (int) Math.round(event.getSize() * a);				
				slate.mStrokes[0].mRenderer.mPaint.setAlpha(b);
				gestureWeights[0] = map(0,1,0,1,event.getSize() * 2);
			}
			super.handleEvent(event);
	}
	
	/**
	 * Weight generating function.
	 */
	@Override
	protected void generateWeights() {
		for (int i=0; i<super.gestureLength; i++) {
			super.gestureWeights[i] = g(0,gestureLength,i);
		}
		Log.d("FDebug", "Generated default weights: ");
		System.out.println("Hi!");
		for (int i=0; i<gestureWeights.length;i++) {
			 Log.d("Fdebug", "gestureWeights[" + i + "] = " + gestureWeights[i]);
		}
	}
	/**
	 * Weight function, g(x). This should be where your weighting logic goes.
	 */
	private float g(int min, int max, int i) {
		// Change this logic to define a new function.  
		return 1.0f;
	}
	
	// Constrained map to range
	private float map(float minIn, float maxIn, float minOut, float maxOut, float f) {
		float rangeIn = (maxIn - maxIn);
		float rangeOut = (maxOut - maxOut);
		float percent = f / rangeIn;
		float ret = percent * rangeOut;
		if (ret < minOut) {
			ret = minOut;
		}
		if (ret > maxOut) {
			ret = maxOut;
		}
		return ret;
	}
	
	public float getInkAmount(MotionEvent event) {
			if (gestureTracker < gestureWeights.length) {
				return gestureWeights[gestureTracker];
			}
			else {
				return gestureWeights[gestureWeights.length - 1];
			}
	}
}
