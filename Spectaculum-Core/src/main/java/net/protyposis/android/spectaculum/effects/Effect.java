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

import java.util.List;

import net.protyposis.android.spectaculum.gles.Framebuffer;
import net.protyposis.android.spectaculum.gles.Texture2D;

/**
 * Interface to shader effects for the SpectaculumView.
 * Created by Mario on 18.07.2014.
 */
public interface Effect {

    /**
     * Callback interface for effect events.
     */
    interface Listener {
        /**
         * Gets called when a parameter of the effect has changed.
         * @param effect the effect whose parameter has changed
         */
        void onEffectChanged(Effect effect);
    }

    /**
     * Gets the name of the effect.
     * @return the name of the effect
     */
    String getName();

    /**
     * Initializes the effect by loading all required resources (shaders, framebuffers, textures,
     * subeffects, ...) and preparing it for usage. The resolution of the render pipeline respectively
     * the texture resolution for internal processing must be supplied.
     * @param width the texture width
     * @param height the texture height
     */
    void init(int width, int height);

    /**
     * Returns the initialization status of the effect.
     * @return true if the effect is initialized and ready to use, else false
     */
    boolean isInitialized();

    /**
     * Applies the effect to a source texture and writes it to the target framebuffer. The source
     * texture is the input image data that the effect is applied to, and the target can be an
     * intermediate framebuffer (for chaining to another effect) or the screen for direct output.
     * @param source the source texture where the input image is read from
     * @param target the target framebuffer where the result with the applied effect is written to
     */
    void apply(Texture2D source, Framebuffer target);

    /**
     * Sets a parameter handler for the parameters of this effect. The parameter handler takes
     * care that the parameter values are set on the correct thread (i.e. the GL thread).
     * Setting the handler on the effect makes sure that it is automatically set on its parameters.
     * @see Parameter#setHandler(ParameterHandler)
     * @param handler a handler to set, or null to unset
     */
    void setParameterHandler(ParameterHandler handler);

    /**
     * Adds a parameter to the effect. Parameters can be used to parameterize parameters of the effect :)
     * @see Parameter
     * @param param the parameter to add
     */
    void addParameter(Parameter param);

    /**
     * Gets a list of available parameters of the effect.
     * @return list of effect parameters
     */
    List<Parameter> getParameters();

    /**
     * Checks if this effect has any parameters.
     * @return true if there are parameters attached, else false
     */
    boolean hasParameters();

    /**
     * Sets a listener that gets called when any parameter value of the effect has changed.
     * @see Listener#onEffectChanged(Effect)
     * @param listener the listener to call back
     */
    void setListener(Listener listener);
}
