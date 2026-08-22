package utilities;

import java.io.BufferedReader;
import java.io.FileReader;

import org.lwjgl.opengl.GL30;

public class ShaderUtils {
	
	@SuppressWarnings("resource")
	public
	static String readShaderFile(String location) {
		
		StringBuilder sb = new StringBuilder();
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(location));
			String line = null;
			
			while((line = reader.readLine()) != null) {
				
				sb.append(line + "\n");
				
			}
			
			if(sb.isEmpty()) {
				
				throw new Exception("Failed to read any data");
				
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sb.toString();
		
	}
	
	static int compileShader(String source, int type) {
		
		// Make a shader id
		int id = GL30.glCreateShader(type);
		
		// Give it the source code of the shader
		GL30.glShaderSource(id, source);
		
		// Compile the Shader
		GL30.glCompileShader(id);
		
		// Error handling
		int result = GL30.glGetShaderi(id, GL30.GL_COMPILE_STATUS);
		
		if(result == GL30.GL_FALSE) {
			
			int length = GL30.glGetShaderi(id, GL30.GL_INFO_LOG_LENGTH);
			String message = GL30.glGetShaderInfoLog(id, length);
			
			System.err.println("Failed to compile " +
			(type == GL30.GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader!\n" + message);
			
			GL30.glDeleteShader(id);
			
			return -1;
			
		}
		
		return id;
		
	}
	
	public static int createShader(String vertex, String fragment) {
		
		// Create the shader program
		int program = GL30.glCreateProgram();
		
		// Create the vertex shader
		int vs = compileShader(readShaderFile(vertex), GL30.GL_VERTEX_SHADER);
		
		// Create the fragment shader
		int fs = compileShader(readShaderFile(fragment), GL30.GL_FRAGMENT_SHADER);
		
		// Attach the shaders to the program
		GL30.glAttachShader(program, vs);
		GL30.glAttachShader(program, fs);
		
		// Link and validate the program
		GL30.glLinkProgram(program);
		GL30.glValidateProgram(program);
		
		// Delete the old shader
		GL30.glDeleteShader(vs);
		GL30.glDeleteShader(fs);
		
		return program;
		
	}
	
}
