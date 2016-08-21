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

import net.protyposis.android.spectaculum.gles.ContrastBrightnessAdjustmentShaderProgram;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by Mario on 02.10.2014.
 */
public class ContrastBrightnessAdjustmentEffect extends ShaderEffect {

    private float mContrast;
    private float mBrightness;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final ContrastBrightnessAdjustmentShaderProgram adjustmentsShader = new ContrastBrightnessAdjustmentShaderProgram();

        mContrast = 1.0f;
        mBrightness = 1.0f;

        addParameter(new FloatParameter("Contrast", 0.0f, 5.0f, mContrast, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mContrast = value;
                adjustmentsShader.setContrast(mContrast);
            }
        }));
        addParameter(new FloatParameter("Brightness", 0.0f, 5.0f, mBrightness, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mBrightness = value;
                adjustmentsShader.setBrightness(mBrightness);
            }
        }));

        return adjustmentsShader;
    }
}
