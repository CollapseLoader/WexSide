#version 330

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

layout(std140) uniform BlockOverlayData {
    vec4 screenData;
    vec4 effectData;
};

in vec4 vertexColor;

out vec4 fragColor;

mat2 rotate2D(float r)
{
    return mat2(cos(r), sin(r), -sin(r), cos(r));
}

float hash21(vec2 p)
{
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise21(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p)
{
    float value = 0.0;
    float amplitude = 0.5;

    for (int i = 0; i < 5; i++) {
        value += noise21(p) * amplitude;
        p = p * 2.03 + vec2(17.13, 9.37);
        amplitude *= 0.5;
    }

    return value;
}

vec3 nebulaEffect(vec2 uv, float t)
{
    vec2 n = vec2(0.0);
    vec2 q = vec2(0.0);
    vec2 p = uv * 2.5;

    float d = dot(p, p);
    float S = 16.0;
    float a = 0.0;

    mat2 m = rotate2D(15.0 + sin(d * 0.1 + t * 0.1) * 2.0);

    for (float j = 0.0; j < 6.0; j++)
    {
        p *= m * 1.05;
        n *= m;

        q = p * S + t * 2.5 + sin(t + j) * 0.0018 + 3.0 * j - 1.25 * n;
        a += dot(cos(q) / S, vec2(0.15));

        n -= sin(q);
        S *= 1.5;
    }

    vec3 effect = vec3(2.5, 1.9, 3.5) * (a + 0.182) + 9.0 * a + a;
    return effect;
}

vec3 ribbonsEffect(vec2 uv, float t)
{
    vec2 p = uv * 7.4;
    float warp = fbm(p * 0.9 + vec2(t * 0.34, t * 0.18)) * 2.0 - 1.0;
    float sweepA = sin((p.x + p.y * 0.55) * 2.2 + t * 2.8 + warp * 2.1);
    float sweepB = sin((p.x * 0.55 - p.y) * 2.8 - t * 2.1 + warp * 1.7);
    float bandA = pow(max(0.0, 1.0 - abs(sin(p.y * 4.7 + sweepA))), 5.0);
    float bandB = pow(max(0.0, 1.0 - abs(sin(p.x * 3.6 + sweepB))), 6.0);
    float soft = fbm(p * 1.8 - vec2(t * 0.25, t * 0.35));

    vec3 cyan = vec3(0.30, 1.45, 2.30);
    vec3 magenta = vec3(1.85, 0.48, 2.55);
    return cyan * (bandA * 1.05 + soft * 0.18) + magenta * (bandB * 0.86 + soft * 0.12);
}

vec3 liquidEffect(vec2 uv, float t)
{
    vec2 p = uv * 6.0;
    float flow = fbm(p + vec2(t * 0.38, -t * 0.31));
    p += vec2(
            sin(p.y * 1.8 + t * 1.35 + flow * 2.0),
            cos(p.x * 1.6 - t * 1.10 - flow * 1.7)
    ) * 0.42;

    float current = fbm(p * 2.1 + vec2(t * 0.74, t * 0.29));
    float vein = pow(max(0.0, sin((p.x + p.y) * 6.8 + current * 5.4 - t * 2.6)), 3.2);

    vec3 deep = vec3(0.18, 1.05, 1.95);
    vec3 bloom = vec3(1.50, 0.62, 2.55);
    return mix(deep, bloom, current) * (0.46 + current * 0.88) + vec3(0.40, 1.20, 1.75) * vein * 0.32;
}

vec3 auroraEffect(vec2 uv, float t)
{
    vec2 p = uv * vec2(5.4, 6.2);
    float haze = fbm(p * 0.72 + vec2(t * 0.16, -t * 0.10));
    float curtain = 0.0;
    float detail = 0.0;

    for (int i = 0; i < 3; i++)
    {
        float fi = float(i);
        float speed = 0.72 + fi * 0.28;
        float wave = sin(p.x * (1.25 + fi * 0.34) + t * speed + haze * 2.7 + fi * 1.9) * 0.42
                + sin(p.x * (3.1 + fi * 0.52) - t * (0.54 + fi * 0.18) + fi * 2.6) * 0.14;
        float band = exp(-abs(p.y * 0.72 + wave + fi * 0.34 - 0.42) * (2.9 + fi * 0.55));
        curtain += band * (0.58 - fi * 0.09);
        detail += pow(max(0.0, sin(p.x * (7.0 + fi) + haze * 5.0 + t * (1.8 + fi * 0.24))), 4.0) * band;
    }

    float veil = smoothstep(-1.0, 0.9, p.y + haze * 0.45);
    vec3 teal = vec3(0.18, 1.25, 1.95);
    vec3 violet = vec3(1.32, 0.48, 2.28);
    vec3 pearl = vec3(0.82, 1.0, 1.0);
    vec3 color = mix(teal, violet, smoothstep(0.12, 1.12, haze + detail * 0.18));
    color = mix(color, pearl, detail * 0.12);
    return color * (curtain * veil * 1.08 + haze * 0.10);
}

vec3 plasmaEffect(vec2 uv, float t)
{
    vec2 p = uv * 6.4;
    float warp = fbm(p * 0.58 + vec2(t * 0.30, -t * 0.24));
    p += vec2(
            sin(p.y * 0.95 + t * 0.82 + warp * 3.1),
            cos(p.x * 0.88 - t * 0.76 + warp * 2.4)
    ) * 0.52;

    float cell = 0.0;
    cell += sin(p.x * 1.65 + t * 1.55);
    cell += sin(p.y * 1.90 - t * 1.20);
    cell += sin((p.x + p.y) * 1.12 + warp * 4.0);
    cell = 0.5 + 0.5 * sin(cell);

    float veins = pow(max(0.0, sin(length(p) * 5.0 - t * 2.9 + warp * 5.8)), 4.2);
    float core = smoothstep(0.48, 1.0, cell);
    vec3 blue = vec3(0.16, 0.85, 2.20);
    vec3 pink = vec3(1.82, 0.42, 2.38);
    vec3 gold = vec3(2.10, 1.32, 0.36);
    vec3 color = mix(blue, pink, core);
    color = mix(color, gold, veins * 0.18);
    return color * (0.34 + core * 0.88 + veins * 0.24);
}

vec3 modeEffect(vec2 uv, float t, float mode)
{
    if (mode < 0.5) {
        return nebulaEffect(uv, t);
    }
    if (mode < 1.5) {
        return ribbonsEffect(uv, t);
    }
    if (mode < 2.5) {
        return liquidEffect(uv, t);
    }
    if (mode < 3.5) {
        return auroraEffect(uv, t);
    }

    return plasmaEffect(uv, t);
}

vec3 getEffectColor(vec2 frag, vec3 tint)
{
    vec2 screen = max(screenData.xy, vec2(1.0));
    vec2 uv = 0.33 * (frag - 0.5 * screen) / screen.y;

    float t = effectData.x;
    float mode = floor(effectData.y + 0.5);
    vec3 baseTint = clamp(tint, 0.0, 1.0);
    if (mode < 0.5) {
        return baseTint;
    }

    vec3 effect = modeEffect(uv, t, mode - 1.0);
    vec3 tintLift = baseTint * 0.86 + vec3(0.11, 0.15, 0.19);

    return clamp(effect * tintLift + baseTint * 0.05, 0.0, 1.0);
}

void main()
{
    vec4 color = vertexColor * ColorModulator;
    if (color.a <= 0.0) {
        discard;
    }

    fragColor = vec4(getEffectColor(gl_FragCoord.xy, color.rgb), color.a);
}
