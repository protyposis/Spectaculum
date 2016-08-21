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

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.protyposis.android.mediaplayer.MediaPlayer;
import net.protyposis.android.mediaplayer.UriSource;
import net.protyposis.android.spectaculum.MediaPlayerExtendedView;


public class MediaPlayerExtendedViewActivity extends SpectaculumDemoBaseActivity {

    private static final String TAG = MediaPlayerExtendedViewActivity.class.getSimpleName();

    private MediaPlayerExtendedView mVideoView;

    private Uri mVideoUri;
    private int mVideoPosition;
    private float mVideoPlaybackSpeed;
    private boolean mVideoPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mediaplayerextendedview);
        super.onCreate(savedInstanceState);

        mVideoView = (MediaPlayerExtendedView) findViewById(R.id.spectaculum);
        initMediaController(mVideoView);

        // Init video playback state (will eventually be overwritten by saved instance state)
        mVideoUri = getIntent().getData();
        mVideoPosition = 0;
        mVideoPlaybackSpeed = 0;
        mVideoPlaying = false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVideoUri = savedInstanceState.getParcelable("uri");
        mVideoPosition = savedInstanceState.getInt("position");
        mVideoPlaybackSpeed = savedInstanceState.getInt("playbackSpeed");
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
                Toast.makeText(MediaPlayerExtendedViewActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                hideProgressIndicator();
                getMediaControllerWidget().setEnabled(false);
                return true;
            }
        });
        mVideoView.setOnSeekListener(new MediaPlayer.OnSeekListener() {
            @Override
            public void onSeek(MediaPlayer mp) {
                showProgressIndicator();
            }
        });
        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                hideProgressIndicator();
            }
        });
        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-mediaplayerextended"));
        mVideoView.setVideoSource(new UriSource(this, mVideoUri));
        mVideoView.setPlaybackSpeed(mVideoPlaybackSpeed);
        mVideoView.seekTo(mVideoPosition > 0 ? mVideoPosition : 0);
        if (mVideoPlaying) {
            mVideoView.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mediaplayerextended, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_slowspeed) {
            mVideoView.setPlaybackSpeed(0.2f);
            return true;
        } else if(id == R.id.action_halfspeed) {
            mVideoView.setPlaybackSpeed(0.5f);
            return true;
        } else if(id == R.id.action_doublespeed) {
            mVideoView.setPlaybackSpeed(2.0f);
            return true;
        } else if(id == R.id.action_quadspeed) {
            mVideoView.setPlaybackSpeed(4.0f);
            return true;
        } else if(id == R.id.action_normalspeed) {
            mVideoView.setPlaybackSpeed(1.0f);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoView != null) {
            mVideoPosition = mVideoView.getCurrentPosition();
            mVideoPlaybackSpeed = mVideoView.getPlaybackSpeed();
            mVideoPlaying = mVideoView.isPlaying();
            // the uri is stored in the base activity
            outState.putInt("position", mVideoPosition);
            outState.putFloat("playbackSpeed", mVideoView.getPlaybackSpeed());
            outState.putBoolean("playing", mVideoPlaying);
        }
    }
}
