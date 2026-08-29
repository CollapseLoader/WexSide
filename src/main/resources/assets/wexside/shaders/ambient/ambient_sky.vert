#version 150

layout(std140) uniform AmbientSkyData {
    vec4 meta;
    vec4 skyColor;
    vec4 skyColor2;
    vec4 params;
    vec4 cameraRight;
    vec4 cameraUp;
    vec4 cameraForward;
};

out vec2 texCoord;

void main() {
    vec2 positions[6] = vec2[](
        vec2(-1.0, -1.0),
        vec2( 1.0, -1.0),
        vec2( 1.0,  1.0),
        vec2(-1.0, -1.0),
        vec2( 1.0,  1.0),
        vec2(-1.0,  1.0)
    );

    vec2 uvs[6] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );

    gl_Position = vec4(positions[gl_VertexID], 1.0, 1.0);
    texCoord = uvs[gl_VertexID];
}
