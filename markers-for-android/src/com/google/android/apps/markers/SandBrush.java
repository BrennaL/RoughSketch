package com.google.android.apps.markers;

import org.dsandler.apps.markers.R;

import android.content.res.Resources;
import android.util.Log;

public class SandBrush extends Brush {

		public SandBrush(Resources r) {		
		super(r);

		// Brush settings	
		// Length in number of event calls of a brush gesture 
		super.gestureLength = 10;
		// Initialize the weight array to the length of a brush gesture
		super.gestureWeights = new float[gestureLength];
		// Apply the g(x) function, where x is events since beginning of gesture
		this.generateWeights();
		// The texture of the brush 
		setDataBitmap(R.drawable.blacknwhite);		
	}
		
		@Override
		protected void generateWeights() {
			for (int i=0; i<super.gestureLength; i++) {
				if (i <= 2)
					super.gestureWeights[i] = 0f;
				else if( i > 2){
					super.gestureWeights[i] = g(0,gestureLength,i);
				}
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
				int range = (max - i) - min;
				float ret = ((float) i) / ((float) range);
				return ret;
			
		}

}
