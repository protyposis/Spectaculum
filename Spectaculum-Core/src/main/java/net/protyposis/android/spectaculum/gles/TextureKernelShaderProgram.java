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
 * Created by maguggen on 16.06.2014.
 */
public class TextureKernelShaderProgram extends TextureShaderProgram {

    public enum Kernel {

        BLUR(new float[] {
                1f/9f, 1f/9f, 1f/9f,
                1f/9f, 1f/9f, 1f/9f,
                1f/9f, 1f/9f, 1f/9f }),

        BLUR_GAUSS(new float[] {
                1f/16f, 2f/16f, 1f/16f,
                2f/16f, 4f/16f, 2f/16f,
                1f/16f, 2f/16f, 1f/16f }),

        SHARPEN(new float[] {
                0f, -1f, 0f,
                -1f, 5f, -1f,
                0f, -1f, 0f }),

        EDGE_DETECT(new float[] {
                0f, 1f, 0f,
                1f, -4f, 1f,
                0f, 1f, 0f }),

        EMBOSS(new float[] {
                -2f, -1f, 0f,
                -1f, 1f, 1f,
                0f, 1f, 2f });

        float[] mKernel;

        Kernel(float[] kernel) {
            mKernel = kernel;
        }
    }

    protected int mKernelHandle;
    protected int mTexOffsetHandle;
    protected int mColorAdjustHandle;

    public TextureKernelShaderProgram(Kernel kernel) {
        super("fs_texture_kernel.glsl");

        mKernelHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Kernel");
        GLUtils.checkError("glGetUniformLocation u_Kernel");
        mTexOffsetHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_TexOffset");
        GLUtils.checkError("glGetUniformLocation u_TexOffset");

        setKernel(kernel);
    }

    public void setKernel(Kernel kernel) {
        GLES20.glUseProgram(getHandle());
        GLES20.glUniform1fv(mKernelHandle, 9, kernel.mKernel, 0);
    }

    @Override
    public void setTextureSize(int width, int height) {
        //super.setTextureSize(width, height);
        float rw = 1.0f / width;
        float rh = 1.0f / height;

        float texOffset[] = new float[] {
                -rw, -rh,   0f, -rh,    rw, -rh,
                -rw, 0f,    0f, 0f,     rw, 0f,
                -rw, rh,    0f, rh,     rw, rh
        };

        GLES20.glUseProgram(getHandle());
        GLES20.glUniform2fv(mTexOffsetHandle, 9, texOffset, 0);
    }
}
