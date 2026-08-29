#version 330 core

in vec2 vTextureUV;

uniform sampler2D uTextureIn;
uniform sampler2D uTextureCheck;
uniform vec2 uScreenSize;
uniform vec2 uDirection;
uniform float uSize;
uniform int uUseCheck;

out vec4 FragColor;

vec4 sampleColor(sampler2D samplerTexture, vec2 uv)
{
    vec4 color = texture(samplerTexture, clamp(uv, vec2(0.001), vec2(0.999)));
    color.rgb *= color.a;
    return color;
}

float gaussian(float x, float sigma)
{
    return exp(-(x * x) / (2.0 * sigma * sigma));
}

void main()
{
    vec2 uv = vTextureUV;

    vec2 texel = uDirection / max(uScreenSize, vec2(1.0));

    float radius = clamp(uSize, 1.0, 32.0);
    float sigma = max(radius * 0.45, 1.0);

    vec4 result = vec4(0.0);
    float totalWeight = 0.0;

    for (float i = -radius; i <= radius; i += 1.0)
    {
        float weight = gaussian(i, sigma);
        vec4 current = sampleColor(uTextureIn, uv + texel * i);

        result += current * weight;
        totalWeight += weight;
    }

    if (totalWeight > 0.0)
    {
        result /= totalWeight;
    }

    float alpha = result.a;

    if (uUseCheck == 1)
    {
        float maskAlpha = texture(uTextureCheck, uv).a;
        alpha *= 1.0 - smoothstep(0.0, 0.20, maskAlpha);
        alpha = 1.0 - exp(-alpha * 2.8);
        alpha *= 0.85;
    }

    if (alpha <= 0.003)
    {
        discard;
    }

    vec3 color = result.rgb / max(result.a, 0.001);

    FragColor = vec4(color, clamp(alpha, 0.0, 1.0));
}
