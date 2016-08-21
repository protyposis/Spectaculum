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
public class TextureShaderProgram extends ShaderProgram {

    protected int mMVPMatrixHandle;
    protected int mPositionHandle;
    protected int mTextureCoordHandle;
    protected int mSTMatrixHandle;
    protected int mTextureSizeHandle;
    protected int mTextureHandle;

    public TextureShaderProgram() {
        this("vs_texture.glsl", "fs_texture.glsl");
    }

    public TextureShaderProgram(String fragmentShaderName) {
        this("vs_texture.glsl", fragmentShaderName);
    }

    protected TextureShaderProgram(String vertexShaderName, String fragmentShaderName) {
        super(vertexShaderName, fragmentShaderName);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        GLUtils.checkError("glGetUniformLocation u_MVPMatrix");
        mSTMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_STMatrix");
        GLUtils.checkError("glGetAttribLocation u_STMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        GLUtils.checkError("glGetAttribLocation a_Position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TextureCoord");
        GLUtils.checkError("glGetAttribLocation a_TextureCoord");
        mTextureSizeHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_TextureSize");
        GLUtils.checkError("glGetUniformLocation u_TextureSize");
        mTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "s_Texture");
        GLUtils.checkError("glGetUniformLocation s_Texture");
    }

    public void setTextureSize(int width, int height) {
        use();
        GLES20.glUniform2f(mTextureSizeHandle, width, height);
    }

    public void setTexture(Texture2D texture) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());
        GLES20.glUniform1i(mTextureHandle, 0); // bind texture unit 0 to the uniform
        GLES20.glUniformMatrix4fv(mSTMatrixHandle, 1, false, texture.getTransformMatrix(), 0);
    }
}
