#version 330 core

in vec2 vTextureUV;

uniform sampler2D BeforeSampler;
uniform sampler2D AfterSampler;
uniform sampler2D DepthBeforeSampler;
uniform sampler2D DepthAfterSampler;

out vec4 FragColor;

void main() {
    float depthBefore = texture(DepthBeforeSampler, vTextureUV).r;
    float depthAfter = texture(DepthAfterSampler, vTextureUV).r;

    float depthDiff = depthBefore - depthAfter;
    bool isHand = depthDiff > 0.0001;

    if (!isHand) {
        vec4 before = texture(BeforeSampler, vTextureUV);
        vec4 after = texture(AfterSampler, vTextureUV);
        vec3 colorDiff = abs(after.rgb - before.rgb);
        float maxColorDiff = max(max(colorDiff.r, colorDiff.g), colorDiff.b);
        isHand = maxColorDiff > 0.01;
    }

    float result = isHand ? 1.0 : 0.0;
    FragColor = vec4(result, result, result, result);
}
