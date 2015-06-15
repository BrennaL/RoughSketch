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

    private boolean single_finger_last = true;
    private double initial_distance = 0;

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

            double distance = Math.sqrt(dx*dx + dy*dy);

            if (single_finger_last)
            {
                //friction is relative to initial distance
                initial_distance = distance;
                friction = 0.8f;
            } else {

                double distance_squared = (distance - initial_distance)*(distance-initial_distance);
                friction = 0.8f - 0.8f*(float) Math.max(0, Math.min( (distance_squared) / 50000.0, 1));
            }

            single_finger_last = false;

        } else {
            //not zoom, mimic friction


            single_finger_last = true;
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

                    //VELOCITY CALCULATION

                    double velocity = Math.sqrt(
                            mVelocityTracker.getXVelocity(0)*mVelocityTracker.getXVelocity(0)+
                                    mVelocityTracker.getYVelocity(0)*mVelocityTracker.getYVelocity(0));
//                    friction = 0.5f*(float)Math.min(Math.max(0, (velocity-250)/1000.0),1);

                    //VELOCITY SQUARED CALCULATION
//                    double velocity_squared =  mVelocityTracker.getXVelocity(0)*mVelocityTracker.getXVelocity(0)+
//                                    mVelocityTracker.getYVelocity(0)*mVelocityTracker.getYVelocity(0);
//                    friction = 0.75f*(float)Math.min(Math.max(0, (velocity_squared-1000)/200000.0),1);

                    //VELOCITY SINGLE SINE CALCULATION
                    double MAX_VELOCITY = 1000;
                    //clamp velocity
                    velocity = Math.min(Math.max(0, velocity), MAX_VELOCITY);
                    //convert from domain [0, MAX_VELOCITY] to range [-PI/2, PI/2] for sigmoid-like shape
                    double velocity_sine = Math.sin(velocity/MAX_VELOCITY*Math.PI - Math.PI/2);
                    //shift to range [0, 1]
                    velocity_sine = velocity_sine/2 + 0.5;
                    friction = 0.5f*(float)velocity_sine;

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
