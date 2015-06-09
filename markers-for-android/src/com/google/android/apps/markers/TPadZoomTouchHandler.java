package com.google.android.apps.markers;

import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import nxr.tpad.lib.TPad;

/**
 * A class that handles touch/zoom sensations
 * @author oliver
 */
public class TPadZoomTouchHandler {
	private TPad tPad;
	private View view;
	private Brush currentBrush;
	private Resources resources;
    VelocityTracker mVelocityTracker;

	public TPadZoomTouchHandler(TPad tPad, View view) {
		this.tPad = tPad;
		this.view = view;
		this.resources = view.getResources();
		currentBrush = new DefaultBrush(resources);
	}

	
	public void handleEvent(MotionEvent event) {
		// Values from 0.0f-1.0f are 0-100% tPad activation
//        float frequency = 1; //Hz
//        long t = System.currentTimeMillis();
//        float friction = (float)Math.sin(t/1000.0*frequency*2*Math.PI)/2+0.5f;

        float friction = 0;

        if (event.getPointerCount() >1)
        {
            //Zoom, etc.
            MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
            MotionEvent.PointerCoords coords2 = new MotionEvent.PointerCoords();

            event.getPointerCoords(0, coords1);
            event.getPointerCoords(1, coords2);

            float dx = coords1.x - coords2.x;
            float dy = coords1.y - coords2.y;

            double distance_squared = dx*dx + dy*dy;

            friction = 1.0f - (float) Math.max(0, Math.min( (distance_squared-4000) / 400000.0, 1));

        } else {
            //not zoom, mimic friction

            //Handy Velocity calculator
            //retrieved from:
            //https://developer.android.com/reference/android/support/v4/view/VelocityTrackerCompat.html
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);
            switch(action) {
                case MotionEvent.ACTION_DOWN:
                    if(mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.addMovement(event);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    mVelocityTracker.computeCurrentVelocity(1000);
                    double velocity = Math.sqrt(
                            mVelocityTracker.getXVelocity(0)*mVelocityTracker.getXVelocity(0)+
                                    mVelocityTracker.getYVelocity(0)*mVelocityTracker.getYVelocity(0));
                    friction = 0.5f*(float)Math.min(Math.max(0, (velocity-250)/1000.0),1);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Return a VelocityTracker object back to be re-used by others.
                    mVelocityTracker.recycle();
                    break;
            }

        }


		try {
            tPad.sendFriction(friction);
		}
		catch (Exception e) {
			Log.d("FDebug", "Send friction failed.");
		}
	}
}
