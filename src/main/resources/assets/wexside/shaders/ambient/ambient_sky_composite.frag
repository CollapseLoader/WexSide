#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D SkySampler;

void main() {
    fragColor = texture(SkySampler, texCoord);
}
