#version 330 core

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
uniform vec2 uViewportSize;
uniform int uStencilMode;
uniform float uTime;

out vec4 fragColor;

#include "/assets/wexside/shaders/common/defines.glsl"
#include "/assets/wexside/shaders/common/helpers.glsl"
#include "/assets/wexside/shaders/common/iface_gl.glsl"
#include "/assets/wexside/shaders/common/modes.glsl"

void main() {
    vec2 fragUL = vec2(gl_FragCoord.x, uViewportSize.y - gl_FragCoord.y);
    fragColor = shade(fragUL);
}
