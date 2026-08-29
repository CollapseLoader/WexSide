#version 330 core

in vec2 vTextureUV;

uniform sampler2D SourceSampler;
uniform sampler2D MaskSampler;

out vec4 FragColor;

void main() {
    vec4 source = texture(SourceSampler, vTextureUV);
    float mask = texture(MaskSampler, vTextureUV).a;
    FragColor = vec4(source.rgb * mask, mask);
}
