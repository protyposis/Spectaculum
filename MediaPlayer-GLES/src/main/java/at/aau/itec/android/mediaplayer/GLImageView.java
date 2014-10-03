/*
 * Copyright (c) 2014 Mario Guggenberger <mario.guggenberger@aau.at>
 *
 * This file is part of ITEC MediaPlayer.
 *
 * ITEC MediaPlayer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ITEC MediaPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ITEC MediaPlayer.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.aau.itec.android.mediaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;

import at.aau.itec.android.mediaplayer.gles.ExternalSurfaceTexture;

/**
 * Created by maguggen on 02.10.2014.
 */
public class GLImageView extends GLTextureView {

    private Bitmap mBitmap;
    private ExternalSurfaceTexture mTexture;

    public GLImageView(Context context) {
        super(context);
        init();
    }

    public GLImageView(Context context, AttributeSet attrs) {
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
        getHolder().setFixedSize(mBitmap.getWidth(), mBitmap.getHeight());
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
