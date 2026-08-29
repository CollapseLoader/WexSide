#define V_TEX vTexCoord
#define MESH_POS meshPosition
#define MESH_SIZE meshSize
#define RADIUS4 radius
#define COL4 color
#define OUTLINE4 outlineColor
#define THICK thickness
#define SOFT softness
#define MSDF_EDGE msdfEdgeAttr
#define MSDF_TEXT_SIZE msdfTextSizeAttr
#define MSDF_RANGE msdfRangeAttr
#define MSDF_ATLAS msdfAtlasSizeAttr
#define VIEWPORT uViewportSize
#define STENCIL_MODE uStencilMode
#define DRAW_MODE drawMode
#define TEX_INDEX texIndex
#define TIME 0.0

vec4 GET_TEX(vec2 uv) {
    if (TEX_INDEX == 0) return texture(textureSampler[0], uv);
    if (TEX_INDEX == 1) return texture(textureSampler[1], uv);
    if (TEX_INDEX == 2) return texture(textureSampler[2], uv);
    if (TEX_INDEX == 3) return texture(textureSampler[3], uv);
    if (TEX_INDEX == 4) return texture(textureSampler[4], uv);
    if (TEX_INDEX == 5) return texture(textureSampler[5], uv);
    if (TEX_INDEX == 6) return texture(textureSampler[6], uv);
    if (TEX_INDEX == 7) return texture(textureSampler[7], uv);
    if (TEX_INDEX == 8) return texture(textureSampler[8], uv);
    if (TEX_INDEX == 9) return texture(textureSampler[9], uv);
    if (TEX_INDEX == 10) return texture(textureSampler[10], uv);
    if (TEX_INDEX == 11) return texture(textureSampler[11], uv);
    if (TEX_INDEX == 12) return texture(textureSampler[12], uv);
    if (TEX_INDEX == 13) return texture(textureSampler[13], uv);
    if (TEX_INDEX == 14) return texture(textureSampler[14], uv);
    if (TEX_INDEX == 15) return texture(textureSampler[15], uv);
    return vec4(0.0);
}

vec4 GET_TEX_AT(int idx, vec2 uv) {
    if (idx == 0) return texture(textureSampler[0], uv);
    if (idx == 1) return texture(textureSampler[1], uv);
    if (idx == 2) return texture(textureSampler[2], uv);
    if (idx == 3) return texture(textureSampler[3], uv);
    if (idx == 4) return texture(textureSampler[4], uv);
    if (idx == 5) return texture(textureSampler[5], uv);
    if (idx == 6) return texture(textureSampler[6], uv);
    if (idx == 7) return texture(textureSampler[7], uv);
    if (idx == 8) return texture(textureSampler[8], uv);
    if (idx == 9) return texture(textureSampler[9], uv);
    if (idx == 10) return texture(textureSampler[10], uv);
    if (idx == 11) return texture(textureSampler[11], uv);
    if (idx == 12) return texture(textureSampler[12], uv);
    if (idx == 13) return texture(textureSampler[13], uv);
    if (idx == 14) return texture(textureSampler[14], uv);
    if (idx == 15) return texture(textureSampler[15], uv);
    return vec4(0.0);
}

vec4 GET_TEXEL_AT(int idx, ivec2 texel) {
    if (idx == 0) return texelFetch(textureSampler[0], texel, 0);
    if (idx == 1) return texelFetch(textureSampler[1], texel, 0);
    if (idx == 2) return texelFetch(textureSampler[2], texel, 0);
    if (idx == 3) return texelFetch(textureSampler[3], texel, 0);
    if (idx == 4) return texelFetch(textureSampler[4], texel, 0);
    if (idx == 5) return texelFetch(textureSampler[5], texel, 0);
    if (idx == 6) return texelFetch(textureSampler[6], texel, 0);
    if (idx == 7) return texelFetch(textureSampler[7], texel, 0);
    if (idx == 8) return texelFetch(textureSampler[8], texel, 0);
    if (idx == 9) return texelFetch(textureSampler[9], texel, 0);
    if (idx == 10) return texelFetch(textureSampler[10], texel, 0);
    if (idx == 11) return texelFetch(textureSampler[11], texel, 0);
    if (idx == 12) return texelFetch(textureSampler[12], texel, 0);
    if (idx == 13) return texelFetch(textureSampler[13], texel, 0);
    if (idx == 14) return texelFetch(textureSampler[14], texel, 0);
    if (idx == 15) return texelFetch(textureSampler[15], texel, 0);
    return vec4(0.0);
}

ivec2 GET_TEX_SIZE(int idx) {
    if (idx == 0) return textureSize(textureSampler[0], 0);
    if (idx == 1) return textureSize(textureSampler[1], 0);
    if (idx == 2) return textureSize(textureSampler[2], 0);
    if (idx == 3) return textureSize(textureSampler[3], 0);
    if (idx == 4) return textureSize(textureSampler[4], 0);
    if (idx == 5) return textureSize(textureSampler[5], 0);
    if (idx == 6) return textureSize(textureSampler[6], 0);
    if (idx == 7) return textureSize(textureSampler[7], 0);
    if (idx == 8) return textureSize(textureSampler[8], 0);
    if (idx == 9) return textureSize(textureSampler[9], 0);
    if (idx == 10) return textureSize(textureSampler[10], 0);
    if (idx == 11) return textureSize(textureSampler[11], 0);
    if (idx == 12) return textureSize(textureSampler[12], 0);
    if (idx == 13) return textureSize(textureSampler[13], 0);
    if (idx == 14) return textureSize(textureSampler[14], 0);
    if (idx == 15) return textureSize(textureSampler[15], 0);
    return ivec2(1, 1);
}
