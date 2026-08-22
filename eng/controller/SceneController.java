package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecs.Entity;
import gui.factorys.Text;
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
	
	// Main loop for the scenes
	// Also calculates FPS
	public List<Entity> execute() {
		
		if(!switchedTo) {
			switchedTo = true;
			
			scenes.get(curIndex).switchedTo();
		}
		
		if( ((float)(System.nanoTime() - lastRun) / 1_000_000_000f) >= Controller.globals.fixedDelta ) {
			
			lastRun = System.nanoTime();
			
			scenes.get(curIndex).fixedUpdate();
			scenes.get(curIndex).world.phys.fixedUpdate();
			Controller.globals.tick++;
			
		}
		
		if(curIndex != -1 && scenes.containsKey(curIndex)) {
			
			scenes.get(curIndex).events = getAllEvents();
			scenes.get(curIndex).world.ecs.provokeListerners(events);
			scenes.get(curIndex).update();
			scenes.get(curIndex).world.phys.update(events);
			
			events.removeAll(events);
			
			List<Entity> objs = scenes.get(curIndex).world.container.getAllVisible();
			
			scenes.get(curIndex).world.container.regenerate();
			
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
		s.id = index;
		
		return index;
		
	}
	
	public void setScene(int id) {
		
		Controller.logger.log("Scene set to scene with ID: " + id, LoggerInfo.INFO);
		
		Controller.globals.camera = null;
		curIndex = id;
		
		switchedTo = false;
		
		Controller.currentScene = scenes.get(id);
		
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public List<Text> getText() {
		return scenes.get(curIndex).text;
	}
	
}
