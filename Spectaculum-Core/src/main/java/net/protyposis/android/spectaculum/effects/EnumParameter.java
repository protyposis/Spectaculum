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
 * Created by Mario on 16.08.2016.
 */
public class EnumParameter<T extends Enum<T>> extends BaseParameter<T> {

    private T mDefault;
    private T mValue;
    private T[] mValues;

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate<T> delegate, String description) {
        super(name, delegate, description);
        mDefault = init;
        mValue = init;

        mValues = enumClass.getEnumConstants();
    }

    public EnumParameter(String name, Class<T> enumClass, T init, Delegate<T> delegate) {
        this(name, enumClass, init, delegate, null);
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
        setDelegateValue(mValue);
    }


    public T getDefault() {
        return mDefault;
    }

    public T[] getEnumValues() {
        return mValues;
    }

    @Override
    public void reset() {
        mValue = mDefault;
        setDelegateValue(mValue);
    }
}
