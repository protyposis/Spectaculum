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
    @Deprecated private Listener mListener;
    private ParameterHandler mParameterHandler;
    private boolean mBlockEvents;
    private List<Listener> mListeners;

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
        mListeners = new ArrayList<>();
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
        parameter.addListener(this);
        parameter.setHandler(mParameterHandler);
        if(!mBlockEvents) {
            fireParameterAdded(parameter);
        }
    }

    @Override
    public void removeParameter(Parameter parameter) {
        mParameters.remove(parameter);
        parameter.removeListener(this);
        parameter.setHandler(null);
        if(!mBlockEvents) {
            fireParameterRemoved(parameter);
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
        // Remove previously set listener
        if (mListener != null) {
            removeListener(mListener);
        }

        // Add the new listener
        // (or do nothing if null was passed in to just remove the previous listener)
        if (listener != null) {
            addListener(listener);
        }

        // Store the listener so we can remove it later
        mListener = listener;
    }

    @Override
    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onParameterChanged(Parameter parameter) {
        fireEffectChanged();
    }

    protected void fireEffectChanged() {
        if (!mListeners.isEmpty()) {
            for (Listener listener : mListeners) {
                listener.onEffectChanged(this);
            }
        }
    }

    protected void fireParameterAdded(Parameter parameter) {
        if (!mListeners.isEmpty()) {
            for (Listener listener : mListeners) {
                listener.onParameterAdded(this, parameter);
            }
        }
    }

    protected void fireParameterRemoved(Parameter parameter) {
        if (!mListeners.isEmpty()) {
            for (Listener listener : mListeners) {
                listener.onParameterRemoved(this, parameter);
            }
        }
    }

    protected void setEventBlocking(boolean blockEvents) {
        mBlockEvents = blockEvents;
    }
}
