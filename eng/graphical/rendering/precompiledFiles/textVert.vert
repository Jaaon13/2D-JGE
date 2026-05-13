#version 330 core

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 textureCoords;

out vec2 textPos;
out int textureId;

void main()
{
	gl_Position = vec4(pos.xy, 0., 1.);
	textPos = textureCoords;
}