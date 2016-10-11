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

import net.protyposis.android.spectaculum.effects.FloatParameter;
import net.protyposis.android.spectaculum.effects.ShaderEffect;
import net.protyposis.android.spectaculum.gles.ColorFilterShaderProgram;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by Mario on 19.07.2014.
 */
public class ColorFilterEffect extends ShaderEffect {

    private float mR, mG, mB, mA;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final ColorFilterShaderProgram colorFilterShader = new ColorFilterShaderProgram();

        mR = 1.0f;
        mG = 0.0f;
        mB = 0.0f;
        mA = 1.0f;

        addParameter(new FloatParameter("Red", 0.0f, 1.0f, mR, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mR = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Green", 0.0f, 1.0f, mG, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mG = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Blue", 0.0f, 1.0f, mB, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mB = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));
        addParameter(new FloatParameter("Alpha", 0.0f, 1.0f, mA, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mA = value;
                colorFilterShader.setColor(mR, mG, mB, mA);
            }
        }));

        return colorFilterShader;
    }
}
