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
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import net.protyposis.android.spectaculum.InputSurfaceHolder;
import net.protyposis.android.spectaculum.SpectaculumView;

import java.io.IOException;

/**
 * Created by Mario on 16.08.2016.
 */
public class MediaPlayerActivity extends SpectaculumDemoBaseActivity implements
        InputSurfaceHolder.Callback {

    private static final String TAG = MediaPlayerActivity.class.getSimpleName();

    private SpectaculumView mVideoView;
    private MediaPlayer mMediaPlayer;

    private Uri mVideoUri;
    private int mVideoPosition;
    private boolean mVideoPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mediaplayer);
        super.onCreate(savedInstanceState);

        mVideoView = (SpectaculumView) findViewById(R.id.spectaculum);
        mVideoView.getInputHolder().addCallback(this);

        initMediaController(new MediaPlayerControl());

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
    public void surfaceCreated(InputSurfaceHolder holder) {
        try {
            initPlayer();
        } catch (IOException e) {
            Log.e(TAG, "error initializing player", e);
        }
    }

    @Override
    public void surfaceDestroyed(InputSurfaceHolder holder) {
        // Stop playback so no video frames get written to the now invalid surface causing an exception
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    private void initPlayer() throws IOException {
        setMediaUri(mVideoUri);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(mVideoView.getInputHolder().getSurface());
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vp) {
                mMediaPlayer.seekTo(mVideoPosition);

                if (mVideoPlaying) {
                    mMediaPlayer.start();
                }

                hideProgressIndicator();
                getMediaControllerWidget().setEnabled(true);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MediaPlayerActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                hideProgressIndicator();
                getMediaControllerWidget().setEnabled(false);
                return true;
            }
        });
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                mVideoView.updateResolution(width, height);
                mVideoView.requestLayout();
            }
        });
        mMediaPlayer.setDataSource(this, mVideoUri);
        mMediaPlayer.prepareAsync();

        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-mediaplayerview"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoView != null) {
            mVideoPosition = mMediaPlayer.getCurrentPosition();
            mVideoPlaying = mMediaPlayer.isPlaying();
            // the uri is stored in the base activity
            outState.putInt("position", mVideoPosition);
            outState.putBoolean("playing", mVideoPlaying);
        }
    }

    @Override
    protected void onStop() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onStop();
    }

    private class MediaPlayerControl implements MediaController.MediaPlayerControl {
        @Override
        public void start() {
            if (mMediaPlayer != null) mMediaPlayer.start();
        }

        @Override
        public void pause() {
            if (mMediaPlayer != null) mMediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
        }

        @Override
        public int getCurrentPosition() {
            return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
        }

        @Override
        public void seekTo(int pos) {
            if (mMediaPlayer != null) mMediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return mMediaPlayer != null ? mMediaPlayer.getAudioSessionId() : 0;
        }
    }
}
