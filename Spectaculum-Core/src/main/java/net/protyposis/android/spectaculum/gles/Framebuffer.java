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

import android.opengl.GLES20;

/**
 * Created by maguggen on 04.07.2014.
 */
public class Framebuffer {

    private int mFramebuffer;
    private Texture2D mTargetTexture;

    public Framebuffer(int width, int height) {
        int[] framebuffer = new int[1];
        GLES20.glGenFramebuffers(1, framebuffer, 0);
        mFramebuffer = framebuffer[0];

        /* Every framebuffer has its own texture attached, because switching between framebuffers
         * is faster and recommended against switching texture attachements of a single framebuffer.
         * http://stackoverflow.com/a/6435997
         * http://stackoverflow.com/a/6767452 (comments!)
         */
        mTargetTexture = Texture2D.generateFloatTexture(width, height);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTargetTexture.getHandle(), 0);

        checkFramebufferStatus();
    }

    public void bind(boolean clear) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        if(clear) {
            // for performance on Android, clear after every bind: http://stackoverflow.com/a/11052366
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        }
    }

    public void bind() {
        bind(true);
    }

    public Texture2D getTexture() {
        return mTargetTexture;
    }

    public void delete() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        // Detach texture from framebuffer
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, 0, 0);
        // Delete texture
        mTargetTexture.delete();
        // Delete framebuffer
        GLES20.glDeleteFramebuffers(1, new int[] { mFramebuffer }, 0);
    }

    private void checkFramebufferStatus() {
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("glCheckFramebufferStatus error " + String.format("0x%X", status));
        }
    }
}
