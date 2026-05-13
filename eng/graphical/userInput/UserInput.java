package graphical.userInput;

import java.awt.Point;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;

import controller.Controller;
import sceneManagment.Event;
import sceneManagment.Event.type;

public class UserInput {

	public static void keypress(long window, int key, int scancode, int action, int mods) {	
		
		if(action == GLFW.GLFW_PRESS) {
			
			Controller.graphics.addKey((char )key);
			
		} else if(action == GLFW.GLFW_RELEASE) {
			
			Controller.graphics.removeKey((char )key);
			
		}
		
	}

	static boolean flag = false;
	
	public static void mousePress(long window, int button, int action, int mods) {
		
		type t;
		type t2;
		
		switch(button) {
		
		case GLFW.GLFW_MOUSE_BUTTON_1:
			t = type.UI_MouseLClick;
			break;
			
		case GLFW.GLFW_MOUSE_BUTTON_2:
			t = type.UI_MouseRClick;
			break;
		
		default:
			t = type.UI_Mouse3Click;
			break;
			
		}
		
		switch(action) {
		
		case GLFW.GLFW_PRESS:
			t2 = type.UI_MousePress;
			break;
			
		case GLFW.GLFW_RELEASE:
			t2 = type.UI_MouseRelease;
			break;
			
		default:
			t2 = null;
			break;
		
		}
		
		Controller.scenes.addEvent(new Event(t, t2, Controller.globals.mousePos));
		
	}

	public static void mouseMoved(long window, double xpos, double ypos) {
		
		Point mPos = new Point((int) xpos, (int) ypos);
		
		if(Objects.nonNull(Controller.globals.camera)) {
			
			mPos.x -= Controller.globals.camera.pos.x;
			mPos.y -= Controller.globals.camera.pos.y;
			
		}
		
		Controller.globals.mousePos = mPos;
		
	}
}
