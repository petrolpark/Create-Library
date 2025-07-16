#version 150

#define pi 3.14

uniform sampler2D DiffuseSampler;
uniform sampler2D ToMix;

uniform float EffectFactor;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    fragColor = vec4(mix(diffuseTex.rgb, texture(ToMix, texCoord).rgb, 1-(1-cos((EffectFactor)*2*pi))/2), diffuseTex.a);
}