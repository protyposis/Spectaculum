/*
 * Copyright (c) 2016 Mario Guggenberger <mg@protyposis.net>
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
