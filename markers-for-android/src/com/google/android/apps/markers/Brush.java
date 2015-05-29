package com.google.android.apps.markers;

import android.view.MotionEvent;

public abstract class Brush {
	protected float frictionValue;
    private float defaultFrictionValue = 0.5f;

	/**
     * Returns current friction value
     * @author bucci
     */
	public float getFrictionValue() {
		return this.frictionValue;
	}
	/**
     * Sets current friction value.
     * @author bucci
     * @param f : will be cropped to 0.0-1.0 
     */
	public void setFrictionValue(float f) {
		if (f < 0) {
			this.frictionValue = 0.0f;
		}
		else if (f > 1) {
			this.frictionValue = 1.0f;
		}
		else {
			this.frictionValue = f;
		}
	}
	/**
     * Starts a texture gesture cycle by setting friction to 0.
     * @author bucci
     */
	public float startGesture(MotionEvent event) {
		return getFrictionValue();
	}
	/**
     * Continues a texture gesture cycle by incrementing friction.
     * @author bucci
     */
	public float continueGesture(MotionEvent event) {
		return getFrictionValue();
	}
	/**
     * Ends a texture gesture cycle.
     * @author bucci
     */
    public float endGesture(MotionEvent event) {
    	return getFrictionValue();
    }
    /**
     * Ends a texture gesture cycle.
     * @author bucci
     */
    public float defaultFriction() {
    	this.frictionValue = this.defaultFrictionValue;
    	return getFrictionValue();
    }
}