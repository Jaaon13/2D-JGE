package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import assets.Atlas;
import assets.Texture;
import graphical.window.Window;
import gui.factorys.Text;
import gui.factorys.TextFactory.Alignment;

public class GraphicsController {

	public void startNewWindow(String title, int width, int height) {
		
		Controller.globals.screenSize = new Point(width, height);
		
		createWindow(title);
		
		Controller.globals.defaultFont = (Texture) Controller.assets.load("\\eng\\graphical\\rendering\\fonts\\minogram_6x10.png");
		Controller.globals.defaultFont.atlas = (Atlas) Controller.assets.load("\\eng\\graphical\\rendering\\fonts\\minogram_6x10.atlas");
		
		Controller.engineLoop();
		
	}
	
	private void createWindow(String title) {
		
		try {
			Window.init(title);
		} catch(Exception e) {
			
			System.err.println(e.getMessage());
			Controller.running = false;
			
		}
		
	}
	
	public void addKey(char c) {
		
		if(!Controller.debug.parseCommands(c)) {
			
			Controller.globals.keys.add(c);
			
		}
		
	}
	
	public void removeKey(char c) {
		
		Controller.globals.keys.remove(c);
		
	}
	
	private HashMap<String, Text> overlays = new HashMap<>();
	
	public void addTextOverlay(String name, Point pos) {
		
		overlays.put(name, new Text("", pos, Alignment.LEFT));
		
	}
	
	public boolean overlayExists(String name) {
		
		return overlays.containsKey(name);
		
	}
	
	public void updateTextOverlay(String name, String data) {
		overlays.get(name).data = data;
	}
	
	public void removeTextOverlay(String name) {
		overlays.remove(name);
	}
	
	public List<Text> getOverlays() {
		
		List<Text> tor = new ArrayList<>();
		tor.addAll(overlays.values());
		return tor;
	}

	public void setScreenSize(Point size) {
		
		Controller.globals.screenSize = size;
		GLFW.glfwSetWindowSize(Controller.globals.window, size.x, size.y);
		
	}

	public void updateTextOverlay(String name, String data, Point newPos) {
		overlays.get(name).data = data;
		overlays.get(name).pos = newPos;
	}
	
}
