package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import assets.Atlas;
import assets.Texture;
import ecs.Entity;
import graphical.componets.EText;
import graphical.componets.EngText;
import graphical.componets.Tags;
import graphical.rendering.fonts.TextFactory;
import graphical.rendering.fonts.TextFactory.Alignment;
import graphical.window.Window;

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
	
	private class overlayData {
		
		public Point pos;
		public String data;
		
		public overlayData(Point p, String s) {
			this.pos = p;
			this.data = s;
		}
		
	}
	
	private HashMap<String, overlayData> overlays = new HashMap<>();
	
	private final Point defaultTextSize = new Point(6, 10);
	
	public void addTextOverlay(String name, Point pos) {
		
		overlays.put(name, new overlayData(pos, ""));
		
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
	
	public List<Entity> getOverlays() {
		List<Entity> toreturn = new ArrayList<>();
		
		for(overlayData key : overlays.values()) {
			
			toreturn.addAll(TextFactory.generateText(key.data, key.pos, Alignment.LEFT));
			
		}
		
		return toreturn;
	}
	
}
