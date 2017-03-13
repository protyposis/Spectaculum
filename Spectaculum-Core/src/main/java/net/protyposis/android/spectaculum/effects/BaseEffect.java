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

import java.util.ArrayList;
import java.util.List;

import net.protyposis.android.spectaculum.gles.Framebuffer;
import net.protyposis.android.spectaculum.gles.Texture2D;

/**
 * Abstract base class with common functionality that all effect implementations need. use this
 * class as base to implement advanced effects. For common effects with a single shader, extend
 * {@link ShaderEffect}.
 * Created by Mario on 18.07.2014.
 */
public abstract class BaseEffect implements Effect, Parameter.Listener {

    private String mName;
    private List<Parameter> mParameters;
    private boolean mInitialized;
    private Listener mListener;
    private ParameterHandler mParameterHandler;
    private boolean mBlockEvents;

    public BaseEffect(String name) {
        if(name == null) {
            name = this.getClass().getSimpleName();
            // remove "effect" suffix when applicable
            if(name.endsWith("Effect")) {
                name = name.substring(0, name.length() - 6);
            }
        }
        mName = name;
        mParameters = new ArrayList<>();
    }

    public BaseEffect() {
        this(null);
    }

    public String getName() {
        return mName;
    }

    public abstract void init(int width, int height);

    @Override
    public boolean isInitialized() {
        return mInitialized;
    }

    public abstract void apply(Texture2D source, Framebuffer target);

    @Override
    public void setParameterHandler(ParameterHandler handler) {
        mParameterHandler = handler;
        for(Parameter p: getParameters()) {
            p.setHandler(handler);
        }
    }

    protected ParameterHandler getParameterHandler() {
        return mParameterHandler;
    }

    @Override
    public void addParameter(Parameter parameter) {
        mParameters.add(parameter);
        parameter.setListener(this);
        parameter.setHandler(mParameterHandler);
        if(!mBlockEvents && mListener != null) {
            mListener.onParameterAdded(this, parameter);
        }
    }

    @Override
    public void removeParameter(Parameter parameter) {
        mParameters.remove(parameter);
        parameter.setListener(null);
        parameter.setHandler(null);
        if(!mBlockEvents && mListener != null) {
            mListener.onParameterRemoved(this, parameter);
        }
    }

    @Override
    public List<Parameter> getParameters() {
        return mParameters;
    }

    @Override
    public boolean hasParameters() {
        return mParameters != null && !mParameters.isEmpty();
    }

    @Override
    public void reset() {
        for(Parameter p: getParameters()) {
            p.reset();
        }
    }

    protected void setInitialized() {
        mInitialized = true;
    }

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onParameterChanged(Parameter parameter) {
        if(mListener != null) {
            mListener.onEffectChanged(this);
        }
    }

    protected void fireEffectChanged() {
        if(mListener != null) {
            mListener.onEffectChanged(this);
        }
    }

    protected void setEventBlocking(boolean blockEvents) {
        mBlockEvents = blockEvents;
    }
}
