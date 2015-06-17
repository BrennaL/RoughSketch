package com.google.android.apps.markers;

import nxr.tpad.lib.TPadService;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.graphics.Bitmap;

public abstract class Brush {	
	// Holders of Haptic data
	public Bitmap dataBitmap;
	
	// Android velocity tracking object, used in predicting finger position
	public VelocityTracker vTracker;
	
	// Velocity and position variables of the finger. Get updated at approx 60Hz
	// when the user is touching
	public static float vy, vx;
	public static float py, px;
	
	// Prediction "Horizon", that is the number of samples needed to extrapolate
	// finger position until the next position is taken.
	// 125 samples, 20ms @ sample rate output
	public static final int PREDICT_HORIZON = (int) (TPadService.OUTPUT_SAMPLE_RATE * (.020f)); 
	
	// Array for holding the extrapolated pixel values
	public static float[] predictedPixels = new float[PREDICT_HORIZON];
	
   	// Index and array for g(x)
	protected int gestureTracker;
	protected int gestureLength;
   	protected float[] gestureWeights; // need to set in subclass init
   	
   	// Resources
   	protected Resources resources;
	
   	public Brush(Resources r) {
   		resources = r;
   	}
   	
	public void handleEvent(MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				try {
					startGesture(event);				
				}
				catch (Exception e) {
					Log.d("FDebug", "startGesture failed.");
				}
				break;
			case MotionEvent.ACTION_MOVE:
				gestureTracker++;

				try {
					continueGesture(event);
				}
				catch (Exception e) {
					Log.d("FDebug", "continueGesture failed.");
				}
				break;
			case MotionEvent.ACTION_UP:
				try {
					endGesture(event);
				}
				catch (Exception e) {
					Log.d("FDebug", "endGesture failed.");
				}
		
				break;
			case MotionEvent.ACTION_CANCEL:
				try {
					endGesture(event);
				}
				catch (Exception e) {
					Log.d("FDebug", "endGesture failed.");
				}
				break;
			default:
				try {
					endGesture(event);
				}
				catch (Exception e) {
					Log.d("FDebug", "endGesture failed.");
				}
				break;
		}
	}
	public void startGesture(MotionEvent event) {
		gestureTracker = 0;
		startPredictGesture(event);
	}
	public void continueGesture(MotionEvent event) {
		gestureTracker++;
		continuePredictGesture(event);
	}
	public void endGesture(MotionEvent event) {
		endPredictGesture();
	}
	/**
	 * The set of weights for a gesture.
	 * @author bucci
	 */
	protected void generateWeights() {};
	
	/**
	 * Return the weighted predicted pixel buffer.
	 */
	public float[] getPixelBuffer() {
		return predictedPixels;
	}
	
	/**
	 * Start the gesture tracking for a brush.
	 * @author bucci
	 * @param event
	 */
	public void startPredictGesture(MotionEvent event) {
		// Get position coordinates, and scale for proper dataBitmap size
		px = event.getX();// * hapticScaleFactor;
		py = event.getY();// * hapticScaleFactor;

		// Reset velocities to zero
		vx = 0;
		vy = 0;

		// Start a new velocity tracker
		if (vTracker == null) {
			vTracker = VelocityTracker.obtain();
		} else {
			vTracker.clear();
		}

		// Add first event to tracker
		vTracker.addMovement(event);
	}
	
	/**
	 * Continue gesture tracking for a brush.
	 * @param event
	 */
	public void continuePredictGesture(MotionEvent event) {
		// Get position coordinates, and scale for proper dataBitmap size
		px = event.getX();// * hapticScaleFactor;
		py = event.getY();// * hapticScaleFactor;

		// Add new motion even to the velocity tracker
		vTracker.addMovement(event);

		// Compute velocity in pixels/ms
		vTracker.computeCurrentVelocity(1);

		// Get computed velocities, and scale them appropriately
		vx = vTracker.getXVelocity();// * hapticScaleFactor;
		vy = vTracker.getYVelocity();// * hapticScaleFactor;

		// Call prediction algorithm below. This function computes the
		// proper extrapolation of friction values and automatically updates
		// predictedPixels with these values
		try {
			predictPixels();
		}
		catch (Exception e) {
			Log.d("FDebug", "Predict pixels failed.");
		}
	}

	public void endPredictGesture() {
		// Recycle velocity tracker on a cancel event
		vTracker.recycle();
		//set to null after recycle to prevent illegal state
		vTracker = null;
		for (int i=0; i < predictedPixels.length; i++) {
			predictedPixels[i] = 0.0f;
		}
	}

	// Main extrapolation algorithm used to calculate upsampled friction values
	// to the TPad
	public void predictPixels() {
		// Local friction values
		float friction = 0;

		// Local x,y values, based on most recent px, py
		int x = (int) px;
		int y = (int) py;

		// A frequency scaling factor to ensure we are producing the correct
		// number of samples to be played back
		float freqScaleFactor = (float) (1 / (TPadService.OUTPUT_SAMPLE_RATE / 1000.));

		// Main extrapolation loop. This is where the extrapolated data is
		// produced
		for (int i = 0; i < predictedPixels.length; i++) {

			// 1st order hold in x direction
			x = (int) (px + vx * i * freqScaleFactor);

			// Ensure we are not going off the edge of the bitmap with our
			// extrapolated point
			if (x >= dataBitmap.getWidth()) {
				x = dataBitmap.getWidth() - 1;
			} else if (x < 0)
				x = 0;

			// 1st order hold in y direction
			y = (int) (py + vy * i * freqScaleFactor);

			// Ensure we are not going off the edge of the bitmap with our
			// extrapolated point
			if (y >= dataBitmap.getHeight()) {
				y = dataBitmap.getHeight() - 1;
			} else if (y < 0)
				y = 0;

			friction = pixelToFriction(dataBitmap.getPixel(x, y));

			// Save the stored friction value into the buffer that will be sent
			// to the TPad to be played back as real-time as possible
			if ((i + gestureTracker) < gestureWeights.length) {
				predictedPixels[i] = friction * gestureWeights[i + gestureTracker];
			}
			else {
				predictedPixels[i] = friction * gestureWeights[gestureWeights.length - 1];
			}
		}
	}


	// Main mapping to go from a pixel color to a friction value. OVERRIDE THIS
	// FUNCTION FOR NEW MAPPINGS FROM COLOR TO FRICTION
	public float pixelToFriction(int pixel) {
		// Setup a Hue, Saturation, Value matrix
		float[] hsv = new float[3];

		// Convert the RGB color in to HSV data and store it
		Color.colorToHSV(pixel, hsv);

		// Return the Value of the color, which, generally, corresponds to the
		// grayscale value of the color from 0-1
		return hsv[2];
	}
	
	public void setDataBitmap(int uri) {
		// Create new bitmap from a copy of the reference. This ensures the
		// reference copy won't be used, and it can then be destroyed.
		Bitmap bmp = BitmapFactory.decodeResource(resources, uri);
		dataBitmap = null;
		dataBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
	}
	public void setDataBitmap(Bitmap image) {
		// Create new bitmap from a copy of the reference. This ensures the
		// reference copy won't be used, and it can then be destroyed.
		Bitmap imageWithBG = Bitmap.createBitmap(image.getWidth(), image.getHeight(),image.getConfig());  // Create another image the same size
		imageWithBG.eraseColor(Color.WHITE);  // set its background to white, or whatever color you want
		Canvas canvas = new Canvas(imageWithBG);  // create a canvas to draw on the new image
		canvas.drawBitmap(image, 0f, 0f, null); // draw old image on the background
		image.recycle();  // clear out old image 
		dataBitmap = null;
		dataBitmap = imageWithBG.copy(Bitmap.Config.ARGB_8888, true);
	}

	public void brushOff() {		
	}

	public void brushOn() {	
	}
}