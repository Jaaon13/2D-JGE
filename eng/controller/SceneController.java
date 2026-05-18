package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecs.Entity;
import ecs.EntityManager;
import graphical.rendering.fonts.Text;
import logger.Logger.LoggerInfo;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class SceneController {

	// All of the current scenes
	private Map<Integer, Scene> scenes = new HashMap<>();
	
	// An increases key value
	private int index = 0;
	
	// Selected scene key to draw
	public int curIndex = -1;
	
	public long lastRun = System.nanoTime();
	
	private List<Event> events = new ArrayList<>();
	
	private boolean switchedTo = false;
	
	public EntityManager ecs;
	
	// Main loop for the scenes
	// Also calculates FPS
	public List<Entity> execute() {
		
		if(!switchedTo) {
			switchedTo = true;
			
			scenes.get(curIndex).switchedTo();
		}
		
		if( ((float) (System.nanoTime() - (float) lastRun) / 1000000000) >= Controller.globals.fixedDelta ) {
			
			lastRun = System.nanoTime();
			
			scenes.get(curIndex).fixedUpdate();
			Controller.globals.tick++;
			
		}
		
		if(curIndex != -1 && scenes.containsKey(curIndex)) {
			
			scenes.get(curIndex).events = getAllEvents();
			scenes.get(curIndex).update();
			
			events.removeAll(events);
			
			List<Entity> objs = scenes.get(curIndex).entities.getVisible();
			
			return objs;
		}
		
		return null;
		
	}
	
	private List<Event> getAllEvents() {
		
		for(char c : Controller.globals.keys) {
			
			events.add(new Event(type.UI_KeyPress, c));
			
		}
		
		return List.copyOf(events);
	}

	// Add a scene to the current scenes
	public int addScene(Scene s) {
		
		index++;
		scenes.put(index, s);
		
		return index;
		
	}
	
	public void setScene(int id) {
		
		Controller.logger.log("Scene set to scene with ID: " + id, LoggerInfo.INFO);
		
		Controller.globals.camera = null;
		curIndex = id;
		
		switchedTo = false;
		
		ecs = scenes.get(id).entities;
		
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public List<Text> getText() {
		return scenes.get(curIndex).text;
	}
	
}
