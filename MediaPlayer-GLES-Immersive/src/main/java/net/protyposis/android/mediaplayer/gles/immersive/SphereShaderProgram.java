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

package net.protyposis.android.mediaplayer.gles.immersive;

import android.opengl.GLES20;

import net.protyposis.android.mediaplayer.gles.GLUtils;
import net.protyposis.android.mediaplayer.gles.TextureShaderProgram;

/**
 * Created by Mario on 11.08.2016.
 */
public class SphereShaderProgram extends TextureShaderProgram {

    protected int mRotXHandle;
    protected int mRotYHandle;

    public SphereShaderProgram() {
        super("fs_sphere.s");

        mRotXHandle = GLES20.glGetUniformLocation(mProgramHandle, "rot_x");
        GLUtils.checkError("glGetUniformLocation rot_x");

        mRotYHandle = GLES20.glGetUniformLocation(mProgramHandle, "rot_y");
        GLUtils.checkError("glGetUniformLocation rot_x");
    }

    public void setRotX(float rotX) {
        use();
        GLES20.glUniform1f(mRotXHandle, rotX);
    }

    public void setRotY(float rotY) {
        use();
        GLES20.glUniform1f(mRotYHandle, rotY);
    }
}
