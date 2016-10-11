precision mediump float;

uniform sampler2D s_Texture;
uniform vec2 u_TextureSize;
uniform vec4 color;
uniform float opacity;
uniform int distance;
varying vec2 v_TextureCoord;

void main() {
    vec4 black = vec4(0.0, 0.0, 0.0, 1.0);
    vec4 color = texture2D(s_Texture, v_TextureCoord);
    float scanline = step(mod(v_TextureCoord.y * u_TextureSize.y, float(distance)), 0.5) * opacity;

    gl_FragColor =  color * (1.0 - scanline) + black * scanline;
}