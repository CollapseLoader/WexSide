#version 150

in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D DepthSampler;

layout(std140) uniform AmbientSkyData {
    vec4 meta;
    vec4 skyColor;
    vec4 skyColor2;
    vec4 params;
    vec4 cameraRight;
    vec4 cameraUp;
    vec4 cameraForward;
};

bool skyHidden(vec2 uv) {
    vec2 margin = vec2(1.5 / max(meta.x, 1.0), 1.5 / max(meta.y, 1.0));
    float far = texture(DepthSampler, uv + vec2(-margin.x, -margin.y)).r;
    far = max(far, texture(DepthSampler, uv + vec2(margin.x, -margin.y)).r);
    far = max(far, texture(DepthSampler, uv + vec2(-margin.x, margin.y)).r);
    far = max(far, texture(DepthSampler, uv + vec2(margin.x, margin.y)).r);
    return far < 1.0;
}

vec3 skyDirection(vec2 uv) {
    vec2 ndc = uv * 2.0 - 1.0;
    float tanHalfFov = max(params.z, 0.01);
    ndc.x *= (meta.x / max(meta.y, 1.0)) * tanHalfFov;
    ndc.y *= tanHalfFov;
    return normalize(cameraForward.xyz + cameraRight.xyz * ndc.x + cameraUp.xyz * ndc.y);
}

vec3 tintColor(vec2 uv) {
    float gradientT = params.x > 0.5 ? smoothstep(0.0, 1.0, uv.y) : 0.0;
    return mix(skyColor.rgb, skyColor2.rgb, gradientT);
}

float verticalFade(vec2 uv) {
    float lower = smoothstep(0.02, 0.34, uv.y);
    float upper = 1.0 - smoothstep(0.97, 1.0, uv.y) * 0.12;
    return lower * upper;
}

mat2 rotate2(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float valueNoise2(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);

    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));

    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm2(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;

    for (int i = 0; i < 5; i++) {
        value += valueNoise2(p) * amplitude;
        p = rotate2(0.58) * p * 2.02 + vec2(17.13, 8.47);
        amplitude *= 0.5;
    }

    return value;
}

vec3 waterEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 8.0 - vec2(20.0);
    vec2 i = p;
    float c = 1.0;
    float inten = 0.05;

    for (int n = 0; n < 10; n++) {
        float t = time * (1.0 - (3.0 / float(n + 1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        vec2 wave = max(abs(vec2(sin(i.x + t), cos(i.y + t))), vec2(0.015)) / inten;
        c += 1.0 / max(length(vec2(p.x / wave.x, p.y / wave.y)), 0.001);
    }

    c /= 10.0;
    c = 1.5 - sqrt(max(c, 0.0));
    float foam = clamp(c * c * c * c, 0.0, 1.0);
    float body = clamp(0.22 + foam * 0.82, 0.0, 1.0);
    vec3 base = vec3(0.0, 0.3, 0.5) + vec3(foam);
    vec3 color = base * mix(vec3(0.85, 0.96, 1.08), tint, 0.28);
    alphaEnergy = body;
    return clamp(color, 0.0, 1.0);
}

vec3 silkEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 6.0;
    float flow = fbm2(p * 0.42 + vec2(time * 0.05, -time * 0.04));
    p += vec2(flow * 1.7, fbm2(p * 0.58 - time * 0.035) * 1.25);
    float ribbonA = pow(max(0.0, sin((p.x + p.y * 0.45) * 5.4 + time * 0.52 + flow * 3.2)), 3.2);
    float ribbonB = pow(max(0.0, sin((p.x * 0.38 - p.y) * 7.1 - time * 0.42 + flow * 2.8)), 4.5);
    float haze = smoothstep(0.18, 0.92, fbm2(p * 0.72 + flow));
    alphaEnergy = clamp(haze * 0.38 + ribbonA * 0.52 + ribbonB * 0.32, 0.0, 1.0);
    vec3 pearl = vec3(0.62, 0.86, 1.15);
    return clamp((tint * 0.58 + pearl * 0.42) * (0.34 + alphaEnergy * 1.28), 0.0, 1.0);
}

vec3 auroraEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * vec2(5.2, 7.2);
    float drift = fbm2(p * 0.38 + vec2(time * 0.04, -time * 0.03));
    float curtain = 0.0;

    for (int i = 0; i < 4; i++) {
        float fi = float(i);
        float wave = sin(p.x * (1.2 + fi * 0.34) + time * (0.34 + fi * 0.07) + drift * 4.0 + fi * 1.7);
        float band = exp(-abs(p.y * 0.72 + wave * 0.54 + fi * 0.26 - 0.15) * (2.4 + fi * 0.55));
        curtain += band * (0.58 - fi * 0.08);
    }

    float shimmer = pow(max(0.0, sin(p.x * 9.0 + drift * 7.0 + time * 1.3)), 4.0);
    alphaEnergy = clamp(curtain * (0.78 + shimmer * 0.25), 0.0, 1.0);
    vec3 teal = vec3(0.30, 1.20, 1.36);
    vec3 violet = vec3(0.74, 0.52, 1.35);
    return clamp(mix(teal, violet, smoothstep(0.2, 1.0, drift + shimmer * 0.22)) * (0.20 + alphaEnergy * 1.35) * mix(vec3(1.0), tint, 0.35), 0.0, 1.0);
}

