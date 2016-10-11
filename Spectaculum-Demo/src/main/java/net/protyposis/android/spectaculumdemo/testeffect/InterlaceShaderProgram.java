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

package net.protyposis.android.spectaculumdemo.testeffect;

import android.opengl.GLES20;

import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by Mario on 11.10.2016.
 */

public class InterlaceShaderProgram extends TextureShaderProgram {

    protected int mOpacityHandle;
    protected int mDistanceHandle;

    public InterlaceShaderProgram() {
        super("fs_interlace.glsl");

        mOpacityHandle = GLES20.glGetUniformLocation(mProgramHandle, "opacity");
        GLUtils.checkError("glGetUniformLocation opacity");
        mDistanceHandle = GLES20.glGetUniformLocation(mProgramHandle, "distance");
        GLUtils.checkError("glGetUniformLocation distance");
    }

    public void setOpacity(float opacity) {
        use();
        GLES20.glUniform1f(mOpacityHandle, opacity);
    }

    public void setDistance(int distance) {
        use();
        GLES20.glUniform1i(mDistanceHandle, distance);
    }
}
