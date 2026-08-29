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
    vec2 halfPixel = texel * 2.0 * KawaseParams.x;

    vec3 sum = texture(InSampler, uv).rgb * 4.0;
    sum += texture(InSampler, uv - halfPixel).rgb;
    sum += texture(InSampler, uv + halfPixel).rgb;
    sum += texture(InSampler, uv + vec2(halfPixel.x, -halfPixel.y)).rgb;
    sum += texture(InSampler, uv - vec2(halfPixel.x, -halfPixel.y)).rgb;

    fragColor = vec4(sum / 8.0, 1.0);
}
