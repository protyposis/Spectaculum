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

/**
 * Created by maguggen on 21.08.2014.
 */
public abstract class BaseParameter<T> implements Parameter<T> {

    private String mName;
    private Delegate<T> mDelegate;
    private String mDescription;
    private Listener mListener;
    private ParameterHandler mHandler;

    protected BaseParameter(String name, Delegate<T> delegate) {
        mName = name;
        mDelegate = delegate;
    }

    public BaseParameter(String name, Delegate<T> delegate, String description) {
        this(name, delegate);
        this.mDescription = description;
    }

    public String getName() {
        return mName;
    }

    protected Delegate<T> getDelegate() {
        return mDelegate;
    }

    public String getDescription() {
        return mDescription;
    }

    public abstract void reset();

    public void setListener(Listener listener) {
        mListener = listener;
    }

    protected void fireParameterChanged() {
        if(mListener != null) {
            mListener.onParameterChanged(this);
        }
    }

    protected void setDelegateValue(final T value) {
        if(mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDelegate.setValue(value);
                }
            });
        } else {
            mDelegate.setValue(value);
        }
        fireParameterChanged();
    }

    /**
     * Sets a ParameterHandler on which parameter value changes will be executed. Parameter values
     * need to be set on the GL thread where the effect that the parameter belongs is active, and
     * this handler can be used to hand the parameter setting over to the GL thread.
     * If no handler is set, parameters will be set on the caller thread.
     * @param handler the parameter handler to set, or null to unset
     */
    public void setHandler(ParameterHandler handler) {
        mHandler = handler;
    }
}
