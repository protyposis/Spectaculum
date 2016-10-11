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
import android.opengl.GLES30;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by maguggen on 18.06.2014.
 */
public class Texture2D extends Texture {

    private static final String TAG = Texture2D.class.getSimpleName();

    private int mWidth;
    private int mHeight;

    public Texture2D(int internalformat, int format, int width, int height, int type, Buffer pixels) {
        super();

        mWidth = width;
        mHeight = height;

        setupTexture();

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalformat, mWidth, mHeight, 0, format, type, pixels);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // unbind texture
    }

    public Texture2D(int width, int height) {
        this(GLES20.GL_RGBA, GLES20.GL_RGBA, width, height, GLES20.GL_UNSIGNED_BYTE, null);
    }

    public Texture2D(Bitmap bitmap) {
        super();

        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        setupTexture();

        // Load texture
        // This method automatically puts the texture into the next larger power of 2 size
        android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // unbind texture
    }

    private void setupTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTexture = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLUtils.checkError("glBindTexture");

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Tegra needs GL_CLAMP_TO_EDGE for non-power-of-2 textures, else the picture is black: http://stackoverflow.com/a/9042198
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    /**
     * Sets the filter mode of the texture. Specify -1 to keep the current setting.
     */
    public void setFilterMode(int minFilter, int maxFilter) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);

        if(minFilter > -1) {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        }

        if(maxFilter > -1) {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, maxFilter);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public void delete() {
        GLES20.glDeleteTextures(1, new int[] { mTexture }, 0);
    }

    public static Texture2D generateFloatTexture(int width, int height) {
        if(GLUtils.HAS_GLES30 && GLUtils.HAS_GL_OES_texture_half_float && GLUtils.HAS_FLOAT_FRAMEBUFFER_SUPPORT) {
            return new Texture2D(GLES30.GL_RGBA16F, GLES20.GL_RGBA, width, height, GLES20.GL_FLOAT, null);
        } else {
            Log.i(TAG, "Texture fallback mode to GLES20 8 bit");
            return new Texture2D(GLES20.GL_RGBA, GLES20.GL_RGBA, width, height, GLES20.GL_UNSIGNED_BYTE, null);
        }
    }
}
