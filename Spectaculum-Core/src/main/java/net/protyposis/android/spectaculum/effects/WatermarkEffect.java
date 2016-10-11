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

package net.protyposis.android.spectaculum.effects;

import android.graphics.Bitmap;

import net.protyposis.android.spectaculum.gles.Texture2D;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;
import net.protyposis.android.spectaculum.gles.WatermarkShaderProgram;

/**
 * Watermarks the content with a bitmap.
 */
public class WatermarkEffect extends ShaderEffect {

    private WatermarkShaderProgram mShaderProgram;
    private float mScale;
    private float mOpacity;

    private Bitmap mWatermarkBitmap;
    private Texture2D mWatermarkTexture;

    private FloatParameter mScaleParameter;
    private FloatParameter mOpacityParameter;

    public WatermarkEffect() {
        mScale = 1.0f;
        mOpacity = 1.0f;
    }

    @Override
    protected TextureShaderProgram initShaderProgram() {
        mShaderProgram = new WatermarkShaderProgram();

        mScaleParameter = new FloatParameter("Scale", 0f, 10f, mScale, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mShaderProgram.setWatermarkScale(value);
            }
        });
        addParameter(mScaleParameter);

        mOpacityParameter = new FloatParameter("Opacity", 0f, 1f, mOpacity, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mShaderProgram.setWatermarkOpacity(value);
            }
        });
        addParameter(mOpacityParameter);

        if(mWatermarkBitmap != null) {
            mWatermarkTexture = new Texture2D(mWatermarkBitmap);
            mShaderProgram.setWatermark(mWatermarkTexture);
        }

        return mShaderProgram;
    }

    public void setWatermark(Bitmap watermark) {
        // TODO clear previous texture
        mWatermarkBitmap = watermark;
        if(isInitialized()) {
            mWatermarkTexture = new Texture2D(watermark);
            mShaderProgram.setWatermark(mWatermarkTexture);
        }
    }

    public void setScale(float scale) {
        if(isInitialized()) {
            mScaleParameter.setValue(scale);
        } else {
            mScale = scale;
        }
    }

    public void setOpacity(float opacity) {
        if(isInitialized()) {
            mOpacityParameter.setValue(opacity);
        } else {
            mOpacity = opacity;
        }
    }
}
