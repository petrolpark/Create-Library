#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ToMultiply;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    fragColor = diffuseTex * vec4(texture(ToMultiply, texCoord).xyz, 1.0);
}