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

package net.protyposis.android.spectaculum.gles.immersive;

import android.opengl.GLES20;

import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by Mario on 11.08.2016.
 */
public class EquirectangularSphereShaderProgram extends TextureShaderProgram {

    protected int mRotationMatrixHandle;
    protected int mModeHandle;

    public EquirectangularSphereShaderProgram() {
        super("fs_equirectangularsphere.glsl");

        mRotationMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "rotation");
        GLUtils.checkError("glGetUniformLocation rotation");

        mModeHandle = GLES20.glGetUniformLocation(mProgramHandle, "mode");
        GLUtils.checkError("glGetUniformLocation mode");
    }

    public void setRotationMatrix(float[] rotationMatrix) {
        use();
        GLES20.glUniformMatrix4fv(mRotationMatrixHandle, 1, false, rotationMatrix, 0);
    }

    public void setMode(int mode) {
        if(mode < 0 || mode > 2) {
            throw new RuntimeException("mode must be in range [0, 2]");
        }
        use();
        GLES20.glUniform1i(mModeHandle, mode);
    }
}
