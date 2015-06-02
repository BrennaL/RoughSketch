/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.markers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import nxr.tpad.lib.TPad;

public class ZoomTouchView extends View {
    public static final String TAG = Slate.TAG;

    public static final boolean DEBUG = false;
    public static final boolean DEBUG_OVERLAY = DEBUG;

    private static final boolean DOUBLE_TAP_FATBITS = false;
    public static final float DOUBLE_TAP_ZOOM_LEVEL = 4f;

    private Slate mSlate;
    private float[] mTouchPoint = new float[2]; // screen coordinates
    private float[] mTouchPointDoc = new float[2]; // doc coordinates
    private double mInitialSpan = 1.0f;
    private long mTouchTime;

    private float[] mInitialPos = new float[2];

    private Matrix mInitialZoomMatrix = new Matrix();
    private Paint mZoomPaint;

    //TPad members
    private TPad mTpad;
    private TPadZoomTouchHandler tPadZoomTouch;


    public ZoomTouchView(Context context, TPad mTpad) {
        this(context, null, mTpad);
    }

    public ZoomTouchView(Context context, AttributeSet attrs, TPad mTpad) {
        this(context, attrs, 0, mTpad);
    }

    public ZoomTouchView(Context context, AttributeSet attrs, int defStyle, TPad mTpad) {
        super(context, attrs, defStyle);

        setTpad(mTpad);

        mZoomPaint = new Paint();
        mZoomPaint.setTextSize(25f);
    }

    float[] getCenter(MotionEvent event, float[] pt) {
        int P = event.getPointerCount();
        pt = ((pt == null) ? new float[2] : pt);
        pt[0] = event.getX(0);
        pt[1] = event.getY(0);
//        final int zero[] = { 0, 0 };
//        getLocationOnScreen(zero);
        for (int j = 1; j < P; j++) {
            pt[0] += event.getX(j); // + zero[0];
            pt[1] += event.getY(j); // + zero[1];
        }
        pt[0] /= P;
        pt[1] /= P;
        return pt;
    }
    double getSpan(MotionEvent event) {
        int P = event.getPointerCount();
        if (P < 2) return 0;

        final double x0 = event.getX(0); // + zero[0];
        final double x1 = event.getX(1); // + zero[0];
        final double y0 = event.getY(0); // + zero[1];
        final double y1 = event.getY(1); // + zero[1];
        final double span = Math.hypot(x1 - x0, y1 - y0);

        return span; 
    }

