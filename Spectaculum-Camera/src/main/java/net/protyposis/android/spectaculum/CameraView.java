/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
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

package net.protyposis.android.spectaculum;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * @author Mario Guggenberger
 */
public class CameraView extends SpectaculumView {

    private static final String TAG = CameraView.class.getSimpleName();

    private Camera mCamera;
    private int mCameraId;

    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mCameraId = 0;
        if(!checkCameraHardware(context)) {
            Log.w(TAG, "no camera present");
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if(isInEditMode()) {
            // there's no camera in the layout editor available
            return false;
        }
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onInputSurfaceCreated(InputSurfaceHolder inputSurfaceHolder) {
        startCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCamera();
        super.surfaceDestroyed(holder);
    }

    @Override
    public void onPause() {
        stopCamera();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startCamera() {
        try {
            if(mCamera == null) {
                mCamera = Camera.open(mCameraId);

                // enable autofocus if available
                List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
                if(supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    mCamera.setParameters(params);
                }

                // set orientation
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCameraId, info);
                WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
                int rotation = windowManager.getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0: degrees = 0; break;
                    case Surface.ROTATION_90: degrees = 90; break;
                    case Surface.ROTATION_180: degrees = 180; break;
                    case Surface.ROTATION_270: degrees = 270; break;
                }
                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360;
                } else {
                    result = (info.orientation - degrees + 360) % 360;
                }
                mCamera.setDisplayOrientation(result);

                // setup preview
                mCamera.setPreviewTexture(getInputHolder().getSurfaceTexture());
                mCamera.startPreview();

                int width, height;
                if(result == 0 || result == 180) {
                    width = mCamera.getParameters().getPreviewSize().width;
                    height = mCamera.getParameters().getPreviewSize().height;
                } else {
                    // swap width/height in portrait mode for a correct aspect ratio
                    height = mCamera.getParameters().getPreviewSize().width;
                    width = mCamera.getParameters().getPreviewSize().height;
                }

                updateResolution(width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCamera() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public boolean supportsCameraSwitch() {
        return Camera.getNumberOfCameras() > 1;
    }

    public void switchCamera() {
        mCameraId = ((mCameraId + 1) % Camera.getNumberOfCameras());
        stopCamera();
        startCamera();
    }
}
