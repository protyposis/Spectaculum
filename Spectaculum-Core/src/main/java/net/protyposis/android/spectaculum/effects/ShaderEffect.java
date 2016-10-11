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

import net.protyposis.android.spectaculum.gles.Framebuffer;
import net.protyposis.android.spectaculum.gles.Texture2D;
import net.protyposis.android.spectaculum.gles.TextureShaderProgram;
import net.protyposis.android.spectaculum.gles.TexturedRectangle;

/**
 * A base class for an effect with a single shader program.
 * Created by Mario on 18.07.2014.
 */
public abstract class ShaderEffect extends BaseEffect {

    private TexturedRectangle mTexturedRectangle;
    private TextureShaderProgram mShaderProgram;

    protected ShaderEffect(String name) {
        super(name);
    }

    protected ShaderEffect() {
    }

    protected abstract TextureShaderProgram initShaderProgram();

    public void init(int width, int height) {
        getParameters().clear();

        /* Block events while initializing effect to avoid parameter added events
         * (they should only be fired after construction/initialization) */
        // TODO deliver the events on the UI thread
        setEventBlocking(true);
        mShaderProgram = initShaderProgram();
        reset(); // initialize shader program with default values
        mShaderProgram.setTextureSize(width, height);
        setEventBlocking(false);

        mTexturedRectangle = new TexturedRectangle();
        mTexturedRectangle.reset();

        setInitialized();
    }

    public TextureShaderProgram getShaderProgram() {
        return mShaderProgram;
    }

    @Override
    public void apply(Texture2D source, Framebuffer target) {
        target.bind();
        mShaderProgram.use();
        mShaderProgram.setTexture(source);
        mTexturedRectangle.draw(mShaderProgram);
    }
}
