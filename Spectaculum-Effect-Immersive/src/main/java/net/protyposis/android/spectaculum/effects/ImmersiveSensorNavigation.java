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

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import net.protyposis.android.spectaculum.gles.GLUtils;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

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
    private int mScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float[] mDeltaRotationVector = new float[4];
    float[] deltaRotationMatrix  = new float[16];
    float[] rotationCurrent = new float[]{
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1
    };
    float[] resultRotation = new float[16];
    private float timestamp;

    /**
     * Creates a sensor navigation instance for the immersive effect.
     * @param context context providing the sensor manager
     * @throws RuntimeException if no rotation sensor is available
     */
    public ImmersiveSensorNavigation(Context context) throws RuntimeException {
        mContext = context;

        // Get sensor
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(mSensor == null) {
            throw new RuntimeException("No rotation sensor available");
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
     * @throws RuntimeException thrown if there is already an effect attached
     */
    public void attachTo(ImmersiveEffect effect) throws RuntimeException {
        if(mEffect != null) {
            throw new RuntimeException("Previous effect is still attached, call detach() first");
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

    /**
     * Sets screen orientation.Must be set before use ImmersiveSensorNavigation
     * @param orientation activity's requested screen orientation
     */
    public void setScreenOrientation(int orientation){
        mScreenOrientation = orientation;
    }

    /**
     * Simplifies to portrait or landscape screen orientation
     * and returns screen orientation
     * @return true if screen orientation related to portrait
     */
    private boolean isRequestedOrientationPortrait(){
        ////FIXME it can be not full list of portrait orientations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                    mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT ||
                    mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT ||
                    mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
        } else {
            return mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                    mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT ||
                    mScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mEffect != null && mActive) {
            if(timestamp > 0){
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = isRequestedOrientationPortrait() ? event.values[0] : event.values[1];
                float axisY = isRequestedOrientationPortrait() ? event.values[1] : event.values[0];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (omegaMagnitude > 2.7f) {
                    axisX /= omegaMagnitude;
                    axisY /= omegaMagnitude;
                    axisZ /= omegaMagnitude;
                }

                // Integrate around this axis with the angular speed by the time step
                // in order to get a delta rotation from this sample over the time step
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                float sinThetaOverTwo = sin(thetaOverTwo);
                float cosThetaOverTwo = cos(thetaOverTwo);
                mDeltaRotationVector[0] = sinThetaOverTwo * axisX ;
                mDeltaRotationVector[1] = sinThetaOverTwo * axisY;
                mDeltaRotationVector[2] = sinThetaOverTwo * axisZ;
                mDeltaRotationVector[3] = cosThetaOverTwo;

            }
            timestamp = event.timestamp;

            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, mDeltaRotationVector);

            Matrix.multiplyMM(rotationCurrent, 0, deltaRotationMatrix, 0, rotationCurrent, 0);

            Matrix.invertM(resultRotation,0,rotationCurrent,0);

            mEffect.setRotationMatrix(resultRotation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}