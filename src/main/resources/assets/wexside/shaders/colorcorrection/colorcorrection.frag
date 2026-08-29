#version 330 core

in vec2 vTextureUV;

uniform sampler2D uScene;
uniform float uContrast;
uniform float uSaturation;
uniform float uBrightness;

out vec4 FragColor;

vec3 adjustSaturation(vec3 color, float saturation) {
    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    return mix(vec3(gray), color, saturation);
}

vec3 adjustContrast(vec3 color, float contrast) {
    return (color - 0.5) * contrast + 0.5;
}

void main() {
    vec4 scene = texture(uScene, vTextureUV);
    vec3 corrected = adjustContrast(scene.rgb, uContrast);
    corrected = adjustSaturation(corrected, uSaturation) * uBrightness;
    FragColor = vec4(clamp(corrected, vec3(0.0), vec3(1.0)), scene.a);
}
