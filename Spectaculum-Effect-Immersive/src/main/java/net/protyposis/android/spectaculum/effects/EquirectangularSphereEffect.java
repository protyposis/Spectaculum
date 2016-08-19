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

    private EquirectangularSphereShaderProgram mShaderProgram;
    private float mRotX, mRotY, mRotZ;
    private float[] mRotationMatrix = new float[16];
    private Mode mMode;

    @Override
    protected TextureShaderProgram initShaderProgram() {
        mShaderProgram = new EquirectangularSphereShaderProgram();

        mRotX = 0.0f;
        mRotY = 0.0f;
        mRotZ = 0.0f;
        Matrix.setIdentityM(mRotationMatrix, 0);
        mMode = Mode.MONO;

        mShaderProgram.setRotationMatrix(mRotationMatrix);

        addParameter(new FloatParameter("RotX", -360.0f, 360.0f, mRotX, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotX = value;
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the X-axis in degrees"));
        addParameter(new FloatParameter("RotY", -360.0f, 360.0f, mRotY, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotY = -value; // invert to rotate to the right with a positive value
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the Y-axis in degrees"));
        addParameter(new FloatParameter("RotZ", -360.0f, 360.0f, mRotZ, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotZ = value;
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the Z-axis in degrees"));
        addParameter(new EnumParameter<>("VR Mode", Mode.class, mMode, new EnumParameter.Delegate<Mode>() {
            @Override
            public void setValue(Mode value) {
                mMode = value;
                mShaderProgram.setMode(mMode.ordinal());
            }
        }, "Sets the VR mode"));

        return mShaderProgram;
    }

    private void updateRotationMatrix() {
        GLUtils.Matrix.setRotateEulerM(mRotationMatrix, 0, mRotX, mRotY, mRotZ);
        mShaderProgram.setRotationMatrix(mRotationMatrix);
    }

    /**
     * Sets the rotation matrix directly without going through the 3 parameters and provoking
     * lots of function calls (rotation matrix can be updated very frequently).
     * @param R a 4x4 rotation matrix
     */
    public void setRotationMatrix(float[] R) {
        if(R.length < 16) {
            throw new RuntimeException("4x4 matrix expected");
        }

        // Take a copy of the matrix into the local variable
        System.arraycopy(R, 0, mRotationMatrix, 0, 16);

        // Update the shader rotation matrix on the correct thread
        getParameterHandler().post(mRotationMatrixUpdateRunnable);

        // Fire event to trigger a view update
        fireEffectChanged();
    }

    private Runnable mRotationMatrixUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mShaderProgram.setRotationMatrix(mRotationMatrix);
        }
    };
}