vec3 nebulaEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 4.4;
    p = rotate2(0.34) * p;
    float n1 = fbm2(p * 0.76 + vec2(time * 0.025, -time * 0.018));
    float n2 = fbm2(p * 1.38 - vec2(time * 0.018, time * 0.031) + n1 * 1.4);
    float smoke = smoothstep(0.28, 0.92, n1 * 0.78 + n2 * 0.56);
    float veins = pow(max(0.0, sin((p.x + p.y * 0.52 + n2 * 2.5) * 3.8 + time * 0.28)), 3.0);
    alphaEnergy = clamp(smoke * 0.62 + veins * smoke * 0.36, 0.0, 1.0);
    vec3 deep = vec3(0.18, 0.24, 0.46);
    vec3 bloom = vec3(0.72, 0.86, 1.24);
    vec3 color = mix(deep, bloom, smoke) + tint * veins * 0.28;
    return clamp(color * (0.34 + alphaEnergy * 0.95), 0.0, 1.0);
}

vec3 milkyVeilEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 3.7;
    p = rotate2(-0.22) * p;
    float base = fbm2(p * 0.58 + vec2(time * 0.012, -time * 0.018));
    float detail = fbm2(p * 1.42 + base * 2.4 - vec2(time * 0.026, time * 0.015));
    float folds = pow(max(0.0, sin((p.x * 0.72 + p.y * 0.45 + detail * 3.3) * 4.0 + time * 0.20)), 2.2);
    float smoke = smoothstep(0.20, 0.86, base * 0.68 + detail * 0.74);
    float softVoid = smoothstep(0.18, 0.62, fbm2(p * 0.34 - base));
    alphaEnergy = clamp((smoke * 0.78 + folds * 0.28) * (0.58 + softVoid * 0.42), 0.0, 1.0);
    vec3 ash = vec3(0.34, 0.38, 0.52);
    vec3 milk = vec3(0.86, 0.89, 1.02);
    vec3 color = mix(ash, milk, smoke) + vec3(0.28, 0.32, 0.42) * folds;
    return clamp(mix(color, tint * 1.05, 0.12) * (0.40 + alphaEnergy * 0.92), 0.0, 1.0);
}

vec3 plasmaEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 6.2;
    float warp = fbm2(p * 0.42 + vec2(time * 0.06, -time * 0.05));
    p += vec2(sin(p.y * 0.9 + time * 0.42), cos(p.x * 0.8 - time * 0.37)) * (0.65 + warp * 0.55);
    float core = 0.5 + 0.5 * sin(p.x * 1.4 + sin(p.y * 1.6 + time * 0.7) + warp * 4.6);
    float veins = pow(max(0.0, sin(length(p) * 4.0 - time * 0.85 + warp * 5.0)), 4.0);
    alphaEnergy = clamp(core * 0.45 + veins * 0.58, 0.0, 1.0);
    vec3 cyan = vec3(0.24, 1.05, 1.42);
    vec3 blue = vec3(0.22, 0.42, 1.25);
    return clamp(mix(blue, cyan, core) * (0.30 + alphaEnergy * 1.18) * mix(vec3(1.0), tint, 0.42), 0.0, 1.0);
}

vec3 frostEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 8.0;
    float n = fbm2(p * 0.36 + time * 0.015);
    float shardA = pow(max(0.0, 1.0 - abs(sin((p.x + p.y * 0.38 + n * 2.2) * 2.8))), 5.0);
    float shardB = pow(max(0.0, 1.0 - abs(sin((p.x * 0.44 - p.y + n * 2.8) * 3.6))), 6.0);
    float mist = smoothstep(0.34, 0.86, fbm2(p * 0.22 - vec2(time * 0.01, time * 0.02)));
    alphaEnergy = clamp(mist * 0.34 + shardA * 0.62 + shardB * 0.46, 0.0, 1.0);
    vec3 ice = vec3(0.68, 0.96, 1.18);
    vec3 pale = vec3(0.92, 0.98, 1.08);
    return clamp(mix(ice, pale, shardA + shardB) * (0.25 + alphaEnergy * 1.05) * mix(vec3(1.0), tint, 0.25), 0.0, 1.0);
}

