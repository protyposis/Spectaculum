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
public class FloatParameter extends BaseParameter<Float> {

    public interface Delegate extends Parameter.Delegate<Float> {
    }

    private float mMin;
    private float mMax;
    private float mDefault;
    private float mValue;

    public FloatParameter(String name, float min, float max, float init, Delegate delegate, String description) {
        super(name, delegate, description);
        mMin = min;
        mMax = max;
        mDefault = init;
        mValue = init;
    }

    public FloatParameter(String name, float min, float max, float init, Delegate delegate) {
        this(name, min, max, init, delegate, null);
    }

    public float getValue() {
        return mValue;
    }

    public void setValue(Float value) {
        mValue = value;
        setDelegateValue(mValue);
    }

    public float getMin() {
        return mMin;
    }

    public float getMax() {
        return mMax;
    }

    public float getDefault() {
        return mDefault;
    }

    @Override
    public void reset() {
        mValue = mDefault;
        setDelegateValue(mValue);
    }
}
