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
 * Created by Mario on 14.06.2014.
 */
public class TextureFlipShaderProgram extends TextureShaderProgram {

    protected int mModeHandle;

    public TextureFlipShaderProgram() {
        super("fs_texture_flip.glsl");

        mModeHandle = GLES20.glGetUniformLocation(mProgramHandle, "mode");
        GLUtils.checkError("glGetUniformLocation mode");
    }

    public void setMode(int mode) {
        if(mode < 0 || mode > 3) {
            throw new RuntimeException("mode must be in range [0, 3]");
        }
        use();
        GLES20.glUniform1i(mModeHandle, mode);
    }
}
