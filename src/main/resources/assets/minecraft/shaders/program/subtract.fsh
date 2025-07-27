#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ToSubtract;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    fragColor = diffuseTex - vec4(texture(ToSubtract, texCoord).rgb, 0.0);
}