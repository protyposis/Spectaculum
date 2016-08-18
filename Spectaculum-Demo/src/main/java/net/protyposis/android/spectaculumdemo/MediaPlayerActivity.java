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
    private Bundle mSavedInstanceState;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mediaplayer);
        super.onCreate(savedInstanceState);

        mVideoView = (SpectaculumView) findViewById(R.id.spectaculum);
        mVideoView.getInputHolder().addCallback(this);

        initMediaController(new MediaPlayerControl());

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
        setMediaUri(uri);

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
        mMediaPlayer.setDataSource(this, uri);
        mMediaPlayer.prepareAsync();

        mVideoView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-mediaplayerview"));
    }

    @Override
    protected void onStop() {
        mMediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoView != null) {
            // the uri is stored in the base activity
            outState.putBoolean("playing", mMediaPlayer.isPlaying());
            outState.putInt("position", mMediaPlayer.getCurrentPosition());
        }
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
