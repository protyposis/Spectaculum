/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
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

package net.protyposis.android.spectaculum.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.protyposis.android.spectaculum.effects.Effect;
import net.protyposis.android.spectaculum.effects.EffectException;

/**
 * Created by Mario on 14.06.2014.
 */
public class GLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = GLRenderer.class.getSimpleName();

    public enum RenderRequest {
        DEFAULT,
        ALL,
        EFFECT,
        GEOMETRY
    }

    /**
     * Callback interface for being notified when the ExternalSurfaceTexture is created, which
     * can be used to feed picture frames from the MediaCodec or camera preview. Note that
     * the callback comes from the GLThread so you need to take care in the handler of
     * dispatching the action to the right thread, e.g. the UI thread.
     */
    public interface OnExternalSurfaceTextureCreatedListener {
        void onExternalSurfaceTextureCreated(ExternalSurfaceTexture surfaceTexture);
    }

    /**
     * Callback interface for effect-related events.
     */
    public interface EffectEventListener {

        /**
         * Gets called when an effect has been initialized which happens when an effect is
         * selected for the first time.
         * @param index the index of the initialized effect
         * @param effect the initialized effect
         */
        void onEffectInitialized(int index, Effect effect);

        /**
         * Gets called when an effect has been successfully selected.
         * @param index the index of the selected effect
         * @param effect the selected effect
         */
        void onEffectSelected(int index, Effect effect);

        /**
         * Gets called when an error related to an effect happens, e.g. during initialization.
         * @param index the index of the failed effect
         * @param effect the failed effect
         * @param e the exception carrying the cause of failure
         */
        void onEffectError(int index, Effect effect, EffectException e);
    }

    public interface OnFrameCapturedCallback {
        void onFrameCaptured(Bitmap bitmap);
    }

    /**
     * The view matrix / camera position
     */
    private float[] mViewMatrix = new float[16];

    /**
     * The projection matrix / camera frame
     */
    private float[] mProjectionMatrix = new float[16];

    private int mWidth;
    private int mHeight;

    private ExternalSurfaceTexture mExternalSurfaceTexture;
    private ReadExternalTextureShaderProgram mReadExternalTextureShaderProgram;
    private Framebuffer mFramebufferIn;
    private Framebuffer mFramebufferOut;
    private TexturedRectangle mTexturedRectangle;
    private TextureShaderProgram mTextureToScreenShaderProgram;

    private TextureShaderProgram mDefaultShaderProgram;
    private List<Effect> mEffects;
    private Effect mEffect;
    private RenderRequest mRenderRequest;

    private OnExternalSurfaceTextureCreatedListener mOnExternalSurfaceTextureCreatedListener;
    private EffectEventListener mEffectEventListener;
    private FrameRateCalculator mFrameRateCalculator;
    private boolean mInitializeStuff;

    public GLRenderer() {
        Log.d(TAG, "ctor");

        mTexturedRectangle = new TexturedRectangle();

        mEffects = new ArrayList<>();
    }

    public void setOnExternalSurfaceTextureCreatedListener(OnExternalSurfaceTextureCreatedListener l) {
        this.mOnExternalSurfaceTextureCreatedListener = l;
    }

    public void setEffectEventListener(EffectEventListener l) {
        this.mEffectEventListener = l;
    }

    public void setRenderRequest(RenderRequest renderRequest) {
        mRenderRequest = renderRequest;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        GLUtils.init();
        //GLUtils.printSysConfig();

        // set the background color
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);

        // set up the "camera"
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, 1.0f,   // eye x,y,z
                0.0f, 0.0f, 0.0f,  // look x,y,z
                0.0f, 1.0f, 0.0f);  // up x,y,z

        if(mExternalSurfaceTexture != null) {
            // Delete input texture from previous context to free RAM
            mExternalSurfaceTexture.delete();
        }

        mExternalSurfaceTexture = new ExternalSurfaceTexture();
        mReadExternalTextureShaderProgram = new ReadExternalTextureShaderProgram();

        mDefaultShaderProgram = new TextureShaderProgram();

        mTextureToScreenShaderProgram = new TextureShaderProgram();

        if(mOnExternalSurfaceTextureCreatedListener != null) {
            mOnExternalSurfaceTextureCreatedListener.onExternalSurfaceTextureCreated(mExternalSurfaceTexture);
        }

        mFrameRateCalculator = new FrameRateCalculator(30);

        mInitializeStuff = true;
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        Log.d(TAG, "onSurfaceChanged " + width + "x" + height);

        // Initialize stuff in the following block only if the surface was just created or the resolution has changed
        if(mInitializeStuff || mWidth != width || mHeight != height) {
            if(mFramebufferIn != null) {
                mFramebufferIn.delete();
                mFramebufferOut.delete();
            }

            mFramebufferIn = new Framebuffer(width, height);
            mFramebufferOut = new Framebuffer(width, height);
            mFramebufferOut.getTexture().setFilterMode(-1, GLES20.GL_LINEAR);

            for (Effect effect : mEffects) {
            /* After a surface change, if the resolution has changed, effects need to be
             * reinitialized to update them to the new resolution. */
                if (effect.isInitialized()) {
                    Log.d(TAG, "reinitializing effect " + effect.getName());
                    effect.init(width, height);
                }
            }

            mInitializeStuff = false;
        }

        // adjust the viewport to the surface size
        GLES20.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;

        setZoomLevel(1.0f);

        // fully re-render current scene to adjust to the change
        mRenderRequest = RenderRequest.ALL;
        onDrawFrame(glUnused);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        // PREPARE

        mTexturedRectangle.reset();


        // FETCH AND TRANSFER FRAME TO TEXTURE
        if(mRenderRequest == RenderRequest.ALL || mExternalSurfaceTexture.isTextureUpdateAvailable()) {
            mExternalSurfaceTexture.updateTexture();

            mFramebufferIn.bind();
            mReadExternalTextureShaderProgram.use();
            mReadExternalTextureShaderProgram.setTexture(mExternalSurfaceTexture);
            mTexturedRectangle.draw(mReadExternalTextureShaderProgram);

            mRenderRequest = RenderRequest.EFFECT;
        }


        // MANIPULATE TEXTURE WITH SHADER(S)

        if(mRenderRequest == RenderRequest.EFFECT) {
            if (mEffect != null) {
                mEffect.apply(mFramebufferIn.getTexture(), mFramebufferOut);
            } else {
                mFramebufferOut.bind();
                mDefaultShaderProgram.use();
                mDefaultShaderProgram.setTexture(mFramebufferIn.getTexture());
                mTexturedRectangle.draw(mDefaultShaderProgram);
            }

            mRenderRequest = RenderRequest.GEOMETRY;
        }


        // RENDER TEXTURE TO SCREEN

        if(mRenderRequest == RenderRequest.GEOMETRY) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); // framebuffer 0 is the screen
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            mTextureToScreenShaderProgram.use();
            mTextureToScreenShaderProgram.setTexture(mFramebufferOut.getTexture());

            mTexturedRectangle.translate(0.0f, 0.0f, -1.0f);
            mTexturedRectangle.calculateMVP(mViewMatrix, mProjectionMatrix);

            mTexturedRectangle.draw(mTextureToScreenShaderProgram);
        }

        // STUFF

        //mFrameRateCalculator.frame();
        mRenderRequest = RenderRequest.DEFAULT;
    }

    public void setZoomLevel(float zoomLevel) {
        Matrix.orthoM(mProjectionMatrix, 0,
                -1.0f / zoomLevel, 1.0f / zoomLevel,
                -1.0f / zoomLevel, 1.0f / zoomLevel,
                1.0f, 10.0f);
    }

    public void setPan(float pX, float pY) {
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, pX, pY, 0.0f);
    }

    public void addEffect(Effect... effects) {
        for(Effect effect : effects) {
            Log.d(TAG, "adding effect " + effect.getName());
            mEffects.add(effect);
        }
    }

    public void selectEffect(int index) {
        if(index >= mEffects.size()) {
            Log.w(TAG, String.format("invalid effect index %d (%d effects registered)",
                    index, mEffects.size()));
            return;
        }
        Effect effect = mEffects.get(index); // keep in a local variable until initialized, in case initialization fails
        if(!effect.isInitialized()) {
            Log.d(TAG, "initializing effect " + effect.getName());
            try {
                effect.init(mWidth, mHeight);
                if (mEffectEventListener != null) {
                    mEffectEventListener.onEffectInitialized(index, effect);
                }
            } catch (OutOfMemoryError | Exception e) {
                if (mEffectEventListener != null) {
                    mEffectEventListener.onEffectError(index, effect, new EffectException(e));
                }
                return;
            }
        }
        mEffect = effect;
        if(mEffectEventListener != null) {
            mEffectEventListener.onEffectSelected(index, effect);
        }
    }

    public void saveCurrentFrame(OnFrameCapturedCallback callback) {
        callback.onFrameCaptured(GLUtils.getFrameBuffer(mWidth, mHeight));
    }
}
