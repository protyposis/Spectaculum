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

package net.protyposis.android.spectaculum.effects;

import android.opengl.Matrix;

import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;
import net.protyposis.android.spectaculum.gles.immersive.EquirectangularSphereShaderProgram;

/**
 * Created by Mario on 11.08.2016.
 */
public class EquirectangularSphereEffect extends ShaderEffect {

    public enum Mode {
        MONO,
        STEREO_SBS,
        STEREO_TAB,
    }

    private float mRotX, mRotY, mRotZ;
    private float[] mRotationMatrix = new float[16];
    private Mode mMode;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        final EquirectangularSphereShaderProgram sphereShader = new EquirectangularSphereShaderProgram();

        mRotX = 0.0f;
        mRotY = 0.0f;
        mRotZ = 0.0f;
        Matrix.setIdentityM(mRotationMatrix, 0);
        mMode = Mode.MONO;

        sphereShader.setRotationMatrix(mRotationMatrix);

        addParameter(new FloatParameter("RotX", -360.0f, 360.0f, mRotX, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mRotX = value;
                updateRotationMatrix(sphereShader);
            }
        }, "Sets the rotation angle around the X-axis in degrees"));
        addParameter(new FloatParameter("RotY", -360.0f, 360.0f, mRotY, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mRotY = -value; // invert to rotate to the right with a positive value
                updateRotationMatrix(sphereShader);
            }
        }, "Sets the rotation angle around the Y-axis in degrees"));
        addParameter(new FloatParameter("RotZ", -360.0f, 360.0f, mRotZ, new FloatParameter.Delegate() {
            @Override
            public void setValue(float value) {
                mRotZ = value;
                updateRotationMatrix(sphereShader);
            }
        }, "Sets the rotation angle around the Z-axis in degrees"));
        addParameter(new EnumParameter<>("VR Mode", Mode.class, mMode, new EnumParameter.Delegate<Mode>() {
            @Override
            public void setValue(Mode value) {
                mMode = value;
                sphereShader.setMode(mMode.ordinal());
            }
        }, "Sets the VR mode"));

        return sphereShader;
    }

    private void updateRotationMatrix(EquirectangularSphereShaderProgram sphereShader) {
        GLUtils.Matrix.setRotateEulerM(mRotationMatrix, 0, mRotX, mRotY, mRotZ);
        sphereShader.setRotationMatrix(mRotationMatrix);
    }
}
