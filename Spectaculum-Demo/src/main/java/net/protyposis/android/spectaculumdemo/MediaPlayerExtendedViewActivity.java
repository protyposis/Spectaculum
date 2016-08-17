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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.protyposis.android.mediaplayer.MediaPlayer;
import net.protyposis.android.mediaplayer.MediaSource;
import net.protyposis.android.spectaculum.MediaPlayerExtendedView;


public class MediaPlayerExtendedViewActivity extends Activity {

    private static final String TAG = MediaPlayerExtendedViewActivity.class.getSimpleName();

    private Uri mVideoUri;
    private MediaPlayerExtendedView mVideoView;
    private ProgressBar mProgress;

    private MediaController.MediaPlayerControl mMediaPlayerControl;
    private MediaController mMediaController;

    private EffectManager mEffectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayerextendedview); // reuse main layout
        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        mVideoView = (MediaPlayerExtendedView) findViewById(R.id.videoview);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        /*
         * Disable touch event processing in the view to receive the event in this activity. This way,
         * we can intercept single touch events to toggle the media controller and then pass them back
         * to the view for further processing.
         */
        mVideoView.setTouchEnabled(false);

        mMediaPlayerControl = mVideoView;
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(findViewById(R.id.container));
        mMediaController.setMediaPlayer(mMediaPlayerControl);
        mMediaController.setEnabled(false);

        mProgress.setVisibility(View.VISIBLE);

        mEffectList = new EffectManager(this, R.id.parameterlist, mVideoView);
        mEffectList.addEffects();

        if(savedInstanceState != null) {
            initPlayer((Uri)savedInstanceState.getParcelable("uri"),
                    savedInstanceState.getInt("position"),
                    savedInstanceState.getFloat("playbackSpeed", 1.0f),
                    savedInstanceState.getBoolean("playing")
            );
        } else {
            initPlayer(getIntent().getData(), -1, 1.0f, false);
        }
    }

    private void initPlayer(Uri uri, final int position, final float speed, final boolean playback) {
        mVideoUri = uri;
        getActionBar().setSubtitle(mVideoUri+"");

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vp) {
                if (position > 0) {
                    mVideoView.seekTo(position);
                } else {
                    mVideoView.seekTo(0); // display first frame
                }

                mVideoView.setPlaybackSpeed(speed);

                if (playback) {
                    mVideoView.start();
                }

                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(true);
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MediaPlayerExtendedViewActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(false);
                return true;
            }
        });
        mVideoView.setOnSeekListener(new MediaPlayer.OnSeekListener() {
            @Override
            public void onSeek(MediaPlayer mp) {
                mProgress.setVisibility(View.VISIBLE);
            }
        });
        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mProgress.setVisibility(View.GONE);
            }
        });
        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-mediaplayerextended"));

        Utils.uriToMediaSourceAsync(this, uri, new Utils.MediaSourceAsyncCallbackHandler() {
            @Override
            public void onMediaSourceLoaded(MediaSource mediaSource) {
                mVideoView.setVideoSource(mediaSource);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "error loading video", e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        getMenuInflater().inflate(R.menu.mediaplayerextended, menu);
        mEffectList.addToMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
        } else if(id == R.id.action_seekcurrentposition) {
            mVideoView.pause();
            mVideoView.seekTo(mVideoView.getCurrentPosition());
            return true;
        } else if(id == R.id.action_seekcurrentpositionplus1ms) {
            mVideoView.pause();
            mVideoView.seekTo(mVideoView.getCurrentPosition()+1);
            return true;
        } else if(id == R.id.action_seektoend) {
            mVideoView.pause();
            mVideoView.seekTo(mVideoView.getDuration());
            return true;
        } else if(id == R.id.action_getcurrentposition) {
            Toast.makeText(this, "current position: " + mVideoView.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            return true;
        } else if(mEffectList.doMenuActions(item)) {
            return true;
        } else if(id == R.id.action_save_frame) {
            mVideoView.captureFrame();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            long durationMs = event.getEventTime() - event.getDownTime();
            /* The media controller is only getting toggled by simple taps.  If a certain amount of
             * time passes between the DOWN and UP actions, it can be considered as not being a
             * simple tap any more and the media controller is not getting toggled.
             */
            if(durationMs < 500) {
                if (mMediaController.isShowing()) {
                    mMediaController.hide();
                } else {
                    mMediaController.show();
                }
            }
        }

        // hand the event to the video view to process zoom/pan gestures
        mVideoView.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    protected void onStop() {
        mMediaController.hide();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mVideoUri != null) {
            outState.putParcelable("uri", mVideoUri);
            outState.putBoolean("playing", mVideoView.isPlaying());
            outState.putInt("position", mVideoView.getCurrentPosition());
            outState.putFloat("playbackSpeed", mVideoView.getPlaybackSpeed());
        }
    }
}
