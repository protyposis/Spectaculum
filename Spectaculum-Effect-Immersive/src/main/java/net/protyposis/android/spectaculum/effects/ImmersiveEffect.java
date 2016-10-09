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

import android.opengl.Matrix;

import net.protyposis.android.spectaculum.gles.GLUtils;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;
import net.protyposis.android.spectaculum.gles.immersive.EquirectangularSphereShaderProgram;

/**
 * Created by Mario on 11.08.2016.
 */
public class ImmersiveEffect extends ShaderEffect {

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
        addParameter(new EnumParameter<>("Mode", Mode.class, mMode, new EnumParameter.Delegate<Mode>() {
            @Override
            public void setValue(Mode value) {
                mMode = value;
                mShaderProgram.setMode(mMode.ordinal());
            }
        }, "Sets the render mode"));

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
