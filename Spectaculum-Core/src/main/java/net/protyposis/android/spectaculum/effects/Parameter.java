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
 * Interface to a parameter of an effect.
 * Created by maguggen on 21.08.2014.
 */
public interface Parameter<T> {

    interface Delegate<T> {
        void setValue(T value);
    }

    /**
     * Callback interface for parameter events.
     */
    interface Listener {
        /**
         * Gets called when the value of a parameter has changed.
         * @param parameter the parameter whose value has changed
         */
        void onParameterChanged(Parameter parameter);
    }

    /**
     * Gets the name of the parameter.
     */
    String getName();

    /**
     * Gets the description of the parameter. Returns null if no description has been set.
     */
    String getDescription();

    /**
     * Resets the parameter to its default value.
     */
    void reset();

    /**
     * Sets an event listener to listen for parameter value change events.
     * @see Listener#onParameterChanged(Parameter)
     * @param listener the listener to notify of events
     * @deprecated Will be removed in next major version, use {@link #addListener} instead
     */
    void setListener(Listener listener);

    /**
     * Adds an event listener to listen for parameter value change events.
     * @see Listener#onParameterChanged(Parameter)
     * @param listener the listener to notify of events
     */
    void addListener(Listener listener);

    /**
     * Removes an event listener added with {@link #addListener}.
     * @param listener the listener to remove
     */
    void removeListener(Listener listener);

    /**
     * Sets a ParameterHandler on which parameter value changes will be executed. Parameter values
     * need to be set on the GL thread where the effect that the parameter belongs is active, and
     * this handler can be used to hand the parameter setting over to the GL thread.
     * If no handler is set, parameters will be set on the caller thread.
     * @param handler the parameter handler to set, or null to unset
     */
    void setHandler(ParameterHandler handler);
}
