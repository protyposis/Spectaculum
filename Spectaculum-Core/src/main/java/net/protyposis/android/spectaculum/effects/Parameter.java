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

/**
 * Created by maguggen on 21.08.2014.
 */
public interface Parameter<T> {

    interface Delegate<T> {
        void setValue(T value);
    }

    interface Listener {
        void onParameterChanged(Parameter parameter);
    }

    enum Type {
        INTEGER,
        FLOAT,
        ENUM
    }

    Type getType();

    String getName();

    String getDescription();

    void reset();

    void setListener(Listener listener);

    /**
     * Sets a ParameterHandler on which parameter value changes will be executed. Parameter values
     * need to be set on the GL thread where the effect that the parameter belongs is active, and
     * this handler can be used to hand the parameter setting over to the GL thread.
     * If no handler is set, parameters will be set on the caller thread.
     * @param handler the parameter handler to set, or null to unset
     */
    void setHandler(ParameterHandler handler);
}
