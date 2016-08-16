package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.protyposis.android.spectaculum.VideoView;

/**
 * Created by Mario on 16.08.2016.
 */
public class AndroidVideoViewActivity extends Activity {

    private static final String TAG = AndroidVideoViewActivity.class.getSimpleName();

    private Uri mVideoUri;
    private VideoView mVideoView;
    private ProgressBar mProgress;

    private MediaController.MediaPlayerControl mMediaPlayerControl;
    private MediaController mMediaController;

    private GLEffects mEffectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_androidvideoview);
        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        mVideoView = (VideoView) findViewById(R.id.videoview);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        mMediaPlayerControl = mVideoView;
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(findViewById(R.id.container));
        mMediaController.setMediaPlayer(mMediaPlayerControl);
        mMediaController.setEnabled(false);

        mProgress.setVisibility(View.VISIBLE);

        mEffectList = new GLEffects(this, R.id.parameterlist, mVideoView);
        mEffectList.addEffects();

        if(savedInstanceState != null) {
            initPlayer((Uri)savedInstanceState.getParcelable("uri"),
                    savedInstanceState.getInt("position"),
                    savedInstanceState.getBoolean("playing")
            );
        } else {
            initPlayer(getIntent().getData(), -1, false);
        }
    }

    private void initPlayer(Uri uri, final int position, final boolean playback) {
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
                Toast.makeText(AndroidVideoViewActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(false);
                return true;
            }
        });
        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-androidvideoview"));
        mVideoView.setVideoURI(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        mEffectList.addToMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(mEffectList.doMenuActions(item)) {
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
        }
    }
}
