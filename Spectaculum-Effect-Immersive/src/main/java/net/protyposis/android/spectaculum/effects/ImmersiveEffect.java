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
 * Effect to render immersive / 360° / VR content.
 */
public class ImmersiveEffect extends ShaderEffect {

    /**
     * Image source render mode.
     */
    public enum Mode {
        /**
         * Monoscopic rendering of mono sources.
         */
        MONO,

        /**
         * Stereoscopic rendering of side-by-side (SBS) sources, where two pictures are packed
         * horizontally in the image source.
         */
        STEREO_SBS,

        /**
         * Stereoscopic rendering of top-and-bottom (TAB) sources, where two pictures are packed
         * vertically in the image source.
         */
        STEREO_TAB,
    }

    private EquirectangularSphereShaderProgram mShaderProgram;
    private float mRotX, mRotY, mRotZ;
    private float[] mRotationMatrix = new float[16];
    private Mode mMode;

    private FloatParameter mParameterRotX, mParameterRotY, mParameterRotZ;
    private EnumParameter<Mode> mParameterMode;

    public ImmersiveEffect() {
        mRotX = 0.0f;
        mRotY = 0.0f;
        mRotZ = 0.0f;
        Matrix.setIdentityM(mRotationMatrix, 0);
        mMode = Mode.MONO;
    }

    @Override
    protected TextureShaderProgram initShaderProgram() {
        mShaderProgram = new EquirectangularSphereShaderProgram();

        mParameterRotX = new FloatParameter("RotX", -360.0f, 360.0f, mRotX, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotX = value;
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the X-axis in degrees");
        mParameterRotY = new FloatParameter("RotY", -360.0f, 360.0f, mRotY, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotY = -value; // invert to rotate to the right with a positive value
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the Y-axis in degrees");
        mParameterRotZ = new FloatParameter("RotZ", -360.0f, 360.0f, mRotZ, new FloatParameter.Delegate() {
            @Override
            public void setValue(Float value) {
                mRotZ = value;
                updateRotationMatrix();
            }
        }, "Sets the rotation angle around the Z-axis in degrees");
        mParameterMode = new EnumParameter<>("Mode", Mode.class, mMode, new EnumParameter.Delegate<Mode>() {
            @Override
            public void setValue(Mode value) {
                mMode = value;
                mShaderProgram.setMode(mMode.ordinal());
            }
        }, "Sets the render mode");

        addParameter(mParameterRotX);
        addParameter(mParameterRotY);
        addParameter(mParameterRotZ);
        addParameter(mParameterMode);

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

        if(isInitialized()) {
            // Update the shader rotation matrix on the correct thread
            getParameterHandler().post(mRotationMatrixUpdateRunnable);

            // Fire event to trigger a view update
            fireEffectChanged();
        }
    }

    /**
     * Gets the rotation matrix. The rotation matrix will be written into the supplied
     * parameter.
     * @param R a 4x4 output matrix
     */
    public void getRotationMatrix(float[] R) {
        if(R.length < 16) {
            throw new RuntimeException("4x4 matrix expected");
        }

        // Copy the local matrix into the output matrix
        System.arraycopy(mRotationMatrix, 0, R, 0, 16);
    }

    /**
     * Sets the rotation along the Y-axis.
     * @param rotX rotation in degrees
     */
    public void setRotationX(float rotX) {
        if(isInitialized()) {
            mParameterRotX.setValue(rotX);
        } else {
            mRotX = rotX;
        }
    }

    /**
     * Sets the rotation along the Y-axis.
     * @param rotY rotation in degrees
     */
    public void setRotationY(float rotY) {
        if(isInitialized()) {
            mParameterRotY.setValue(rotY);
        } else {
            mRotX = rotY;
        }
    }

    /**
     * Sets the rotation along the Z-axis.
     * @param rotZ rotation in degrees
     */
    public void setRotationZ(float rotZ) {
        if(isInitialized()) {
            mParameterRotZ.setValue(rotZ);
        } else {
            mRotX = rotZ;
        }
    }

    /**
     * Sets the content render mode. Should be set to match the image source.
     * @param mode the image source render mode
     */
    public void setMode(Mode mode) {
        if(isInitialized()) {
            mParameterMode.setValue(mode);
        } else {
            mMode = mode;
        }
    }

    private Runnable mRotationMatrixUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mShaderProgram.setRotationMatrix(mRotationMatrix);
        }
    };
}
