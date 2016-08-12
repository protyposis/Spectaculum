/*
 * Copyright (c) 2016 Mario Guggenberger <mg@protyposis.net>
 *
 * This file is part of MediaPlayer-Extended.
 *
 * MediaPlayer-Extended is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MediaPlayer-Extended is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MediaPlayer-Extended.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.protyposis.android.mediaplayer.effects;

import net.protyposis.android.mediaplayer.gles.TextureShaderProgram;
import net.protyposis.android.mediaplayer.gles.immersive.SphereShaderProgram;

/**
 * Created by Mario on 11.08.2016.
 */
public class SphereEffect extends ShaderEffect {

    private float mRotX, mRotY;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final SphereShaderProgram sphereShader = new SphereShaderProgram();

        mRotX = 0.0f;
        mRotY = 0.0f;

        sphereShader.setRotX(mRotX);
        sphereShader.setRotY(mRotY);

        addParameter(new FloatParameter("RotX", -1.0f, 1.0f, mRotX, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mRotX = value;
                sphereShader.setRotX(mRotX);
            }
        }));
        addParameter(new FloatParameter("RotY", -1.0f, 1.0f, mRotY, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mRotY = value;
                sphereShader.setRotY(mRotY);
            }
        }));

        return sphereShader;
    }
}
