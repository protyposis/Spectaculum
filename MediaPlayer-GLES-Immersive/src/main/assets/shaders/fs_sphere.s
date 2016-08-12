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
    vec2 uv = v_TextureCoord;

    // Scale texture space from (0,1) to (-1,1) in both axes
    uv = uv * 2.0 - 1.0;

    float d = uv.x * uv.x + uv.y * uv.y;

    if (d > 1.0)
        discard;

    float z = sqrt(1.0 - d);

    // To view back side of sphere, negate z
    vec4 point = vec4(uv.xy, z, 1.0);

    float x = (atan(point.x, point.z) + PI) / TWOPI + rot_x,
          y = (asin(point.y) + PIP2) / PI + rot_y;

    vec4 texel = texture2D(s_Texture, vec2(x, y));

    gl_FragColor = texel;
}