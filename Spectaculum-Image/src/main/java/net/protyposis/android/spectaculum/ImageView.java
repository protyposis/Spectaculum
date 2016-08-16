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
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;

import net.protyposis.android.spectaculum.gles.ExternalSurfaceTexture;

/**
 * Created by maguggen on 02.10.2014.
 */
public class ImageView extends SpectaculumView {

    private Bitmap mBitmap;
    private ExternalSurfaceTexture mTexture;

    public ImageView(Context context) {
        super(context);
        init();
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // nothing to do
    }

    @Override
    public void onExternalSurfaceTextureCreated(ExternalSurfaceTexture surfaceTexture) {
        super.onExternalSurfaceTextureCreated(surfaceTexture);

        mTexture = surfaceTexture;
        tryLoadBitmap();
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mVideoWidth = mBitmap.getWidth();
        mVideoHeight = mBitmap.getHeight();
        updateSourceResolution(mVideoWidth, mVideoHeight);
        requestLayout();
        tryLoadBitmap();
    }

    private void tryLoadBitmap() {
        if(mBitmap != null && mTexture != null) {
            SurfaceTexture st = mTexture.getSurfaceTexture();
            Surface s = mTexture.getSurface();

            st.setDefaultBufferSize(mBitmap.getWidth(), mBitmap.getHeight());

            Canvas c = s.lockCanvas(null);
            c.drawBitmap(mBitmap, 0, 0, null);
            s.unlockCanvasAndPost(c);
        }
    }
}
