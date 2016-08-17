package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.media.MediaPlayer;
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

import net.protyposis.android.spectaculum.InputSurfaceHolder;
import net.protyposis.android.spectaculum.SpectaculumView;

import java.io.IOException;

/**
 * Created by Mario on 16.08.2016.
 */
public class AndroidMediaPlayerActivity extends Activity implements
        InputSurfaceHolder.Callback, MediaController.MediaPlayerControl {

    private static final String TAG = AndroidMediaPlayerActivity.class.getSimpleName();

    private Uri mVideoUri;
    private SpectaculumView mVideoView;
    private ProgressBar mProgress;
    private Bundle mSavedInstanceState;

    private MediaController mMediaController;
    private MediaPlayer mMediaPlayer;

    private EffectManager mEffectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_androidmediaplayer);
        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        mVideoView = (SpectaculumView) findViewById(R.id.videoview);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        mVideoView.getInputHolder().addCallback(this);

        //mMediaPlayerControl = mVideoView;
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(findViewById(R.id.container));
        mMediaController.setMediaPlayer(this);
        mMediaController.setEnabled(false);

        mProgress.setVisibility(View.VISIBLE);

        mEffectList = new EffectManager(this, R.id.parameterlist, mVideoView);
        mEffectList.addEffects();

        mSavedInstanceState = savedInstanceState;
    }

    @Override
    public void surfaceCreated(InputSurfaceHolder holder) {
        try {
            if (mSavedInstanceState != null) {
                initPlayer((Uri) mSavedInstanceState.getParcelable("uri"),
                        mSavedInstanceState.getInt("position"),
                        mSavedInstanceState.getBoolean("playing")
                );
            } else {
                initPlayer(getIntent().getData(), -1, false);
            }
        } catch (IOException e) {
            Log.e(TAG, "error initializing player", e);
        }
    }

    @Override
    public void surfaceDestroyed(InputSurfaceHolder holder) {

    }

    private void initPlayer(Uri uri, final int position, final boolean playback) throws IOException {
        mVideoUri = uri;
        getActionBar().setSubtitle(mVideoUri + "");

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(mVideoView.getInputHolder().getSurface());
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vp) {
                if (position > 0) {
                    mMediaPlayer.seekTo(position);
                } else {
                    mMediaPlayer.seekTo(0); // display first frame
                }

                if (playback) {
                    mMediaPlayer.start();
                }

                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(true);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(AndroidMediaPlayerActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(false);
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
        mMediaPlayer.setDataSource(this, uri);
        mMediaPlayer.prepareAsync();

        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-mediaplayerview"));
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
        if (mEffectList.doMenuActions(item)) {
            return true;
        } else if (id == R.id.action_save_frame) {
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
            if (durationMs < 500) {
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
        mMediaPlayer.release();
        mMediaController.hide();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoUri != null) {
            outState.putParcelable("uri", mVideoUri);
            outState.putBoolean("playing", mMediaPlayer.isPlaying());
            outState.putInt("position", mMediaPlayer.getCurrentPosition());
        }
    }

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
