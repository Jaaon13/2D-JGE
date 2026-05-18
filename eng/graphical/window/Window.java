package graphical.window;

import java.awt.Point;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;

import controller.Controller;
import graphical.userInput.UserInput;

public class Window {

	public static void init(String title) throws Exception {
		
		// Initalize GLFW and if failed throw an exception
		if(!GLFW.glfwInit()) {
			throw new Exception("Failed to intialize glfw!");
		}
		
		// Create the window using height and width and title
		Controller.globals.window = GLFW.glfwCreateWindow(
				Controller.globals.screenSize.x, Controller.globals.screenSize.y, title, MemoryUtil.NULL, MemoryUtil.NULL);
		
		// If it failed to make the window throw an exception
		if(Controller.globals.window == 0) {
			GLFW.glfwTerminate();
			throw new Exception("Failed to make window!");
		}
		
		// Make the current window context current
		GLFW.glfwMakeContextCurrent(Controller.globals.window);
		
		// Make the window visible
		GLFW.glfwShowWindow(Controller.globals.window);
		
		// Make the OpenGL capabilities
		GL.createCapabilities();
		
		GLFW.glfwSwapInterval(0);
		
		// Window Hints
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		boolean enableGLFWdebug = true;
		
		if(enableGLFWdebug) {
			
			// Set up debug shit
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
			
		}
		
		if (GL.getCapabilities().glDebugMessageCallback != 0) {
		    GLUtil.setupDebugMessageCallback();
		    GL43.glDebugMessageControl(GL43.GL_DONT_CARE, GL43.GL_DONT_CARE, GL43.GL_DEBUG_SEVERITY_NOTIFICATION, (IntBuffer)null, false);
		}
		
		userInputInit();
		
	}
	
	private static void userInputInit() {
		
		// Setup user input
		GLFW.glfwSetKeyCallback(Controller.globals.window, (window, key, scancode, action, mods)
				-> UserInput.keypress(window, key, scancode, action, mods));
				
		GLFW.glfwSetMouseButtonCallback(Controller.globals.window, (window, button, action, mods) 
				-> UserInput.mousePress(window, button, action, mods));
		
		// Set window Resizing callback
		
		GLFW.glfwSetFramebufferSizeCallback(Controller.globals.window, (window, width, height)
				-> {
		
						GL30.glViewport(0, 0, width, height);
						Controller.globals.screenSize = new Point(width, height);
						Controller.render.windowResized();
		
					});
		
	}
	
}
