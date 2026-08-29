#version 330 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aMeshPos;
layout(location = 2) in vec2 aMeshSize;
layout(location = 3) in vec2 aTexCoord;
layout(location = 4) in vec4 aRadius;
layout(location = 5) in vec4 aColor;
layout(location = 6) in vec4 aOutlineColor;
layout(location = 7) in float aThickness;
layout(location = 8) in float aSoftness;
layout(location = 9) in float aMsdfEdge;
layout(location = 10) in float aMsdfTextSize;
layout(location = 11) in float aMsdfRange;
layout(location = 12) in vec2 aMsdfAtlasSize;
layout(location = 13) in int aTexIndex;
layout(location = 14) in int aDrawMode;

uniform mat4 orthographicMatrix;

out vec2 vTexCoord;
out vec2 meshPosition;
out vec2 meshSize;
out vec4 radius;
out vec4 color;
out vec4 outlineColor;
out float thickness;
out float softness;
out float msdfEdgeAttr;
out float msdfTextSizeAttr;
out float msdfRangeAttr;
out vec2 msdfAtlasSizeAttr;
flat out int texIndex;
flat out int drawMode;

void main() {
    gl_Position = orthographicMatrix * vec4(aPosition, 0.0, 1.0);
    vTexCoord = aTexCoord;
    meshPosition = aMeshPos;
    meshSize = aMeshSize;
    radius = aRadius;
    color = aColor;
    outlineColor = aOutlineColor;
    thickness = aThickness;
    softness = aSoftness;
    msdfEdgeAttr = aMsdfEdge;
    msdfTextSizeAttr = aMsdfTextSize;
    msdfRangeAttr = aMsdfRange;
    msdfAtlasSizeAttr = aMsdfAtlasSize;
    texIndex = aTexIndex;
    drawMode = aDrawMode;
}
