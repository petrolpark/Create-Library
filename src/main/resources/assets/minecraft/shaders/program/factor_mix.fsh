#version 150

#define pi 3.14

uniform sampler2D DiffuseSampler;
uniform sampler2D ToMix;
uniform sampler2D Factor;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    vec4 toMixTex = texture(ToMix, texCoord);
    vec4 factorTex = texture(Factor, vec2(0.0));
    float factor = factorTex.r;
    fragColor = vec4(mix(diffuseTex.rgb, toMixTex.rgb, factor), diffuseTex.a);
}