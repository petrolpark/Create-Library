#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ToMix;

uniform mat4x4 Points; //16 [ 0.0, 0.0, 0.0, 1.0, 0.1, 1.0, 0.5, 1.0, 0.5, 1.0, 0.9, 1.0, 1.0, 1.0, 1.0, 0.0 ]

uniform float EffectFactor;

in vec2 texCoord;

out vec4 fragColor;

vec2 bezier(vec2 p1, vec2 p2, vec2 p3, vec2 p4, float dt) {
    vec2 a1 = mix(p1, p2, dt);
    vec2 a2 = mix(p2, p3, dt);
    vec2 a3 = mix(p3, p4, dt);

    vec2 b1 = mix(a1, a2, dt);
    vec2 b2 = mix(a2, a3, dt);

    return mix(b1, b2, dt);
}

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    vec4 toMixTex = texture(ToMix, texCoord);

    vec2 point;

    if (EffectFactor <= 0.5) {
        point = bezier(
            vec2(Points[0][0], Points[0][1]),
            vec2(Points[0][2], Points[0][3]),
            vec2(Points[1][0], Points[1][1]),
            vec2(Points[1][2], Points[1][3]),
            EffectFactor * 2.0
        );
    } else {
        point = bezier(
            vec2(Points[2][0], Points[2][1]),
            vec2(Points[2][2], Points[2][3]),
            vec2(Points[3][0], Points[3][1]),
            vec2(Points[3][2], Points[3][3]),
            (EffectFactor - 0.5) * 2.0
        );
    }

    fragColor = mix(diffuseTex, toMixTex, point.y);
}