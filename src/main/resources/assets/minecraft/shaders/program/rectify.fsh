#version 150

uniform sampler2D DiffuseSampler;
uniform vec3 Color;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 diffuseTex = texture(DiffuseSampler, texCoord);
    float difference = dot(Color, diffuseTex.rgb);

    fragColor = vec4(vec3(difference), 1.0);
}