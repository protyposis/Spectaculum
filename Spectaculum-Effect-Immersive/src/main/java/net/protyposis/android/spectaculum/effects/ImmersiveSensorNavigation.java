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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Mario on 18.08.2016.
 */
public class ImmersiveSensorNavigation implements SensorEventListener {

    private static final String TAG = ImmersiveSensorNavigation.class.getSimpleName();

    private Context mContext;
    private ImmersiveEffect mEffect;
    private BooleanParameter mParameter;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean mActive;
    private float[] mRotationMatrix = new float[16];
    private float[] mRemappedRotationMatrix = new float[16];

    public ImmersiveSensorNavigation(Context context) throws Exception {
        mContext = context;

        // Get sensor
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if(mSensor == null) {
            throw new Exception("No rotation sensor available");
        }

        // Make a UI handler for activation state toggling
        final Handler h = new Handler();

        // Create an effect parameter to toggle the sensor navigation on/off
        mParameter = new BooleanParameter("SensorNav", false, new BooleanParameter.Delegate() {
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

    /**
     * Attaches to the effect and adds parameter to toggle sensor navigation on/off.
     * @param effect the effect to attach sensor navigation to
     * @throws Exception thrown if there is already an effect attached
     */
    public void attachTo(ImmersiveEffect effect) throws Exception {
        if(mEffect != null) {
            throw new Exception("Previous effect is still attached, call detach() first");
        }
        mEffect = effect;
        mEffect.addParameter(mParameter);
    }

    /**
     * Detaches sensor navigation from an effect.
     */
    public void detach() {
        mEffect.removeParameter(mParameter);
        mEffect = null;
    }

    /**
     * Activates sensor input.
     */
    public void activate() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        mActive = true;
    }

    /**
     * Deactivates sensor input. Should be called when pausing a fragment or activity.
     */
    public void deactivate() {
        mSensorManager.unregisterListener(this);
        mActive = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mEffect != null && mActive) {
            // TODO understand those sensor coordinate spaces
            // TODO find out how the sensor rotation can be mapped to the sphere shader correctly
            // TODO should we store the initial rotation value to set the zero rotation point to the current phone rotation?

            // Get the rotation matrix from the sensor
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

            // Debug output
            //float[] orientation = new float[3];
            //SensorManager.getOrientation(mRotationMatrix, orientation);
            //debugOutputOrientationInDegree(orientation);

            // Some axes seem like they need to be exchanged
            // FIXME this does not seem to remap axes at all!?
            SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemappedRotationMatrix);

            // Update effect and thus the viewport too
            mEffect.setRotationMatrix(mRemappedRotationMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void debugOutputOrientationInDegree(float[] orientation) {
        double rad2deg = 180/Math.PI;
        float azimuth = (float)(orientation[0] * rad2deg); // -z
        float pitch = (float)(orientation[1] * rad2deg) ; // x
        float roll = (float)(orientation[2] * rad2deg); // y
        Log.d(TAG, azimuth + ", " + pitch + ", " + roll);
    }
}
