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

package net.protyposis.android.spectaculum.effects;

import net.protyposis.android.spectaculum.gles.TextureShaderProgram;
import net.protyposis.android.spectaculum.gles.TextureSobelShaderProgram;

/**
 * Created by Mario on 07.09.2014.
 */
public class SobelEffect extends ShaderEffect {

    private float mLow, mHigh;
    private float mR, mG, mB;

    public SobelEffect() {
        super("Sobel Edge Detect");
    }

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final TextureSobelShaderProgram sobelShader = new TextureSobelShaderProgram();

        mLow = 0.3f;
        mHigh = 0.8f;
        mR = 0.0f;
        mG = 1.0f;
        mB = 0.0f;

        addParameter(new FloatParameter("Low", 0.0f, 1.0f, mLow, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mLow = value;
                sobelShader.setThreshold(mLow, mHigh);
            }
        }));
        addParameter(new FloatParameter("High", 0.0f, 1.0f, mHigh, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mHigh = value;
                sobelShader.setThreshold(mLow, mHigh);
            }
        }));
        addParameter(new FloatParameter("Red", 0.0f, 1.0f, mR, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mR = value;
                sobelShader.setColor(mR, mG, mB);
            }
        }));
        addParameter(new FloatParameter("Green", 0.0f, 1.0f, mG, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mG = value;
                sobelShader.setColor(mR, mG, mB);
            }
        }));
        addParameter(new FloatParameter("Blue", 0.0f, 1.0f, mB, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mB = value;
                sobelShader.setColor(mR, mG, mB);
            }
        }));

        return sobelShader;
    }
}
