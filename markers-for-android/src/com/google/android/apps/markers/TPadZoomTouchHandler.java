package com.google.android.apps.markers;

import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import nxr.tpad.lib.TPad;

public class TPadZoomTouchHandler {
	private TPad tPad;
	private View view;
	private Brush currentBrush;
	private Resources resources;

    private long lastDownTimeMillis = 0;

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

        final int action = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
                ? event.getActionMasked()
                : event.getAction();

        if(action == MotionEvent.ACTION_DOWN)
        {
            lastDownTimeMillis = System.currentTimeMillis();
        }

        long t = System.currentTimeMillis();

        float friction = (float)Math.min((t - lastDownTimeMillis)/500.0, 1.0f);
		try {
            tPad.sendFriction(friction);
		}
		catch (Exception e) {
			Log.d("FDebug", "Send friction failed.");
		}
	}
}
