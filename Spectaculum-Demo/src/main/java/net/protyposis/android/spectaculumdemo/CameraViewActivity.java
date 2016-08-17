/*
 * Copyright (c) 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * This file is part of MediaPlayer-Extended.
 *
 * MediaPlayer-Extended is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MediaPlayer-Extended is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MediaPlayer-Extended.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
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
