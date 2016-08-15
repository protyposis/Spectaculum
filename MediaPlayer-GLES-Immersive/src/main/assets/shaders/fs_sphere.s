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
    // parametric form of a ray: = O + tD
    vec3 O = vec3(0.0, 0.0, 0.0); // ray origin O
    vec3 D = normalize(vec3(p, 1.0)); // ray direction D

    vec3 C = vec3(0.0, 0.0, 0.0); // sphere center C
    float r = 1.0; // sphere radius r

    vec3 L = C - O;
    float tca = dot(L, D);

    if(tca < 0.0) {
        // nothing in front of ray
        return vec4(0.0, 0.0, 1.0, 1.0);
    }

    float d2 = dot(L, L) - (tca * tca);
    float r2 = r * r;

    if(d2 > r2) {
        // ray misses sphere
        return vec4(0.0, 1.0, 0.0, 1.0);
    }

    float thc = sqrt(r2 - d2);

    float t0 = tca - thc;
    float t1 = tca + thc;

    // calculate hit point of ray on sphere
    vec3 Phit = O + t0 * D;
    Phit = (rotation * vec4(Phit, 1.0)).xyz;
    vec3 Nhit = normalize(Phit - C);

    // calculate texture mapping for hit point
    vec3 sp = Phit - C; // calculate sphere point position with zero origin
    float phi = atan(sp.z, sp.x);
    float theta = acos(sp.y / r);

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