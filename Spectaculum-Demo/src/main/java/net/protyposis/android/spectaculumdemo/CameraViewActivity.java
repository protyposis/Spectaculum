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

package net.protyposis.android.spectaculumdemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.protyposis.android.spectaculum.CameraView;


public class CameraViewActivity extends SpectaculumDemoBaseActivity {

    private static final String TAG = CameraViewActivity.class.getSimpleName();

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cameraview);
        super.onCreate(savedInstanceState);

        mCameraView = (CameraView) findViewById(R.id.spectaculum);
        mCameraView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-camera"));
        mCameraView.setTouchEnabled(true); // enable zoom&pan
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_switch_camera).setVisible(mCameraView.supportsCameraSwitch());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_switch_camera) {
            mCameraView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }
}
