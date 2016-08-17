package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.PlayerControl;

import net.protyposis.android.spectaculum.InputSurfaceHolder;
import net.protyposis.android.spectaculum.SpectaculumView;

import java.io.IOException;

/**
 * Created by Mario on 17.08.2016.
 */
public class ExoPlayerActivity extends Activity implements
        InputSurfaceHolder.Callback, MediaController.MediaPlayerControl {

    private static final String TAG = ExoPlayerActivity.class.getSimpleName();

    private Uri mVideoUri;
    private SpectaculumView mVideoView;
    private ProgressBar mProgress;
    private Bundle mSavedInstanceState;

    private MediaController mMediaController;
    private ExoPlayer mExoPlayer;
    private PlayerControl mExoPlayerControl;

    private EffectManager mEffectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        mVideoView = (SpectaculumView) findViewById(R.id.videoview);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        /*
         * Disable touch event processing in the view to receive the event in this activity. This way,
         * we can intercept single touch events to toggle the media controller and then pass them back
         * to the view for further processing.
         */
        mVideoView.setTouchEnabled(false);
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
        final boolean[] waitingForFirstOnPrepared = {true};

        // Set up an ExoPlayer
        // This looks really messy but serves the purpose
        mExoPlayer = ExoPlayer.Factory.newInstance(2);
        int BUFFER_SEGMENT_SIZE = 64 * 1024;
        int BUFFER_SEGMENT_COUNT = 256;
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(this, "Spectaculum-Demo");
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                uri, dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        MediaCodecVideoTrackRenderer.EventListener videoEventListener = new MediaCodecVideoTrackRenderer.EventListener() {
            @Override
            public void onDroppedFrames(int count, long elapsed) {}

            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                mVideoView.updateResolution(width, height);
            }

            @Override
            public void onDrawnToSurface(Surface surface) {}

            @Override
            public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {}

            @Override
            public void onCryptoError(MediaCodec.CryptoException e) {}

            @Override
            public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {}
        };
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
                this, sampleSource, MediaCodecSelector.DEFAULT,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 1000, new Handler(), videoEventListener, 10);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                sampleSource, MediaCodecSelector.DEFAULT);
        mExoPlayer.prepare(videoRenderer, audioRenderer);
        mExoPlayer.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE,
                mVideoView.getInputHolder().getSurface());
        mExoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == ExoPlayer.STATE_READY && waitingForFirstOnPrepared[0]) {
                    if (position > 0) {
                        mExoPlayerControl.seekTo(position);
                    }

                    if (playback) {
                        mExoPlayerControl.start();
                    }

                    mProgress.setVisibility(View.GONE);
                    mMediaController.setEnabled(true);

                    /* Because we want to replicate MediaPlayer's OnPreparedListener that is only
                     * fired once when the player is ready, we set this value to false to not
                     * execute this block again. */
                    waitingForFirstOnPrepared[0] = false;
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {}

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(ExoPlayerActivity.this,
                        "Cannot play the video, see logcat for the detailed exception",
                        Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.GONE);
                mMediaController.setEnabled(false);
            }
        });
        mExoPlayerControl = new PlayerControl(mExoPlayer);

        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-exoplayerview"));
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
        mExoPlayer.release();
        mMediaController.hide();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoUri != null) {
            outState.putParcelable("uri", mVideoUri);
            outState.putBoolean("playing", mExoPlayerControl.isPlaying());
            outState.putInt("position", mExoPlayerControl.getCurrentPosition());
        }
    }

    @Override
    public void start() {
        if (mExoPlayer != null) mExoPlayerControl.start();
    }

    @Override
    public void pause() {
        if (mExoPlayer != null) mExoPlayerControl.pause();
    }

    @Override
    public int getDuration() {
        return mExoPlayer != null ? (int)mExoPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mExoPlayer != null ? (int)mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mExoPlayer != null) mExoPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mExoPlayer != null && mExoPlayerControl.isPlaying();
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
        return mExoPlayer != null ? mExoPlayerControl.getAudioSessionId() : 0;
    }

}
