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
	};
	/**
     * Starts a texture gesture cycle by setting friction to 0.
     * @author bucci
     */
	public float startGesture(MotionEvent event) {
		return getFrictionValue();
	};
	/**
     * Continues a texture gesture cycle by incrementing friction.
     * @author bucci
     */
	public float continueGesture(MotionEvent event) {
		return getFrictionValue();
	};
	/**
     * Ends a texture gesture cycle.
     * @author bucci
     */
    public float endGesture(MotionEvent event) {
    	return getFrictionValue();
    };
    /**
     * Ends a texture gesture cycle.
     * @author bucci
     */
    public float defaultFriction() {
    	this.frictionValue = this.defaultFrictionValue;
    	return getFrictionValue();
    };
}
