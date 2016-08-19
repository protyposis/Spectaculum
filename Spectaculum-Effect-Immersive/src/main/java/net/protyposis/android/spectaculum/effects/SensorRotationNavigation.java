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
public class SensorRotationNavigation implements SensorEventListener {

    private static final String TAG = SensorRotationNavigation.class.getSimpleName();

    private Context mContext;
    private EquirectangularSphereEffect mEffect;
    private IntegerParameter mParameter;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean mActive;
    private float[] mRotationMatrix = new float[16];
    private float[] mRemappedRotationMatrix = new float[16];

    public SensorRotationNavigation(Context context) throws Exception {
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
        mParameter = new IntegerParameter("SensorNav", 0, 1, 0, new IntegerParameter.Delegate() {
            @Override
            public void setValue(final Integer value) {
                // Activate/deactivate on UI thread
                // Parameters are usually set on the GL thread, so we need to transfer this back to the UI thread
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        mActive = (value == 1);
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
    public void attachTo(EquirectangularSphereEffect effect) throws Exception {
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
    }

    /**
     * Deactivates sensor input. Should be called when pausing a fragment or activity.
     */
    public void deactivate() {
        mSensorManager.unregisterListener(this);
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
