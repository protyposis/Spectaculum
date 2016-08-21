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
public class IntegerParameter extends BaseParameter<Integer> {

    public interface Delegate extends BaseParameter.Delegate<Integer> {
    }

    private int mMin;
    private int mMax;
    private int mDefault;
    private int mValue;

    public IntegerParameter(String name, int min, int max, int init, Delegate delegate, String description) {
        super(name, delegate, description);
        mMin = min;
        mMax = max;
        mDefault = init;
        mValue = init;
    }

    public IntegerParameter(String name, int min, int max, int init, Delegate delegate) {
        this(name, min, max, init, delegate, null);
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(Integer value) {
        mValue = value;
        setDelegateValue(mValue);
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }

    public int getDefault() {
        return mDefault;
    }

    @Override
    public void reset() {
        mValue = mDefault;
        setDelegateValue(mValue);
    }
}
