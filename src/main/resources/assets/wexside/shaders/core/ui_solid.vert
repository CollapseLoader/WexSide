#version 150

in vec3 Position;

layout(std140) uniform Projection {
    mat4 Proj;
};

void main() {
    gl_Position = Proj * vec4(Position, 1.0);
}
