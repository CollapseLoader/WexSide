#version 330 core
layout(std140) uniform WexGlobals {
    mat4 orthographicMatrix;
    vec2 uViewportSize;
    int uStencilMode;
    int _pad0;
    ivec4 uScissorRect;
};

in vec2 vTexCoord;
in vec2 meshPosition;
in vec2 meshSize;
in vec4 radius;
in vec4 color;
in vec4 outlineColor;
in float thickness;
in float softness;
in float msdfEdgeAttr;
in float msdfTextSizeAttr;
in float msdfRangeAttr;
in vec2 msdfAtlasSizeAttr;
flat in int texIndex;
flat in int drawMode;

uniform sampler2D textureSampler[16];
uniform sampler2D MaskTex;

out vec4 fragColor;

#include "/assets/wexside/shaders/common/defines.glsl"
#include "/assets/wexside/shaders/common/helpers.glsl"
#include "/assets/wexside/shaders/common/iface_dc.glsl"
#include "/assets/wexside/shaders/common/modes.glsl"

void main() {
    vec2 fragUL = vec2(gl_FragCoord.x, uViewportSize.y - gl_FragCoord.y);

    if (uStencilMode == 2 || uStencilMode == 3) {
        vec2 uv = gl_FragCoord.xy / uViewportSize;
        float m = texture(MaskTex, uv).r;
        if (m < 0.5) discard;
    }

    vec4 c = shade(fragUL);

    if (uStencilMode == 1 || uStencilMode == 3) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
        return;
    }

    fragColor = c;
}
