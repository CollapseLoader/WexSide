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
        float distanceFromCenter = length(delta.xz);
        if (distanceFromCenter > radius + 0.3 || abs(delta.y) > 2.0) {
            continue;
        }

        float antialiasing = clamp(fwidth(distanceFromCenter), 0.001, MAX_AA);
        float fill = 1.0 - smoothstep(radius - antialiasing, radius, distanceFromCenter);
        float outline = 1.0 - smoothstep(
                0.5 * antialiasing,
                1.5 * antialiasing,
                abs(distanceFromCenter - radius)
        );
        float alpha = max(fillAlpha * fill, outlineAlpha * outline);
        if (alpha > best.a) {
            best = vec4(uColors[i].rgb, alpha);
        }
    }

    if (best.a <= 0.002) {
        discard;
    }
    fragColor = best;
}
