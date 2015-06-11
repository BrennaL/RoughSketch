package com.google.android.apps.markers;

import java.io.File;
import java.io.FileOutputStream;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class FingerPaintBrush extends Brush {
	View view;
	
	public FingerPaintBrush(Resources resources, View view) {
		super(resources);
		this.view = view;
		// Brush settings
		
		// Length in number of event calls of a brush gesture 
		super.gestureLength = 125;
		// Initialize the weight array to the length of a brush gesture
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		this.generateWeights();
		// The texture of the brush, default to lines
		setDataBitmap(R.drawable.lines);
		//Start view cache
		view.setDrawingCacheEnabled(true);
	}
	public void takeScreenShot() {
		Bitmap b = view.getDrawingCache();
        File f = new File(Environment.getExternalStorageDirectory(),  "photo.jpg");
        try {
        	b.compress(CompressFormat.JPEG, 72, new FileOutputStream(f.getPath()));
        }
        catch (Exception e) {
        	Log.d("TPad debug","Couldn't write file.");
        }
	}
	@Override
	public void handleEvent(MotionEvent event) {
		takeScreenShot();
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
}