vec3 emberEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * 6.0 + vec2(0.0, -time * 0.12);
    float smoke = fbm2(p * 0.46);
    float sparks = pow(max(0.0, sin((p.x * 1.7 + p.y * 0.8 + smoke * 3.0) * 5.0)), 8.0);
    float haze = smoothstep(0.28, 0.86, smoke);
    alphaEnergy = clamp(haze * 0.38 + sparks * 0.64, 0.0, 1.0);
    vec3 shadow = tint * vec3(0.34, 0.40, 0.50);
    vec3 line = mix(vec3(0.62, 0.86, 1.15), tint, 0.46);
    return clamp(mix(shadow, line, haze + sparks) * (0.40 + alphaEnergy * 1.05), 0.0, 1.0);
}

vec3 pulseEffect(vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    vec2 p = uv * vec2(5.4, 6.6);
    float flow = fbm2(p * 0.36 + vec2(time * 0.035, -time * 0.025));
    float wave = sin(p.y * 3.8 - time * 0.72 + flow * 2.8);
    float scan = pow(max(0.0, 1.0 - abs(wave)), 4.0);
    float beam = pow(max(0.0, sin((p.x + p.y * 0.22) * 4.6 + time * 0.42 + flow * 2.0)), 5.0);
    float haze = smoothstep(0.26, 0.86, flow);
    alphaEnergy = clamp(haze * 0.34 + scan * 0.58 + beam * 0.30, 0.0, 1.0);
    vec3 blue = vec3(0.30, 0.82, 1.35);
    vec3 white = vec3(0.78, 0.96, 1.18);
    vec3 color = mix(blue, white, scan * 0.65 + beam * 0.24);
    return clamp(mix(color, tint, 0.24) * (0.28 + alphaEnergy * 1.14), 0.0, 1.0);
}

float seamlessNoise2(vec2 p, float width) {
    float x = mod(p.x, width);
    float t = smoothstep(0.0, 1.0, x / width);
    return mix(valueNoise2(vec2(x, p.y)), valueNoise2(vec2(x - width, p.y)), t);
}

float seamlessHash21(vec2 p, float width) {
    float x = mod(p.x, width);
    float t = smoothstep(0.0, 1.0, x / width);
    return mix(hash21(vec2(x, p.y)), hash21(vec2(x - width, p.y)), t);
}

vec3 skyStripesEffect(vec3 direction, float time, vec3 tint, out float alphaEnergy) {
    const float TAU = 6.28318530718;

    vec3 d = normalize(direction);

    float azimuth = fract(atan(d.z, d.x) / TAU + 0.5);
    float altitude = clamp(d.y, -1.0, 1.0);
    float altitude01 = clamp((altitude + 0.08) / 1.08, 0.0, 1.0);

    vec3 fwd = normalize(cameraForward.xyz);
    float viewAzimuth = fract(atan(fwd.z, fwd.x) / TAU + 0.5);
    float azToView = abs(fract(azimuth - viewAzimuth + 0.5) - 0.5);
    float viewGlow = exp(-(azToView * azToView) / (2.0 * 0.18 * 0.18));

    float verticalEnvelope = sin(clamp((altitude01 - 0.03) / 0.82, 0.0, 1.0) * 3.14159265);
    verticalEnvelope = pow(clamp(verticalEnvelope, 0.0, 1.0), 1.4);

    float edgeSharpening = mix(3.2, 1.8, verticalEnvelope);

    vec2 p = vec2(azimuth * 6.0, altitude01 * 1.8);
    float warp1 = seamlessNoise2(p + vec2(time * 0.04, time * 0.02), 6.0) - 0.5;
    float warp2 = seamlessNoise2(p * 2.0 - vec2(time * 0.03, time * 0.05), 12.0) - 0.5;
    vec2 warpedP = p + vec2(warp1 * 0.8 + warp2 * 0.3, warp1 * 0.4);

    float baseFill = seamlessNoise2(vec2(warpedP.x * 1.2 + time * 0.01, warpedP.y * 0.6), 7.2);
    baseFill = mix(0.05, 0.28, baseFill) * verticalEnvelope;

    float strandsNoise = seamlessNoise2(warpedP * 3.0, 18.0);
    float strands = sin(warpedP.x * 21.9911486 + strandsNoise * 4.0) * 0.5 + 0.5;
    strands = pow(strands, 1.8) * 0.12 * verticalEnvelope;

    float fold1Raw = abs(seamlessNoise2(vec2(warpedP.x * 2.8 + time * 0.02, warpedP.y * 0.8), 16.8) - 0.5) * 2.0;
    float fold1 = pow(1.0 - fold1Raw, edgeSharpening) * 0.42;

    float fold2Raw = abs(seamlessNoise2(vec2(warpedP.x * 6.0 - time * 0.04, warpedP.y * 1.8), 36.0) - 0.5) * 2.0;
    float fold2 = pow(1.0 - fold2Raw, edgeSharpening) * 0.22;

    float rawPattern = baseFill + strands + fold1 + fold2;

    float curtainPattern = (1.0 - exp(-rawPattern * 1.35)) * verticalEnvelope;

    float subtleGrain = seamlessHash21(vec2(azimuth * 350.0, altitude01 * 200.0 + time * 0.5), 350.0);
    float grainFilter = mix(0.94, 1.0, subtleGrain);

    curtainPattern *= grainFilter;
    curtainPattern *= mix(0.85, 1.15, viewGlow);

    vec3 darkTint = tint * 0.15;
    vec3 mainTint = tint * 0.85;
    vec3 brightTint = mix(tint, vec3(1.0), 0.28);

    vec3 curtainColor = mix(darkTint, mainTint, smoothstep(0.05, 0.45, curtainPattern));
    curtainColor = mix(curtainColor, brightTint, smoothstep(0.45, 0.85, curtainPattern));

    alphaEnergy = clamp(curtainPattern * 0.9, 0.0, 1.0);
    return clamp(curtainColor * curtainPattern, 0.0, 1.0);
}

