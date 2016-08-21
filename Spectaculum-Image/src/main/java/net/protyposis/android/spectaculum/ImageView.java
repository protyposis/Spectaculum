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

package net.protyposis.android.spectaculum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;

/**
 * Created by maguggen on 02.10.2014.
 */
public class ImageView extends SpectaculumView {

    private Bitmap mBitmap;

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
    public void onInputSurfaceCreated(InputSurfaceHolder inputSurfaceHolder) {
        tryLoadBitmap();
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        updateResolution(mBitmap.getWidth(), mBitmap.getHeight());
        tryLoadBitmap();
    }

    private void tryLoadBitmap() {
        if(mBitmap != null && getInputHolder().getSurface() != null) {
            SurfaceTexture st = getInputHolder().getSurfaceTexture();
            Surface s = getInputHolder().getSurface();

            // First set the texture resolution
            st.setDefaultBufferSize(mBitmap.getWidth(), mBitmap.getHeight());

            // Then draw the image data
            Canvas c = s.lockCanvas(null);
            c.drawBitmap(mBitmap, 0, 0, null);
            s.unlockCanvasAndPost(c);
        }
    }
}
