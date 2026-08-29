#version 330 core

in vec2 vTexCoord;

uniform sampler2D DepthSampler;

#define MAX_POTIONS 8

#define MAX_AA 0.15

layout(std140) uniform PotionDecal {
    mat4 uInvViewProj;
    vec4 uParams;
    vec4 uPotions[MAX_POTIONS];
    vec4 uColors[MAX_POTIONS];
};

out vec4 fragColor;

vec3 reconstruct(vec2 uv, float depth) {
    vec4 ndc = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 world = uInvViewProj * ndc;
    return world.xyz / world.w;
}

void main() {
    float depth = texture(DepthSampler, vTexCoord).r;

    vec3 world = reconstruct(vTexCoord, depth);

    if (depth >= 1.0) {
        discard;
    }

    int count = int(uParams.x + 0.5);
    float fillAlpha = uParams.y;
    float outlineAlpha = uParams.z;

    vec4 best = vec4(0.0);
    for (int i = 0; i < MAX_POTIONS; i++) {
        if (i >= count) {
            break;
        }
        vec3 center = uPotions[i].xyz;
        float radius = uPotions[i].w;
        vec3 delta = world - center;
        float dist = length(delta.xz);
        if (dist > radius + 0.3 || abs(delta.y) > 2.0) {
            continue;
        }
        float aa = clamp(fwidth(dist), 0.001, MAX_AA);
        vec3 rgb = uColors[i].rgb;

        float fill = 1.0 - smoothstep(radius - aa, radius, dist);
        float outline = 1.0 - smoothstep(0.5 * aa, 1.5 * aa, abs(dist - radius));
        float a = max(fillAlpha * fill, outlineAlpha * outline);
        if (a > best.a) {
            best = vec4(rgb, a);
        }
    }
    if (best.a <= 0.002) {
        discard;
    }
    fragColor = best;
}
