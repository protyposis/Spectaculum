/*
 * Copyright (c) 2014 Mario Guggenberger <mario.guggenberger@aau.at>
 *
 * This file is part of ITEC MediaPlayer.
 *
 * ITEC MediaPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ITEC MediaPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ITEC MediaPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.aau.itec.android.mediaplayerdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import java.io.File;

import at.aau.itec.android.mediaplayer.GLImageView;
import at.aau.itec.android.mediaplayerdemo.R;

public class GLImageViewActivity extends Activity {

    private GLImageView mGLImageView;
    private GLEffects mEffectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glimageview);

        mGLImageView = (GLImageView) findViewById(R.id.imageview);
        mGLImageView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "glimageview"));

        mEffectList = new GLEffects(this, R.id.parameterlist, mGLImageView);
        mEffectList.addEffects();

        //test.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        Bitmap bmp = BitmapFactory.decodeFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "glcameraview1410439388970.png").getPath());
        mGLImageView.setImageBitmap(bmp);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.glvideoview, menu);
        mEffectList.addToMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(mEffectList.doMenuActions(item)) {
            return true;
        } else if(id == R.id.action_save_frame) {
            mGLImageView.captureFrame();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // hand the event to the image view to process zoom/pan gestures
        mGLImageView.onTouchEvent(event);

        return super.onTouchEvent(event);
    }
}
