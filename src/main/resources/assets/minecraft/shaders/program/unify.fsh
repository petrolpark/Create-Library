#version 150

uniform vec3 Color;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = vec4(Color, 1.0);
}