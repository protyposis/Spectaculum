/*
 * Copyright 2016 Mario Guggenberger <mg@protyposis.net>
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

package net.protyposis.android.spectaculum;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import net.protyposis.android.spectaculum.gles.ExternalSurfaceTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario on 17.08.2016.
 */
public class InputSurfaceHolder {

    public interface Callback {
        /**
         * This is called immediately after the surface is first created.
         * Implementations of this should start up whatever rendering code
         * they desire.  Note that only one thread can ever draw into
         * a {@link Surface}, so you should not draw into the Surface here
         * if your normal rendering will be in another thread.
         *
         * @param holder The SurfaceHolder whose surface is being created.
         */
        void surfaceCreated(InputSurfaceHolder holder);

        /**
         * This is called immediately before a surface is being destroyed. After
         * returning from this call, you should no longer try to access this
         * surface.  If you have a rendering thread that directly accesses
         * the surface, you must ensure that thread is no longer touching the
         * Surface before returning from this function.
         *
         * @param holder The SurfaceHolder whose surface is being destroyed.
         */
        void surfaceDestroyed(InputSurfaceHolder holder);
    }

    private List<Callback> mCallbacks;
    private ExternalSurfaceTexture mExternalSurfaceTexture;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    public InputSurfaceHolder() {
        mCallbacks = new ArrayList<>();
    }

    /**
     * Add a Callback interface to this holder. The holder can handle multiple callbacks.
     * @param callback the callback listener to add
     */
    public void addCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    /**
     * Removes a previously added Callback interface from this holder.
     * @param callback the callback listener to remove
     */
    public void removeCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * Updates the input surface holder with a new external surface texture or removes all references
     * of an old external surface texture and notifies all Callback listeners.
     *
     * This is an internal method that should not be used outside the Spectaculum core.
     *
     * @param externalSurfaceTexture a new external surface texture to assign or null to remove
     */
    void update(ExternalSurfaceTexture externalSurfaceTexture) {
        if(externalSurfaceTexture == null) {
            // Set everything to null
            mExternalSurfaceTexture = null;
            mSurfaceTexture = null;
            mSurface = null;

            // Notify listeners about removed surface
            for (Callback c: mCallbacks) {
                c.surfaceDestroyed(this);
            }

            return;
        }

        // This is our external OpenGL texture object that holds the actual surface texture among other management stuff
        mExternalSurfaceTexture = externalSurfaceTexture;

        // Get the surface texture
        mSurfaceTexture = externalSurfaceTexture.getSurfaceTexture();

        // Create a surface for this texture
        mSurface = new Surface(mSurfaceTexture);

        // Notify listeners about new surface
        for (Callback c: mCallbacks) {
            c.surfaceCreated(this);
        }
    }

    /**
     * Returns the external surface texture from the GL renderer. Internal use only.
     */
    ExternalSurfaceTexture getExternalSurfaceTexture() {
        return mExternalSurfaceTexture;
    }

    /**
     * Returns the surface texture to which input image data can be written or null when it is not available yet.
     */
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * Returns the surface to which input image data can be written or null when it is not available yet.
     */
    public Surface getSurface() {
        return mSurface;
    }
}
