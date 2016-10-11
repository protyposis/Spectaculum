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

import net.protyposis.android.spectaculum.gles.Framebuffer;
import net.protyposis.android.spectaculum.gles.Texture2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Creates a stack of effects that are applied sequentially one by one. Useful to combine effects
 * together, e.g. convert the image with a toon effect, adjust its brightness and add a watermark on top.
 */
public class StackEffect extends BaseEffect {

    private List<Effect> mEffects;
    private Framebuffer mFramebuffer;

    public StackEffect(String name) {
        super(name);
        mEffects = new ArrayList<>();
    }

    public StackEffect(String name, Effect... effects) {
        this(name);
        addEffects(effects);
    }

    public StackEffect() {
        this((String)null);
    }

    public StackEffect(Effect... effects) {
        this(null, effects);
    }

    public void addEffects(Effect... effects) {
        Collections.addAll(mEffects, effects);
    }

    @Override
    public void init(int width, int height) {
        // Create an internal framebuffer which is required to apply a sequence of effects
        mFramebuffer = new Framebuffer(width, height);

        // Initialize all effects
        for (Effect e : mEffects) {
            e.init(width, height);

            // Add effect parameters
            for(Parameter p : e.getParameters()) {
                addParameter(p);
            }
        }
    }

    @Override
    public void apply(Texture2D source, Framebuffer target) {
        Iterator<Effect> i = mEffects.iterator();

        /*
         * The first source texture must always be the passed in texture, the last output framebuffer
         * must always be the passed in target framebuffer. In between, we need to switch source
         * textures and target framebuffers between the passed in external framebuffer and the internal
         * framebuffer, because we cannot read and write to the same framebuffer in one render pass.
         * If the number of effects is even, we start by writing the internal framebuffer, else we
         * start with the external framebuffer.
         */
        Framebuffer internalFB = mFramebuffer;
        Framebuffer externalFB = target;
        boolean useInternalFB = mEffects.size() % 2 == 0; // keeps track of which framebuffer to use as target

        while(i.hasNext()) {
            Effect e = i.next();

            if(i.hasNext()) {
                e.apply(source, useInternalFB ? internalFB : externalFB);
                source = useInternalFB ? internalFB.getTexture() : externalFB.getTexture();
                useInternalFB = !useInternalFB; // switch framebuffer flag
            } else {
                // Last effect; always write result to the target framebuffer
                e.apply(source, target);
            }
        }
    }
}
