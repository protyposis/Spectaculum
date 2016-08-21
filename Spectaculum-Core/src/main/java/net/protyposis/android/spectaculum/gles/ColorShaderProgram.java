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
public class ColorShaderProgram extends ShaderProgram {

    protected int mMVPMatrixHandle;
    protected int mPositionHandle;
    protected int mColorHandle;

    public ColorShaderProgram() {
        super("vs_color.glsl", "fs_color.glsl");

        // NOTE this could be moved to shape objects (would result in more overhead by multiple calls,
        //      but gives more freedom with the usage of shader variables)
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        GLUtils.checkError("glGetUniformLocation u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        GLUtils.checkError("glGetAttribLocation a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        GLUtils.checkError("glGetAttribLocation a_Color");
    }
}
