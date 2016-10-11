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

package net.protyposis.android.spectaculumdemo.testeffect;

import net.protyposis.android.spectaculum.effects.FloatParameter;
import net.protyposis.android.spectaculum.effects.IntegerParameter;
import net.protyposis.android.spectaculum.effects.ShaderEffect;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by Mario on 11.10.2016.
 */

public class InterlaceEffect extends ShaderEffect {
    @Override
    protected TextureShaderProgram initShaderProgram() {
        final InterlaceShaderProgram shaderProgram = new InterlaceShaderProgram();

        addParameter(new FloatParameter("Opacity", 0f, 1f, 0.5f, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                shaderProgram.setOpacity(value);
            }
        }));

        addParameter(new IntegerParameter("Distance", 1, 10, 5, new IntegerParameter.Delegate() {
            @Override
            public void setValue(Integer value) {
                shaderProgram.setDistance(value);
            }
        }));

        return shaderProgram;
    }
}
