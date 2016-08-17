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

package net.protyposis.android.spectaculum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.*;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;

import net.protyposis.android.spectaculum.effects.Effect;
import net.protyposis.android.spectaculum.gles.*;

/**
 * Created by Mario on 14.06.2014.
 */
public class SpectaculumView extends GLSurfaceView implements
        SurfaceTexture.OnFrameAvailableListener,
        Effect.Listener, GLRenderer.OnEffectInitializedListener,
        GLRenderer.OnFrameCapturedCallback {

    private static final String TAG = SpectaculumView.class.getSimpleName();

    public interface OnEffectInitializedListener extends GLRenderer.OnEffectInitializedListener {}
    public interface OnFrameCapturedCallback extends GLRenderer.OnFrameCapturedCallback {}

    private GLRenderer mRenderer;
    private InputSurfaceHolder mInputSurfaceHolder;
    private Handler mRunOnUiThreadHandler = new Handler();
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    private OnEffectInitializedListener mOnEffectInitializedListener;
    private OnFrameCapturedCallback mOnFrameCapturedCallback;

    private PipelineResolution mPipelineResolution = PipelineResolution.VIEW;

    private float mZoomLevel = 1.0f;
    private float mZoomSnappingRange = 0.02f;
    private float mPanX;
    private float mPanY;
    private float mPanSnappingRange = 0.02f;
    private boolean mTouchEnabled = true;

    protected int mVideoWidth;
    protected int mVideoHeight;

    protected SpectaculumView(Context context) {
        super(context);
        init(context);
    }

    protected SpectaculumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        if(isInEditMode()) {
            // do not start renderer in layout editor
            return;
        }
        if(!net.protyposis.android.spectaculum.gles.GLUtils.isGlEs2Supported(context)) {
            Log.e(TAG, "GLES 2.0 is not supported");
            return;
        }

        LibraryHelper.setContext(context);

        mRenderer = new GLRenderer();
        mRenderer.setOnExternalSurfaceTextureCreatedListener(mExternalSurfaceTextureCreatedListener);
        mRenderer.setOnEffectInitializedListener(this);

        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        mZoomLevel *= detector.getScaleFactor();

                        if(LibraryHelper.isBetween(mZoomLevel, 1-mZoomSnappingRange, 1+mZoomSnappingRange)) {
                            mZoomLevel = 1.0f;
                        }

                        // limit zooming to magnification zooms (zoom-ins)
                        if(mZoomLevel < 1.0f) {
                            mZoomLevel = 1.0f;
                        }

                        setZoom(mZoomLevel);
                        return true;
                    }
                });

        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        // divide by zoom level to adjust panning speed to zoomed picture size
                        // multiply by fixed scaling factor to compensate for panning lag
                        mPanX += distanceX / getWidth() / mZoomLevel * 1.2f;
                        mPanY += distanceY / getHeight() / mZoomLevel * 1.2f;

                        float panSnappingRange = mPanSnappingRange / mZoomLevel;
                        if(LibraryHelper.isBetween(mPanX, -panSnappingRange, +panSnappingRange)) {
                            mPanX = 0;
                        }
                        if(LibraryHelper.isBetween(mPanY, -panSnappingRange, +panSnappingRange)) {
                            mPanY = 0;
                        }

                        // limit panning to the texture bounds so it always covers the complete view
                        float maxPanX = Math.abs((1.0f / mZoomLevel) - 1.0f);
                        float maxPanY = Math.abs((1.0f / mZoomLevel) - 1.0f);
                        mPanX = LibraryHelper.clamp(mPanX, -maxPanX, maxPanX);
                        mPanY = LibraryHelper.clamp(mPanY, -maxPanY, maxPanY);

                        setPan(mPanX, mPanY);
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        mZoomLevel = 1;
                        mPanX = 0;
                        mPanY = 0;

                        setZoom(mZoomLevel);
                        setPan(mPanX, mPanY);

                        return true;
                    }
                });
    }

    /**
     * Sets the zoom factor of the texture in the view. 1.0 means no zoom, 2.0 2x zoom, etc.
     */
    public void setZoom(float zoomFactor) {
        mZoomLevel = zoomFactor;
        mRenderer.setZoomLevel(mZoomLevel);
        requestRender(GLRenderer.RenderRequest.GEOMETRY);
    }

    public float getZoomLevel() {
        return mZoomLevel;
    }

    /**
     * Sets the panning of the texture in the view. (0.0, 0.0) centers the texture and means no
     * panning, (-1.0, -1.0) moves the texture to the lower right quarter.
     * @param x
     * @param y
     */
    public void setPan(float x, float y) {
        mPanX = x;
        mPanY = y;
        mRenderer.setPan(-mPanX, mPanY);
        requestRender(GLRenderer.RenderRequest.GEOMETRY);
    }

    public float getPanX() {
        return mPanX;
    }

    public float getPanY() {
        return mPanY;
    }

    /**
     * Enables or disables touch zoom/pan gestures. When disabled, a parent container (e.g. an activity)
     * can still pass touch events to this view's {@link #onTouchEvent(MotionEvent)} to process
     * zoom/pan gestures.
     * @see #isTouchEnabled()
     */
    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
    }

    /**
     * Checks if touch gestures are enabled. Touch gestures are enabled by default.
     * @see #setTouchEnabled(boolean)
     */
    public boolean isTouchEnabled() {
        return mTouchEnabled;
    }

    /**
     * Resizes the video view according to the video size to keep aspect ratio.
     * Code copied from {@link android.widget.VideoView#onMeasure(int, int)}.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
                + MeasureSpec.toString(heightMeasureSpec) + ")");

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * NOTE: These calls should not be simplified to a logical chain, because the evaluation
         * would stop at the first true value and not execute the following functions.
         */
        boolean event1 = mScaleGestureDetector.onTouchEvent(event);
        boolean event2 = mGestureDetector.onTouchEvent(event);
        return event1 || event2;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!mTouchEnabled) {
            // Touch events are disabled and we return false to route all events to the parent
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Implement this method to receive the input surface holder when it is ready to be used.
     * The input surface holder holds the surface and surface texture to which input data, i.e. image
     * data from some source that should be processed and displayed, should be written to display
     * it in the view.
     * @param inputSurfaceHolder the input surface holder which holds the surface where image data should be written to
     */
    public void onInputSurfaceCreated(InputSurfaceHolder inputSurfaceHolder) {
        // nothing to do here
    }

    /**
     * Gets the input surface holder that holds the surface where image data should be written to
     * for processing and display. The holder is only available once {@link #onInputSurfaceCreated(InputSurfaceHolder)}
     * has been called.
     * The input surface holder holds the input surface (texture) that is used to write image data
     * into the processing pipeline, opposed to the surface holder from {@link #getHolder()} that holds
     * the surface to which the final result of the processing pipeline will be written to for display.
     * @return the input surface holder or null if it is not available yet
     */
    public InputSurfaceHolder getInputHolder() {
        return mInputSurfaceHolder;
    }

    /**
     * Adds one or more effects to the view. Added effects can then be activated/selected by calling
     * {@link #selectEffect(int)}. The effect indices start at zero and are in the order that they
     * are added to the view.
     * @param effects effects to add
     */
    public void addEffect(final Effect... effects) {
        for(Effect effect : effects) {
            effect.setListener(this);
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.addEffect(effects);
            }
        });
    }

    /**
     * Selects/activates the effect with the given index as it has been added through {@link #addEffect(Effect...)}.
     * @param index the index of the effect to activate
     */
    public void selectEffect(final int index) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.selectEffect(index);
                requestRender(GLRenderer.RenderRequest.EFFECT);
            }
        });
    }

    /**
     * Gets called when an effect has been initialized after being selected for the first time. Can
     * be overwritten in subclasses but must be called through. External callers should use
     * {@link #setOnEffectInitializedListener(OnEffectInitializedListener)}.
     * @param effect the initialized effect
     */
    @Override
    public void onEffectInitialized(Effect effect) {
        if(mOnEffectInitializedListener != null) {
            mOnEffectInitializedListener.onEffectInitialized(effect);
        }
        requestRender(GLRenderer.RenderRequest.EFFECT);
    }

    /**
     * Sets an event listener that gets called when a selected effect has been initialized, that
     * is when it is selected ({@link #selectEffect(int)}) for the first time.
     * This can take some time when a lot of data (framebuffers, textures, ...) is loaded.
     */
    public void setOnEffectInitializedListener(OnEffectInitializedListener listener) {
        mOnEffectInitializedListener = listener;
    }

    /**
     * Gets called when a parameter of an effect has changed. This method then triggers a fresh
     * rendering of the effect. Can be overridden in subclasses but must be called through.
     * @param effect the effect of which a parameter value has changed
     */
    @Override
    public void onEffectChanged(Effect effect) {
        requestRender(GLRenderer.RenderRequest.EFFECT);
    }

    /**
     * Gets called when a new image frame has been written to the surface texture and requests a
     * fresh rendering of the view. The texture can be obtained through {@link #onInputSurfaceCreated(InputSurfaceHolder)}
     * or {@link #getInputHolder()}.
     * Can be overridden in subclasses but must be called through.
     * @param surfaceTexture the updated surface texture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender(GLRenderer.RenderRequest.ALL);
    }

    /**
     * Requests a render pass of the specified render pipeline section.
     * @param renderRequest specifies the pipeline section to be rendered
     */
    protected void requestRender(final GLRenderer.RenderRequest renderRequest) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setRenderRequest(renderRequest);
                requestRender();
            }
        });
    }

    /**
     * Requests a capture of the current frame on the view. The frame is asynchronously requested
     * from the renderer and will be passed back on the UI thread to {@link #onFrameCaptured(Bitmap)}
     * and the event listener that can be set with {@link #setOnFrameCapturedCallback(OnFrameCapturedCallback)}.
     */
    public void captureFrame() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.saveCurrentFrame(new GLRenderer.OnFrameCapturedCallback() {
                    @Override
                    public void onFrameCaptured(final Bitmap bitmap) {
                        mRunOnUiThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                SpectaculumView.this.onFrameCaptured(bitmap);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Receives a captured frame from the renderer. Can be overwritten in subclasses but must be
     * called through. External callers should use {@link #setOnFrameCapturedCallback(OnFrameCapturedCallback)}.
     */
    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        if(mOnFrameCapturedCallback != null) {
            mOnFrameCapturedCallback.onFrameCaptured(bitmap);
        }
    }

    /**
     * Sets a callback event handler that receives a bitmap of the captured frame.
     */
    public void setOnFrameCapturedCallback(OnFrameCapturedCallback callback) {
        mOnFrameCapturedCallback = callback;
    }

    /**
     * Sets the resolution mode of the processing pipeline.
     * @see PipelineResolution
     */
    public void setPipelineResolution(PipelineResolution resolution) {
        mPipelineResolution = resolution;
    }

    /**
     * Gets the configured resolution mode of the processing pipeline.
     */
    public PipelineResolution getPipelineResolution() {
        return mPipelineResolution;
    }

    protected void updateSourceResolution(int width, int height) {
        if (width != 0 && height != 0 && mPipelineResolution == PipelineResolution.SOURCE) {
            getHolder().setFixedSize(width, height);
        }
    }

    private GLRenderer.OnExternalSurfaceTextureCreatedListener mExternalSurfaceTextureCreatedListener =
            new GLRenderer.OnExternalSurfaceTextureCreatedListener() {
        @Override
        public void onExternalSurfaceTextureCreated(final ExternalSurfaceTexture surfaceTexture) {
            // dispatch event to UI thread
            mRunOnUiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Create an input surface holder and call the event handler
                    mInputSurfaceHolder = new InputSurfaceHolder(surfaceTexture);
                    onInputSurfaceCreated(mInputSurfaceHolder);
                }
            });

            surfaceTexture.setOnFrameAvailableListener(SpectaculumView.this);
        }
    };
}
