package com.google.android.apps.markers;

import android.view.MotionEvent;

public class DefaultBrush extends Brush {
    private float gestureIncrement = 0.03f;
    
	public float startGesture(MotionEvent event) {
		super.frictionValue = 1.0f;
		return super.getFrictionValue();
	}
	
	public float continueGesture(MotionEvent event) {
		if (super.frictionValue > this.gestureIncrement) {
			super.frictionValue -= this.gestureIncrement;
		}
		return super.getFrictionValue();
	}
	
    public float endGesture(MotionEvent event) {
    	return super.getFrictionValue();
	}
    
    public float defaultFriction() {
    	return super.getFrictionValue();
	}
}
