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

 precision highp float;

varying vec2 v_TextureCoord;
uniform sampler2D s_Texture;
uniform vec2 u_TextureSize;
uniform sampler2D watermark;
uniform vec2 size;
uniform float scale;
uniform float opacity;
uniform vec2 margin;

void main() {
    vec2 watermarkAdjustmentScale = vec2(1.0) / u_TextureSize * size; // scale watermark to correct aspect ratio and pixel mapping size
    vec2 watermarkCoord = v_TextureCoord / watermarkAdjustmentScale / scale;

    // add margin
    watermarkCoord = watermarkCoord - margin;

    // get pixel values
    vec4 p_image = texture2D(s_Texture, v_TextureCoord);
    vec4 p_watermark = texture2D(watermark, watermarkCoord);

    // calculate alpha and inverse alpha for source addition
    float a = p_watermark.w * opacity; // alpha is the fourth component of the watermark, scale by opacity parameter
    float ia = 1.0 - a;

    gl_FragColor = vec4(p_image.xyz * ia + p_watermark.xyz * a, 1.0); // combine sources together
}
