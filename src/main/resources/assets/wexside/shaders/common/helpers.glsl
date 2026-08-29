float sdRoundedBox(vec2 p, vec2 s, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - s + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

float median(float r, float g, float b) {
    return max(min(r,g), min(max(r,g), b));
}

float noise(in vec2 st) {
    return fract(sin(dot(st, vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 clampCornerRadii(vec4 radii, vec2 size) {
    float maxRadius = max(0.0, min(size.x, size.y) * 0.5);
    return min(radii, vec4(maxRadius));
}

vec4 insetCornerRadii(vec4 radii, vec2 size, float inset) {
    vec2 innerSize = max(size - vec2(inset * 2.0), vec2(0.0));
    return clampCornerRadii(max(radii - vec4(inset), vec4(0.0)), innerSize);
}
