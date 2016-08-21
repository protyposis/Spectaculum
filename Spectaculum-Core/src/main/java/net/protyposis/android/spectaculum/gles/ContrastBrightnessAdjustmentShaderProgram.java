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
 * Created by Mario on 02.10.2014.
 */
public class ContrastBrightnessAdjustmentShaderProgram extends TextureShaderProgram {

    private int mContrastHandle;
    private int mBrightnessHandle;

    public ContrastBrightnessAdjustmentShaderProgram() {
        super("fs_adjust_contrast_brightness.glsl");

        mContrastHandle = GLES20.glGetUniformLocation(mProgramHandle, "contrast");
        GLUtils.checkError("glGetUniformLocation contrast");

        mBrightnessHandle = GLES20.glGetUniformLocation(mProgramHandle, "brightness");
        GLUtils.checkError("glGetUniformLocation brightness");

        setContrast(1.0f);
        setBrightness(1.0f);
    }

    public void setContrast(float contrast) {
        use();
        GLES20.glUniform1f(mContrastHandle, contrast);
    }

    public void setBrightness(float brightness) {
        use();
        GLES20.glUniform1f(mBrightnessHandle, brightness);
    }
}
