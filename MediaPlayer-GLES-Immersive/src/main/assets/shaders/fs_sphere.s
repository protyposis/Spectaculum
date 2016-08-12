// Sphere shader inspired by http://z0b.kapsi.fi/sphere.php

precision highp float;

uniform sampler2D s_Texture;
uniform vec2 u_TextureSize;
uniform float rot_x;
uniform float rot_y;
varying vec2 v_TextureCoord;

#define PIP2    1.5707963       // PI/2
#define PI      3.1415629
#define TWOPI   6.2831853       // 2PI

void main (void)
{
    vec2 xy = v_TextureCoord;

    // Scale texture space from (0,1) to (-1,1) in both axes
    xy = xy * 2.0 - 1.0;

    float d = xy.x * xy.x + xy.y * xy.y;

    if (d > 1.0)
        discard;

    float z = sqrt(1.0 - d);

    // To view back side of sphere, negate z
    vec4 point = vec4(xy.xy, z, 1.0);

    // Spherical mapping from sphere to texture
    float u = (atan(point.x, point.z) + PI) / TWOPI + rot_x,
          v = (asin(point.y) + PIP2) / PI + rot_y;

    // Texture mapping coordinates
    vec2 uv = vec2(u, v);

    vec4 texel = texture2D(s_Texture, uv);

    gl_FragColor = texel;
}