vec3 planeEffect(float mode, vec2 uv, float time, vec3 tint, out float alphaEnergy) {
    if (mode < 0.5) {
        return waterEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 1.5) {
        return silkEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 2.5) {
        return auroraEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 3.5) {
        return nebulaEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 4.5) {
        return milkyVeilEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 5.5) {
        return plasmaEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 6.5) {
        return frostEffect(uv, time, tint, alphaEnergy);
    }
    if (mode < 7.5) {
        return emberEffect(uv, time, tint, alphaEnergy);
    }

    return pulseEffect(uv, time, tint, alphaEnergy);
}

vec3 seamlessSkyEffect(vec3 direction, float time, vec3 tint, float mode, out float alphaEnergy) {
    vec3 d = normalize(direction);
    vec3 weights = pow(max(abs(d), vec3(0.001)), vec3(3.0));
    weights /= max(weights.x + weights.y + weights.z, 0.001);

    float energyX = 0.0;
    float energyY = 0.0;
    float energyZ = 0.0;
    vec3 effectX = planeEffect(mode, d.yz * 0.5 + vec2(0.17, -0.31), time, tint, energyX);
    vec3 effectY = planeEffect(mode, d.xz * 0.5 + vec2(-0.43, 0.09), time, tint, energyY);
    vec3 effectZ = planeEffect(mode, d.xy * 0.5 + vec2(0.28, 0.37), time, tint, energyZ);

    alphaEnergy = dot(vec3(energyX, energyY, energyZ), weights);
    return effectX * weights.x + effectY * weights.y + effectZ * weights.z;
}

void main() {
    if (skyHidden(texCoord)) {
        discard;
    }

    vec3 direction = skyDirection(texCoord);
    vec2 verticalUv = vec2(texCoord.x, clamp(direction.y * 0.5 + 0.5, 0.0, 1.0));
    float time = meta.z;
    float intensity = params.y;
    float fade = verticalFade(verticalUv);
    vec3 tint = clamp(tintColor(verticalUv), 0.0, 1.0);
    float energy = 0.0;
    float mode = floor(meta.w + 0.5);

    if (mode > 8.5) {
        vec3 stripes = skyStripesEffect(direction, time, tint, energy);
        float stripesAlpha = clamp(energy * skyColor.a * intensity, 0.0, 0.995);
        // Альфа 0 вырождает source-over в сложение, так что отдельный блендинг режиму не нужен.
        fragColor = vec4(stripes * stripesAlpha, 0.0);
        return;
    }

    vec3 effect = seamlessSkyEffect(direction, time, tint, mode, energy);

    float alpha = clamp((0.12 + energy * 0.48) * skyColor.a * intensity * fade, 0.0, 0.74);
    fragColor = vec4(clamp(effect * intensity, 0.0, 1.0) * alpha, alpha);
}