    private void doubleClick(MotionEvent event) {
        // this is still broken
        if (DOUBLE_TAP_FATBITS) {
            final float density = mSlate.getDrawingDensity();
            final float scale = 1f/density;
            final Matrix m = new Matrix();
            mTouchPointDoc[0] = mTouchPointDoc[1] = 0f;
            if (getScale(mSlate.getZoom()) == scale) {
                getCenter(event, mTouchPoint);
                mTouchPoint[0] *= density;
                mTouchPoint[1] *= density;
                mTouchPointDoc[0] = mTouchPoint[0] - mSlate.getZoomPosX();
                mTouchPointDoc[1] = mTouchPoint[1] - mSlate.getZoomPosY();
                mSlate.getZoomInv().mapPoints(mTouchPointDoc);
                m.preScale(scale*DOUBLE_TAP_ZOOM_LEVEL, scale*DOUBLE_TAP_ZOOM_LEVEL);

                mSlate.setZoomPosNoInval(mTouchPointDoc);
                mSlate.setZoom(m);
            } else {
                mSlate.resetZoom();
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        if (isEnabled()) {
            ViewConfiguration vc = ViewConfiguration.get(getContext());
            //final Matrix currentM = mSlate.getMatrix();

            if (action == MotionEvent.ACTION_DOWN
                    || action == MotionEvent.ACTION_POINTER_DOWN
                    || action == MotionEvent.ACTION_POINTER_UP
                    || action == MotionEvent.ACTION_UP) {
                mTouchPoint[0] = mTouchPoint[1] = -1f;
                if (action == MotionEvent.ACTION_DOWN) {
                    final long now = event.getEventTime();
                    if (now - mTouchTime < ViewConfiguration.getDoubleTapTimeout()) {
                        mTouchTime = 0;
                        doubleClick(event);
                    } else {
                        mTouchTime = now;
                    }
                } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                    mTouchTime = 0; // no double-tap for other fingers
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (mTouchPoint[0] < 0) {
                    mInitialZoomMatrix.set(mSlate.getZoom());
                    mSlate.getZoomPos(mInitialPos);

                    mInitialSpan = getSpan(event);
                    getCenter(event, mTouchPoint);
                    mTouchPointDoc[0] = mTouchPoint[0] - mSlate.getZoomPosX();
                    mTouchPointDoc[1] = mTouchPoint[1] - mSlate.getZoomPosY();
                    mSlate.getZoomInv().mapPoints(mTouchPointDoc);
                }
                if (mInitialSpan != 0) {
                    double span = getSpan(event);
                    float scale = (float) (span / mInitialSpan);

                    if (scale != 0f) {
                        Matrix m = new Matrix(mInitialZoomMatrix);
                        final float currentScale = getScale(m);

                        scale = Math.max(Math.min(scale*currentScale, 20.0f), 0.1f)
                                    / currentScale;

                        m.preScale(scale, scale, mTouchPointDoc[0], mTouchPointDoc[1]);

                        mSlate.setZoom(m);
                    }
                }

                float[] newCenter = getCenter(event, null);
                final float dx = newCenter[0] - mTouchPoint[0];
                final float dy = newCenter[1] - mTouchPoint[1];
                mSlate.setZoomPos(mInitialPos[0] + dx,
                        mInitialPos[1] + dy);

                if (Math.hypot(dx, dy) > vc.getScaledTouchSlop()) {
                    mTouchTime = 0; // no double tap now
                }
            }
            //handleTPad
            onZoomEventTPad(event);
            if (DEBUG_OVERLAY) invalidate();
            return true;
        }
        return false;
    }

    public void setSlate(Slate slate) {
        mSlate = slate;
    }

    private static final float[] mvals = new float[9];
    public static float getScale(Matrix m) {
        m.getValues(mvals);
        return mvals[0];
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isEnabled()) return;
        
        final Matrix m = mSlate.getZoom();
        final int x = (int) mSlate.getZoomPosX();
        final int y = (int) mSlate.getZoomPosY();

        //final float scale = m.mapRadius(1f);
        final float scale = getScale(m);
        canvas.drawText(String.format("%d%% %+d,%+d",
                            (int)(scale * 100f),
                            x,
                            y),
                canvas.getWidth() - 200, canvas.getHeight() - 20, mZoomPaint);
        
        if (!DEBUG_OVERLAY) return;

        setVisibility(View.VISIBLE);
        //canvas.drawColor(0xFFFFFF00);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setAlpha(0.5f);
        }
        Paint pt = new Paint();
        pt.setFlags(Paint.ANTI_ALIAS_FLAG);
        pt.setTextSize(20f);
        pt.setColor(0x80FF0000);
        if (mTouchPoint[0] != 0)
        canvas.drawCircle(mTouchPoint[0], mTouchPoint[1], 50 * (float) mSlate.getScaleX(), pt);
    }


    //TPad Methods

    // BEGIN MERGE OF FrictionMapView FILE:
    // Called by creating activity to initialize the local TPad reference object
    public void setTpad(TPad tpad) {
        try {
            tpad.toString();
        } catch (NullPointerException ex) {
            Log.w("FrictionMapView", "Warning: This view is being passed a null tpad! Expect no friction!");
            ex.printStackTrace();
        }
        this.mTpad = tpad;
        this.tPadZoomTouch = new TPadZoomTouchHandler(mTpad, this);
    }


    /**
     * Handles zoom events for the TPad separate from the motion event.
     * @author oliver
     * @param  event
     */
    public boolean onZoomEventTPad(MotionEvent event) {
        try {
            tPadZoomTouch.handleEvent(event);
        }
        catch (Exception e) {
            Log.d("FDebug", "motionEvent failed");
        }
        return true;
    }
}
