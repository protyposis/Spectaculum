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

package net.protyposis.android.spectaculum;

/**
 * The resolution in which the OpenGL pipeline will do the processing.
 */
public enum PipelineResolution {
    /**
     * The resolution of the view on screen. Source data will be resized to screen size and then processed.
     *
     * The resolution of the view has a tendency to be lower than the source data, except the
     * source data is of really low quality. Therefore, processing in view resolution sets an
     * upper bound to the required processing power, e.g. rendering a 4K video on a 720p screen
     * saves lots of pixel processing, while blowing up a 480p to 720p will not waste lots of
     * power because this requires only low processing capacity.
     *
     * There are cases when it makes more sense to process in source resolution, e.g. when the picture
     * gets zoomed in or needs to be captured. Zooming a 4K picture processed in 720p resolution
     * will get blurry, while there is a lot of zooming "headroom" left when processing is done in 4K.
     */
    VIEW,

    /**
     * Default mode.
     * The resolution of the source data, i.e. an image file, camera or video stream.
     * See {@link #VIEW} for an explanation on when this makes sense.
     */
    SOURCE
}
