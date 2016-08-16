// Immersive 360 degree image shader
// Maps a equirectangular/spherical texture to a sphere
//
// This is basically a simple ray tracer modeled after the following guide:
// http://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection

precision highp float;

uniform sampler2D s_Texture;
uniform vec2 u_TextureSize;
uniform mat4 rotation;
varying vec2 v_TextureCoord;

#define PIP2    1.5707963 // PI/2
#define PI      3.1415629
#define TWOPI   6.2831853 // 2PI

vec4 trace(in vec2 p)
{
    vec3 D = normalize(vec3(p, 1.0)); // ray direction D

    // calculate hit point of ray on sphere
    vec3 sp = (rotation * vec4(-D, 1.0)).xyz;

    // calculate texture mapping for hit point
    float phi = atan(sp.z, sp.x);
    float theta = acos(sp.y);

    // Spherical mapping from sphere to texture
    float u = 0.5 - (phi + PI) / TWOPI + 0.25;
    float v = (theta + PIP2) / PI - 0.5;

    // Texture mapping coordinates
    vec2 uv = vec2(u, v);

    // Simulate texture wrap mode GL_REPEAT
    uv = mod(uv, 1.0);

    return texture2D(s_Texture, uv);
}

void main (void)
{
    // Scale texture space to (-1,1) in both axes
    vec2 p = -1.0 + 2.0 * v_TextureCoord;

    // Scale to aspect ratio
    p.x *= u_TextureSize.x / u_TextureSize.y;

    // Paint texel
    gl_FragColor = trace(p);
}