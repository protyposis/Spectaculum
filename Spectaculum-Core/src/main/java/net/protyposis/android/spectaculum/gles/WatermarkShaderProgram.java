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

package net.protyposis.android.spectaculum.gles;

import android.opengl.GLES20;

/**
 * Created by Mario on 11.10.2016.
 */

public class WatermarkShaderProgram extends TextureShaderProgram {

    private int mWatermarkHandle;
    private int mWatermarkSizeHandle;
    private int mWatermarkScaleHandle;
    private int mWatermarkOpacityHandle;
    private int mWatermarkMarginHandle;
    private int mWatermarkAlignmentHandle;

    public WatermarkShaderProgram() {
        super("fs_watermark.glsl");

        mWatermarkHandle = GLES20.glGetUniformLocation(mProgramHandle, "watermark");
        GLUtils.checkError("glGetUniformLocation watermark");
        mWatermarkSizeHandle = GLES20.glGetUniformLocation(mProgramHandle, "size");
        GLUtils.checkError("glGetUniformLocation size");
        mWatermarkScaleHandle = GLES20.glGetUniformLocation(mProgramHandle, "scale");
        GLUtils.checkError("glGetUniformLocation scale");
        mWatermarkOpacityHandle = GLES20.glGetUniformLocation(mProgramHandle, "opacity");
        GLUtils.checkError("glGetUniformLocation opacity");
        mWatermarkMarginHandle = GLES20.glGetUniformLocation(mProgramHandle, "margin");
        GLUtils.checkError("glGetUniformLocation margin");
        mWatermarkAlignmentHandle = GLES20.glGetUniformLocation(mProgramHandle, "alignment");
        GLUtils.checkError("glGetUniformLocation alignment");

        use();
        setWatermarkScale(1);
        setWatermarkOpacity(1);
    }

    public void setWatermark(Texture2D watermarkTexture) {
        use();

        // Use TEXTURE1 for the watermark, TEXTURE0 is taken by the input
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, watermarkTexture.getHandle());
        GLES20.glUniform1i(mWatermarkHandle, 1); // bind texture unit 1 to the uniform

        GLES20.glUniform2f(mWatermarkSizeHandle, watermarkTexture.getWidth(), watermarkTexture.getHeight());
    }

    public void setWatermarkScale(float scale) {
        if(scale < 0 || scale > 10) {
            throw new RuntimeException("scale must be in range [0, 10]");
        }
        use();
        GLES20.glUniform1f(mWatermarkScaleHandle, scale);
    }

    public void setWatermarkOpacity(float opacity) {
        if(opacity < 0 || opacity > 1) {
            throw new RuntimeException("opacity must be in range [0, 1]");
        }
        use();
        GLES20.glUniform1f(mWatermarkOpacityHandle, opacity);
    }

    public void setWatermarkMargin(float x, float y) {
        use();
        GLES20.glUniform2f(mWatermarkMarginHandle, x, y);
    }

    public void setWatermarkAlignment(int alignment) {
        use();
        GLES20.glUniform1i(mWatermarkAlignmentHandle, alignment);
    }
}
