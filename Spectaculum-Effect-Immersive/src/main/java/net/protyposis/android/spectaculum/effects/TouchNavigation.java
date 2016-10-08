/*
 * Copyright 2016 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.protyposis.android.spectaculum.effects;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import net.protyposis.android.spectaculum.LibraryHelper;
import net.protyposis.android.spectaculum.SpectaculumView;

/**
 * Created by Mario on 08.10.2016.
 */
public class TouchNavigation implements View.OnTouchListener, GestureDetector.OnGestureListener {

    private static final String TAG = TouchNavigation.class.getSimpleName();

    private SpectaculumView mSpectaculumView;
    private boolean mSpectaculumViewTouchEnabled;
    private GestureDetector mGestureDetector;
    private float mPanX;
    private float mPanY;
    private EquirectangularSphereEffect mEffect;
    private BooleanParameter mParameter;
    private boolean mActive;

    public TouchNavigation(SpectaculumView spectaculumView) throws Exception {
        mSpectaculumView = spectaculumView;

        if(mSpectaculumView == null) {
            throw new Exception("No Spectaculum view supplied");
        }

        mGestureDetector = new GestureDetector(mSpectaculumView.getContext(), this);

        // Make a UI handler for activation state toggling
        final Handler h = new Handler();

        // Create an effect parameter to toggle the touch navigation on/off
        mParameter = new BooleanParameter("TouchNav", false, new BooleanParameter.Delegate() {
            @Override
            public void setValue(final Boolean value) {
                // Activate/deactivate on UI thread
                // Parameters are usually set on the GL thread, so we need to transfer this back to the UI thread
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        mActive = value;
                        if(mActive) {
                            activate();
                        } else {
                            deactivate();
                        }
                    }
                });
            }
        });
    }

    public void attachTo(EquirectangularSphereEffect effect) throws Exception {
        if(mEffect != null) {
            throw new Exception("Previous effect is still attached, call detach() first");
        }
        mEffect = effect;
        mEffect.addParameter(mParameter);
    }

    public void detach() {
        mEffect.removeParameter(mParameter);
        mEffect = null;
    }

    public void activate() {
        mSpectaculumView.setOnTouchListener(this);

        // Store touch enabled state and enable touch which is required for this to work
        mSpectaculumViewTouchEnabled = mSpectaculumView.isTouchEnabled();
        mSpectaculumView.setTouchEnabled(true);
    }

    public void deactivate() {
        mSpectaculumView.setOnTouchListener(null);
        mSpectaculumView.setTouchEnabled(mSpectaculumViewTouchEnabled);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Scale the scroll/panning distance to rotation degrees
        // The view's with and height are mapped to 180 degree each
        // TODO map motion event positions from view to the rendered sphere and derive rotation
        //      angles to keep touchscreen positions and sphere positions in sync
        mPanX += distanceX / mSpectaculumView.getWidth() * 180f;
        mPanY += distanceY / mSpectaculumView.getHeight() * 180f;

        // Clamp horizontal rotation to avoid rotations beyond 90 degree which inverts the vertical
        // rotation and makes rotation handling more complicated
        mPanY = LibraryHelper.clamp(mPanY, -90, 90);

        // Apply the panning to the viewport
        // Horizontal panning along the view's X axis translates to a rotation around the viewport's Y axis
        // Vertical panning along the view's Y axis translates to a rotation around the viewport's X axis
        ((FloatParameter) mEffect.getParameters().get(0)).setValue(-mPanY);
        ((FloatParameter) mEffect.getParameters().get(1)).setValue(mPanX);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
