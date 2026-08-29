#version 330 core

layout(std140) uniform KawaseData {
    vec4 KawaseParams;
};

in vec2 vTexCoord;
uniform sampler2D InSampler;

out vec4 fragColor;

void main() {
    vec2 texel = 1.0 / vec2(textureSize(InSampler, 0));
    vec2 uv = vTexCoord;
    vec2 halfPixel = texel * 0.5 * KawaseParams.x;

    vec3 sum = texture(InSampler, uv + vec2(-halfPixel.x * 2.0, 0.0)).rgb;
    sum += texture(InSampler, uv + vec2(-halfPixel.x, halfPixel.y)).rgb * 2.0;
    sum += texture(InSampler, uv + vec2(0.0, halfPixel.y * 2.0)).rgb;
    sum += texture(InSampler, uv + vec2(halfPixel.x, halfPixel.y)).rgb * 2.0;
    sum += texture(InSampler, uv + vec2(halfPixel.x * 2.0, 0.0)).rgb;
    sum += texture(InSampler, uv + vec2(halfPixel.x, -halfPixel.y)).rgb * 2.0;
    sum += texture(InSampler, uv + vec2(0.0, -halfPixel.y * 2.0)).rgb;
    sum += texture(InSampler, uv + vec2(-halfPixel.x, -halfPixel.y)).rgb * 2.0;

    fragColor = vec4(sum / 12.0, 1.0);
}
