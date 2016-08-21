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

import net.protyposis.android.spectaculum.gles.TextureFlipShaderProgram;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;

/**
 * Created by maguggen on 22.08.2014.
 */
public class FlipEffect extends ShaderEffect {

    public enum Mode {
        NONE,
        VERTICAL,
        HORIZONTAL,
        BOTH
    }

    private Mode mMode;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final TextureFlipShaderProgram flipShader = new TextureFlipShaderProgram();
        mMode = Mode.VERTICAL;

        flipShader.setMode(mMode.ordinal());

        addParameter(new EnumParameter<>("Mode", Mode.class, mMode, new EnumParameter.Delegate<Mode>() {
            @Override
            public void setValue(Mode value) {
                mMode = value;
                flipShader.setMode(mMode.ordinal());
            }
        }));

        return flipShader;
    }
}
