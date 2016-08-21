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

package net.protyposis.android.spectaculum.gles;

import android.opengl.Matrix;

/**
 * Created by Mario on 14.06.2014.
 */
public abstract class Shape {

    /**
     * bytes per float
     */
    protected static final int sBytesPerFloat = 4;

    /**
     * The model matrix positions models in world space
     */
    public float[] mModelMatrix = new float[16];

    /**
     * The final model-view-projection matrix used for rendering
     * NOTE: could be made static to save space, since objects calculate their matrix sequentially
     */
    protected float[] mMVPMatrix = new float[16];

    public void reset() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(mModelMatrix, 0, x, y, z);
    }

    public void calculateMVP(float[] viewMatrix, float[] projectionMatrix) {
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
    }
}
