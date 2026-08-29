#version 330 core

in vec2 vTexCoord;

uniform sampler2D image;
uniform float offset;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    vec2 uv = vTexCoord;
    vec2 halfPixel = resolution / 2.0;

    vec3 sum = texture(image, uv + vec2(-halfPixel.x * 2.0, 0.0) * offset).rgb;
    sum += texture(image, uv + vec2(-halfPixel.x, halfPixel.y) * offset).rgb * 2.0;
    sum += texture(image, uv + vec2(0.0, halfPixel.y * 2.0) * offset).rgb;
    sum += texture(image, uv + vec2(halfPixel.x, halfPixel.y) * offset).rgb * 2.0;
    sum += texture(image, uv + vec2(halfPixel.x * 2.0, 0.0) * offset).rgb;
    sum += texture(image, uv + vec2(halfPixel.x, -halfPixel.y) * offset).rgb * 2.0;
    sum += texture(image, uv + vec2(0.0, -halfPixel.y * 2.0) * offset).rgb;
    sum += texture(image, uv + vec2(-halfPixel.x, -halfPixel.y) * offset).rgb * 2.0;

    fragColor = vec4(sum / 12.0, 1.0);
}
