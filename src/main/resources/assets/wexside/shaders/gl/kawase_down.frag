#version 330 core

in vec2 vTexCoord;

uniform sampler2D image;
uniform float offset;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    vec2 uv = vTexCoord;
    vec2 halfPixel = resolution * 2.0;

    vec3 sum = texture(image, uv).rgb * 4.0;
    sum += texture(image, uv - halfPixel * offset).rgb;
    sum += texture(image, uv + halfPixel * offset).rgb;
    sum += texture(image, uv + vec2(halfPixel.x, -halfPixel.y) * offset).rgb;
    sum += texture(image, uv - vec2(halfPixel.x, -halfPixel.y) * offset).rgb;

    fragColor = vec4(sum / 8.0, 1.0);
}
