package com.google.android.apps.markers;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;

public class PaintBrush extends Brush {
	Slate slate;
	
	public PaintBrush(Resources resources, Slate slate) {
		super(resources);
		// Brush settings	
		// Length in number of event calls of a brush gesture 
		super.gestureLength = 1200;
		// Initialize the weight array to the length of a brush gesture
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		this.generateWeights();
		// The texture of the brush 
		setDataBitmap(R.drawable.linessoft);
		this.slate = slate;
	}
	
	@Override
	public void handleEvent(MotionEvent event) {
			Log.d("gest", "tracker is " + gestureTracker);
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				continuePredictGesture(event);
				gestureTracker++;
				int a = slate.mStrokes[0].mRenderer.mPaint.getAlpha();
				int b = (int) Math.round(Math.sqrt((double) getInkAmount(event)) * a);
				
				if (b > 0xff) {
					b = 0xff;
				}
				else if (b < 0x01) {
					b = 0x01;
				}
				else {
//					event.getSize();
				}
				//Log.d("b", "B value = " + b);
				slate.mStrokes[0].mRenderer.mPaint.setAlpha(b);
			}
			else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				startPredictGesture(event);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				endPredictGesture();
			}
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
		float ret = 1 - ((float) i) / ((float) range);  
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
