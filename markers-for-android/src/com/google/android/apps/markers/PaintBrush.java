package com.google.android.apps.markers;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PaintBrush extends Brush {
	Slate slate;
	Boolean dabbed;
	
	public PaintBrush(Resources resources, Slate slate) {
		super(resources);
		// Brush settings	
		// Length in number of event calls of a brush gesture 
		super.gestureLength = 120;
		// Initialize the weight array to the length of a brush gesture
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		this.generateWeights();
		// The texture of the brush 
		setDataBitmap(R.drawable.lines);
		this.slate = slate;
		dabbed = false;
	}
	
	@Override
	public void handleEvent(MotionEvent event) {
			float f = getInkAmount();
			int b;
			if (f < 1.0 && f > 0.05) {
				b = 16 - Math.round(f * 16);
			}
			else {
				b = 0x01;
			}
			Log.d("b", "F value = " + b);
			slate.mStrokes[0].mRenderer.mPaint.setAlpha(b);
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
		int range = max - min;
		float ret = ((float) i) / ((float) range);  
		return ret;
	}
	
	public float getInkAmount() {
		if (dabbed) {
			if (this.gestureTracker < this.gestureWeights.length) {
				return this.gestureWeights[this.gestureTracker];
			}
			else {
				return this.gestureWeights[this.gestureWeights.length - 1];
			}
		}
		else {
			return 0f;
		}
	}
}
