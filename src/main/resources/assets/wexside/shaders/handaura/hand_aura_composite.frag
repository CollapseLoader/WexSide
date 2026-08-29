#version 150

in vec2 texCoord;
in vec2 texelSize;

out vec4 fragColor;

uniform sampler2D SceneBeforeSampler;
uniform sampler2D SceneAfterSampler;
uniform sampler2D MaskSampler;
uniform sampler2D GlowSampler;

layout(std140) uniform HandAuraData {
    vec4 meta;
    vec4 auraColor;
    vec4 auraColor2;
    vec4 motion;
};

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise21(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;

    for (int i = 0; i < 5; i++) {
        value += noise21(p) * amplitude;
        p = p * 2.03 + vec2(17.1, 9.2);
        amplitude *= 0.5;
    }

    return value;
}

float edgeMask(vec2 uv) {
    vec2 clampedUv = clamp(uv, texelSize * 0.5, vec2(1.0) - texelSize * 0.5);
    float center = texture(MaskSampler, clampedUv).r;
    float edge = 0.0;

    edge += abs(center - texture(MaskSampler, clamp(clampedUv + vec2(texelSize.x, 0.0), texelSize * 0.5, vec2(1.0) - texelSize * 0.5)).r);
    edge += abs(center - texture(MaskSampler, clamp(clampedUv - vec2(texelSize.x, 0.0), texelSize * 0.5, vec2(1.0) - texelSize * 0.5)).r);
    edge += abs(center - texture(MaskSampler, clamp(clampedUv + vec2(0.0, texelSize.y), texelSize * 0.5, vec2(1.0) - texelSize * 0.5)).r);
    edge += abs(center - texture(MaskSampler, clamp(clampedUv - vec2(0.0, texelSize.y), texelSize * 0.5, vec2(1.0) - texelSize * 0.5)).r);

    return clamp(edge * 2.2, 0.0, 1.0);
}

float sampleGlow(vec2 uv) {
    return texture(GlowSampler, clamp(uv, texelSize * 0.5, vec2(1.0) - texelSize * 0.5)).r;
}

float upwardTrail(vec2 uv) {
    float trail = 0.0;
    float weight = 0.0;

    for (int i = 1; i <= 10; i++) {
        float fi = float(i);
        float w = exp(-fi * 0.36);
        float sampleA = sampleGlow(uv - vec2(0.0, texelSize.y * fi * 7.4));
        float sampleB = sampleGlow(uv - vec2(texelSize.x * fi * 0.8, texelSize.y * fi * 5.3));
        float sampleC = sampleGlow(uv + vec2(texelSize.x * fi * 0.8, -texelSize.y * fi * 5.3));
        trail += max(sampleA, max(sampleB, sampleC)) * w;
        weight += w;
    }

    return weight > 0.0 ? trail / weight : 0.0;
}

float flowField(vec2 uv, float glowMask, float time) {
    float rise = uv.y * 4.8 - time * 1.45;
    vec2 p = vec2(uv.x * (8.0 + glowMask * 4.0), rise);
    float swirl = fbm(p + vec2(fbm(p * 0.62 + time * 0.31), 0.0));
    float ribbon = max(0.0, sin(uv.y * 26.0 - time * 6.2 + swirl * 5.0 + uv.x * 6.0));
    float veins = max(0.0, sin(uv.x * 42.0 + time * 1.6 + swirl * 6.5));
    float veil = smoothstep(0.28, 0.9, swirl * 0.9 + ribbon * 0.55);
    return clamp(veil + veins * 0.16, 0.0, 1.0);
}

vec4 activeAuraColor(vec2 uv) {
    float gradientT = mod(motion.w, 2.0) > 0.5 ? smoothstep(0.0, 1.0, uv.y) : 0.0;
    return mix(auraColor, auraColor2, gradientT);
}

vec3 auraTint(vec4 color, float flow, float edge) {
    vec3 base = clamp(color.rgb * 0.88, 0.0, 1.0);
    vec3 highlight = clamp(color.rgb * 1.16, 0.0, 1.0);
    float accent = clamp(0.12 + flow * 0.16 + edge * 0.08, 0.0, 0.34);
    return mix(base, highlight, accent);
}

float ribbonPattern(vec2 uv, float time, float motionStrength) {
    vec2 p = uv + motion.xy * motionStrength * vec2(0.10, 0.06);
    float warp = fbm(vec2(p.x * 5.5 - time * 0.28, p.y * 4.5 + time * 0.18));
    float bandA = pow(max(0.0, sin((p.x + p.y * 0.42) * 18.0 - time * 5.0 + warp * 5.8)), 4.0);
    float bandB = pow(max(0.0, sin((p.x * 0.58 - p.y) * 22.0 + time * 4.2 + warp * 4.6)), 5.0);
    float weave = smoothstep(0.30, 0.92, warp + bandA * 0.26);
    return clamp((bandA * 0.64 + bandB * 0.48 + weave * 0.24), 0.0, 1.0);
}

