/*
 * Copyright (c) 2014 Mario Guggenberger <mg@protyposis.net>
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
abstract class BaseEffect implements Effect, Parameter.Listener {

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
