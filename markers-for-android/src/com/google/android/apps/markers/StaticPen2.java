package com.google.android.apps.markers;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.util.Log;

public class StaticPen2 extends Brush {
	
	public StaticPen2(Resources r) {
		super(r);
		
		super.gestureLength = 2;
		
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		
		this.generateWeights();
		
		// The texture of the pen 
		setDataBitmap(R.drawable.paper_texture);
	}
	
	
	@Override
	protected void generateWeights() {
		for (int i=0; i<super.gestureLength; i++) {
			super.gestureWeights[i] = 0.9f;
		}
	}
	
}
