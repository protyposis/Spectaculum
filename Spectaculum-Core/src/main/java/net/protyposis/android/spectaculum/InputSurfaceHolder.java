package net.protyposis.android.spectaculum;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import net.protyposis.android.spectaculum.gles.ExternalSurfaceTexture;

/**
 * Created by Mario on 17.08.2016.
 */
public class InputSurfaceHolder {

    private ExternalSurfaceTexture mExternalSurfaceTexture;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    public InputSurfaceHolder(ExternalSurfaceTexture externalSurfaceTexture) {
        // This is our external OpenGL texture object that holds the actual surface texture among other management stuff
        mExternalSurfaceTexture = externalSurfaceTexture;

        // Get the surface texture
        mSurfaceTexture = externalSurfaceTexture.getSurfaceTexture();

        // Create a surface for this texture
        mSurface = new Surface(mSurfaceTexture);
    }

    /**
     * Returns the extuernal surface texture from the GL renderer. Internal use only.
     */
    ExternalSurfaceTexture getExternalSurfaceTexture() {
        return mExternalSurfaceTexture;
    }

    /**
     * Gets the surface texture to which input image data can be written.
     */
    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * Gets the surface to which input image data can be written.
     */
    public Surface getSurface() {
        return mSurface;
    }
}
