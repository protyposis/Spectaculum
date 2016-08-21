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

/**
 * Created by Mario on 21.08.2016.
 */
public class BooleanParameter extends BaseParameter<Boolean> {

    public interface Delegate extends Parameter.Delegate<Boolean> {
    }

    private boolean mDefault;
    private boolean mValue;

    public BooleanParameter(String name, boolean init, Delegate delegate, String description) {
        super(name, delegate, description);
        mDefault = init;
        mValue = init;
    }

    public BooleanParameter(String name, boolean init, Delegate delegate) {
        this(name, init, delegate, null);
    }

    public boolean getValue() {
        return mValue;
    }

    public void setValue(boolean value) {
        mValue = value;
        setDelegateValue(mValue);
    }

    public boolean getDefault() {
        return mDefault;
    }

    @Override
    public void reset() {
        mValue = mDefault;
        setDelegateValue(mValue);
    }
}
