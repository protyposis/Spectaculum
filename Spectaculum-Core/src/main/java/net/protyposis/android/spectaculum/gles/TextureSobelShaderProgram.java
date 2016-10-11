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
 * Created by Mario on 07.09.2014.
 */
public class TextureSobelShaderProgram extends TextureShaderProgram {

    private int mThresholdLHandle;
    private int mThresholdHHandle;
    private int mColorHandle;

    public TextureSobelShaderProgram() {
        super("fs_texture_sobel.glsl");

        mThresholdLHandle = GLES20.glGetUniformLocation(mProgramHandle, "thresholdL");
        GLUtils.checkError("glGetUniformLocation thresholdL");

        mThresholdHHandle = GLES20.glGetUniformLocation(mProgramHandle, "thresholdH");
        GLUtils.checkError("glGetUniformLocation thresholdH");

        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "color");
        GLUtils.checkError("glGetUniformLocation color");
    }

    public void setThreshold(float low, float high) {
        use();
        GLES20.glUniform1f(mThresholdLHandle, low);
        GLES20.glUniform1f(mThresholdHHandle, high);
    }

    public void setColor(float r, float g, float b) {
        use();
        GLES20.glUniform3f(mColorHandle, r, g, b);
    }
}
