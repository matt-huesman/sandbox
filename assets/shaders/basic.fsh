#version 330 core
in vec3 out_scale;

out vec4 FragColor;

void main()
{
    FragColor = vec4(out_scale.xyz, 1.0f);
}