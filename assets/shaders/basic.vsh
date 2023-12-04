#version 330 core
layout (location = 0) in vec3 position;

out vec3 out_scale;

uniform vec3 u_scale;

void main()
{
    out_scale = u_scale;
    gl_Position = vec4(position.x, position.y, position.z, 1.0);
}