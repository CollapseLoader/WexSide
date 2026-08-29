#version 330 core

in vec2 vTexCoord;
uniform sampler2D OverlaySampler;

out vec4 fragColor;

void main() {
    vec4 color = texture(OverlaySampler, vTexCoord);
    if (color.a > 0.0) {
        color.rgb /= color.a;
    }
    fragColor = color;
}
