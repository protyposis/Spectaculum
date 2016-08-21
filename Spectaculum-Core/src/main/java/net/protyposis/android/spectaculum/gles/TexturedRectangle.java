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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Mario on 14.06.2014.
 */
public class TexturedRectangle extends Shape {

    /**
     * Elements per vertex
     * */
    protected static final int sStrideBytes = 5 * sBytesPerFloat;

    /**
     * Offset of the position data
     * */
    protected static final int sPositionOffset = 0;

    /**
     * Size of the position data in elements
     * */
    protected static final int sPositionDataSize = 3;

    /**
     * Offset of the color data
     * */
    protected static final int sUVOffset = 3;

    /**
     * Size of the color data in elements
     * */
    protected static final int sUVDataSize = 2;

    // model data
    private float[] mVerticesData = {
            // X, Y, Z,
            // U, V
            -1.0f, -1.0f, 0.0f,
            0.0f, 0.0f,

            1.0f, -1.0f, 0.0f,
            1.0f, 0.0f,

            -1.0f, 1.0f, 0.0f,
            0.0f, 1.0f,

            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f
    };

    // model data buffer
    private FloatBuffer mVertices;

    public TexturedRectangle() {
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * sBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }

    public void draw(TextureShaderProgram shaderProgram) {

        // write vertex data
        mVertices.position(sPositionOffset);
        GLES20.glVertexAttribPointer(shaderProgram.mPositionHandle, sPositionDataSize,
                GLES20.GL_FLOAT, false, sStrideBytes, mVertices);
        GLES20.glEnableVertexAttribArray(shaderProgram.mPositionHandle);

        // write uv data
        mVertices.position(sUVOffset);
        GLES20.glVertexAttribPointer(shaderProgram.mTextureCoordHandle, sUVDataSize,
                GLES20.GL_FLOAT, false, sStrideBytes, mVertices);
        GLES20.glEnableVertexAttribArray(shaderProgram.mTextureCoordHandle);

        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

        // write the MVP matrix
        GLES20.glUniformMatrix4fv(shaderProgram.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // finally, render the rectangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLUtils.checkError("TexturedRectangle.draw");
    }
}
