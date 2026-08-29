#version 330 core

in vec2 vTexCoord;
uniform sampler2D OverlaySampler;

out vec4 fragColor;

void main() {
    vec4 c = texture(OverlaySampler, vTexCoord);
    // FBO content is premultiplied (rendered with SRC_ALPHA blend onto cleared zero background),
    // so un-premultiply RGB before standard TRANSLUCENT compositing on the target framebuffer.
    if (c.a > 0.0) {
        c.rgb = c.rgb / c.a;
    }
    fragColor = c;
}