void main() {
    vec4 sceneBefore = texture(SceneBeforeSampler, texCoord);
    vec4 sceneAfter = texture(SceneAfterSampler, texCoord);
    float mask = texture(MaskSampler, texCoord).r;
    float blurMask = sampleGlow(texCoord) * 0.72;
    float trailMask = upwardTrail(texCoord) * 0.86;
    float liftedMask = max(blurMask, trailMask * 1.12);
    float handCover = smoothstep(0.025, 0.38, mask);
    float fillHands = motion.w >= 2.0 ? 1.0 : 0.0;
    float handEffectCover = mix(handCover, 0.0, fillHands);
    float handOverlay = handCover * fillHands;
    int effectMode = int(meta.w + 0.5);
    vec2 motionDir = motion.xy;
    float motionStrength = motion.z;
    vec2 motionOffset = motionDir * motionStrength * vec2(0.16, 0.10);
    vec2 driftUv = clamp(texCoord + motionOffset, texelSize * 0.5, vec2(1.0) - texelSize * 0.5);
    float softFieldNoise = fbm(vec2(driftUv.x * 6.2 + meta.z * 0.05, driftUv.y * 5.8 - meta.z * 0.04));
    vec4 activeColor = activeAuraColor(texCoord);

    if (liftedMask < 0.004 && mask < 0.004) {
        fragColor = sceneAfter;
        return;
    }

    float edge = edgeMask(texCoord);
    float outerField = clamp(blurMask * 0.86 + trailMask * 0.52 + softFieldNoise * 0.024, 0.0, 1.0);
    float outer = smoothstep(0.07, 0.64, outerField);
    outer *= 1.0 - handEffectCover * 0.975;
    outer = pow(clamp(outer, 0.0, 1.0), 1.16);
    float verticalFade = 0.58 + smoothstep(0.10, 0.60, trailMask) * 0.46;

    float flow = flowField(driftUv, max(trailMask, max(liftedMask * 0.82, mask * 0.65)), meta.z);
    float mist = fbm(vec2(driftUv.x * 5.7 - meta.z * 0.18, driftUv.y * 2.8 - meta.z * 0.32));
    float streaks = pow(max(0.0, sin(driftUv.y * 21.0 - meta.z * 7.1 + mist * 4.2 + driftUv.x * 5.5)), 4.0);
    float motionLines = pow(max(0.0, sin(driftUv.x * 54.0 + driftUv.y * 18.0 - meta.z * 5.8 + mist * 5.9)), 5.0) * motionStrength;
    float fade = smoothstep(0.10, 0.40, outerField);

    float veil = outer * (0.22 + flow * 0.58 + mist * 0.10) * verticalFade;
    float trail = outer * streaks * 0.20 * smoothstep(0.12, 0.70, trailMask);
    trail += outer * motionLines * 0.11;
    float rim = edge * outer * (0.05 + flow * 0.11);
    float aura = clamp((veil + trail + rim) * fade, 0.0, 1.0) * activeColor.a * 0.84;

    vec3 tint = auraTint(activeColor, flow, edge);

    if (effectMode == 1) {
        float glowAura = clamp((outer * (0.38 + trailMask * 0.36) + rim * 0.42) * activeColor.a * 0.96, 0.0, 1.0);
        vec3 glowColor = sceneAfter.rgb + tint * glowAura * (0.92 - handOverlay * 0.44);
        glowColor += tint * edge * outer * (0.08 - handOverlay * 0.03);
        glowColor += vec3(1.0) * outer * (0.03 - handOverlay * 0.012);
        fragColor = vec4(clamp(glowColor, 0.0, 1.0), sceneAfter.a);
        return;
    }

    if (effectMode == 2) {
        float ribbons = ribbonPattern(driftUv, meta.z, motionStrength);
        float ribbonAura = clamp((veil * 0.30 + ribbons * outer * 1.06 + rim * 0.28) * activeColor.a, 0.0, 1.0);
        vec3 ribbonColor = sceneAfter.rgb + tint * ribbonAura * (0.72 - handOverlay * 0.34);
        ribbonColor += mix(tint, vec3(1.0), 0.22) * ribbons * outer * activeColor.a * (0.16 - handEffectCover * 0.10 - handOverlay * 0.05);
        ribbonColor += tint * edge * outer * (0.055 - handOverlay * 0.020);
        fragColor = vec4(clamp(ribbonColor, 0.0, 1.0), sceneAfter.a);
        return;
    }

    vec3 behindHand = sceneBefore.rgb + tint * aura * 1.08;
    behindHand += tint * edge * outer * 0.035;
    behindHand += tint * motionLines * 0.05 * outer;
    vec3 finalColor = mix(behindHand, sceneAfter.rgb, handCover);
    finalColor += (tint * aura * 0.38 + tint * motionLines * outer * 0.035 + tint * edge * outer * 0.018) * handOverlay;

    fragColor = vec4(clamp(finalColor, 0.0, 1.0), sceneAfter.a);
}
