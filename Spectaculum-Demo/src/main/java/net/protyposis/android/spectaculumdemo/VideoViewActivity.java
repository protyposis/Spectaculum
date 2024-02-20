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

package net.protyposis.android.spectaculumdemo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import net.protyposis.android.spectaculum.VideoView;

/**
 * Created by Mario on 16.08.2016.
 */
public class VideoViewActivity extends SpectaculumDemoBaseActivity {

    private static final String TAG = VideoViewActivity.class.getSimpleName();

    private VideoView mVideoView;

    private Uri mVideoUri;
    private int mVideoPosition;
    private boolean mVideoPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_videoview);
        super.onCreate(savedInstanceState);

        mVideoView = (VideoView) findViewById(R.id.spectaculum);
        initMediaController(mVideoView);

        // Init video playback state (will eventually be overwritten by saved instance state)
        mVideoUri = getIntent().getData();
        mVideoPosition = 0;
        mVideoPlaying = false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVideoUri = savedInstanceState.getParcelable("uri");
        mVideoPosition = savedInstanceState.getInt("position");
        mVideoPlaying = savedInstanceState.getBoolean("playing");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer();
    }

    private void initPlayer() {
        setMediaUri(mVideoUri);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vp) {
                hideProgressIndicator();
                getMediaControllerWidget().setEnabled(true);
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoViewActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                hideProgressIndicator();
                getMediaControllerWidget().setEnabled(false);
                return true;
            }
        });
        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-androidvideoview"));
        mVideoView.setVideoURI(mVideoUri);
        mVideoView.seekTo(mVideoPosition > 0 ? mVideoPosition : 0);
        if (mVideoPlaying) {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        mVideoPosition = mVideoView.getCurrentPosition();
        mVideoPlaying = mVideoView.isPlaying();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // the uri is stored in the base activity
        outState.putInt("position", mVideoPosition);
        outState.putBoolean("playing", mVideoPlaying);
    }
}
