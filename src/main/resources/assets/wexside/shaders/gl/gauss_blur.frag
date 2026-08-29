#version 330 core

in vec2 vTexCoord;

uniform sampler2D image;
uniform vec2 direction;
uniform vec2 texelSize;
uniform float sigmaStart;
uniform float sigmaEnd;
uniform vec2 sigmaRange;
uniform int radius;

out vec4 fragColor;

void main() {
    float span = sigmaRange.x - sigmaRange.y;
    float progress = span <= 0.0001 ? 1.0 : clamp((sigmaRange.x - vTexCoord.y) / span, 0.0, 1.0);
    float sigma = mix(sigmaStart, sigmaEnd, progress);

    if (sigma <= 0.0001) {
        fragColor = texture(image, vTexCoord);
        return;
    }

    float twoSigmaSq = 2.0 * sigma * sigma;
    float invDenom = 1.0 / (3.14159265 * twoSigmaSq);
    float reach = ceil(3.0 * sigma);

    vec4 acc = texture(image, vTexCoord) * invDenom;
    float weightSum = invDenom;

    for (int i = 1; i <= radius; i++) {
        if (float(i) > reach) {
            break;
        }
        float w = exp(-float(i * i) / twoSigmaSq) * invDenom;
        vec2 off = direction * texelSize * float(i);
        acc += texture(image, vTexCoord + off) * w;
        acc += texture(image, vTexCoord - off) * w;
        weightSum += w * 2.0;
    }

    fragColor = acc / weightSum;
}
