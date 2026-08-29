#version 330 core

in vec2 vTextureUV;

uniform sampler2D uTexture;
uniform vec2 uScreenSize;
uniform vec2 uLocation;
uniform vec2 uRectSize;
uniform vec4 uColor1;

out vec4 FragColor;

bool insideRect()
{
    vec2 rectMin = uLocation;
    vec2 rectMax = uLocation + max(uRectSize, vec2(1.0));

    return gl_FragCoord.x >= rectMin.x
            && gl_FragCoord.x <= rectMax.x
            && gl_FragCoord.y >= rectMin.y
            && gl_FragCoord.y <= rectMax.y;
}

void main()
{
    if (!insideRect())
    {
        discard;
    }

    vec4 sampled = texture(uTexture, vTextureUV);

    if (sampled.a <= 0.01)
    {
        discard;
    }

    vec3 color = uColor1.rgb;
    float alpha = sampled.a * uColor1.a;

    FragColor = vec4(color, alpha);
}
