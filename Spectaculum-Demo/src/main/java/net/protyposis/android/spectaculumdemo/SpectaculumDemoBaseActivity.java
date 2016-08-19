package net.protyposis.android.spectaculumdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;

import net.protyposis.android.spectaculum.SpectaculumView;

/**
 * A base activity implementation for a Spectaculum view that provides all functionality that is
 * shared among all Spectaculum demo activities.
 *
 * Created by Mario on 17.08.2016.
 */
public abstract class SpectaculumDemoBaseActivity extends Activity {

    private Uri mMediaUri;
    private SpectaculumView mSpectaculumView;
    private EffectManager mEffectManager;
    private ProgressBar mProgressIndicator;
    private MediaController mMediaController;

    /**
     * Sets up the base activity. This must be called from a subclass AFTER setting the layout.
     */
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setActionBarSubtitleEllipsizeMiddle(this);

        // Get views from the layout
        mSpectaculumView = (SpectaculumView) findViewById(R.id.spectaculum);
        mProgressIndicator = (ProgressBar) findViewById(R.id.progress);

        // Initialize Spectaculum effects
        mEffectManager = new EffectManager(this, R.id.parameterlist, mSpectaculumView);
        mEffectManager.addEffects();

        // Display progress indicator because subclasses will now initialize their stuff
        showProgressIndicator();
    }

    public void showProgressIndicator() {
        // Not all layouts contain a progress indicator (e.g. cameraview) so we need to check for null here
        if(mProgressIndicator != null) {
            mProgressIndicator.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressIndicator() {
        // Not all layouts contain a progress indicator (e.g. cameraview) so we need to check for null here
        if(mProgressIndicator != null) {
            mProgressIndicator.setVisibility(View.GONE);
        }
    }

    public void setMediaUri(Uri mediaUri) {
        mMediaUri = mediaUri;
        getActionBar().setSubtitle(mMediaUri.toString());
    }

    /**
     * Creates a media controller and attaches it to the activity.
     * This method is for activities that contain a video player.
     * @param mediaPlayerControl the control interface, e.g. a video view
     */
    public void initMediaController(MediaController.MediaPlayerControl mediaPlayerControl) {
        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(findViewById(R.id.container));
        mMediaController.setMediaPlayer(mediaPlayerControl);
        mMediaController.setEnabled(false);
    }

    /**
     * Returns the media controller for programmatic control of video playback.
     */
    public MediaController getMediaControllerWidget() {
        return mMediaController;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add the common menu items to the action bar
        getMenuInflater().inflate(R.menu.common, menu);

        // Add the effects submenu to the action bar
        mEffectManager.addToMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle action bar clicks
        if(mEffectManager.doMenuActions(item)) {
            // An effect has been selected
            return true;
        } else if(id == R.id.action_save_frame) {
            // Request a frame capture; make sure that a OnFrameCapturedCallback is attached
            mSpectaculumView.captureFrame();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Try to toggle the media controller
        if (event.getAction() == MotionEvent.ACTION_UP && mMediaController != null) {
            long durationMs = event.getEventTime() - event.getDownTime();
            /*
             *If a certain short amount of time passes between DOWN and UP actions, we classify the
             * event as simple tap and toggle the visibility of the media controller.
             */
            if(durationMs < 500) {
                if (mMediaController.isShowing()) {
                    mMediaController.hide();
                } else {
                    mMediaController.show();
                }
            }
        }

        // Hand the event to the Spectaculum view to process zoom/pan gestures
        if(!mSpectaculumView.isTouchEnabled()) {
            mSpectaculumView.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mEffectManager.onPause();

        // Spectaculum must be paused when activity pauses to suspend the rendering thread
        mSpectaculumView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEffectManager.onResume();

        // Spectaculum must be resumed when activity resumes to resume the rendering thread
        mSpectaculumView.onResume();
    }

    @Override
    protected void onStop() {
        // Hide media controller to avoid window leak
        if(mMediaController != null) {
            mMediaController.hide();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Retain the media uri through a configuration change
        if(mMediaUri != null) {
            outState.putParcelable("uri", mMediaUri);
        }
    }
}
