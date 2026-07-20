#version 330 core

layout (location = 0) in vec2 pos;
layout (location = 1) in vec3 incolor;

out vec3 color;

uniform mat4 projection;

void main()
{
	gl_Position = projection * vec4(pos.xy, 0., 1.);
	color = incolor;
}