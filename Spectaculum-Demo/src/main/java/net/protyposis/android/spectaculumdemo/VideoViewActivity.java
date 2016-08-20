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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_videoview);
        super.onCreate(savedInstanceState);

        mVideoView = (VideoView) findViewById(R.id.spectaculum);
        initMediaController(mVideoView);

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
        setMediaUri(uri);

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
        mVideoView.setVideoURI(uri);
        mVideoView.seekTo(position > 0 ? position : 0);
        if (playback) {
            mVideoView.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mVideoView != null) {
            // the uri is stored in the base activity
            outState.putBoolean("playing", mVideoView.isPlaying());
            outState.putInt("position", mVideoView.getCurrentPosition());
        }
    }
}
