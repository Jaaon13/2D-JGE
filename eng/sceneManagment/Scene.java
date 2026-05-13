package sceneManagment;

import java.util.ArrayList;
import java.util.List;

import ecs.EntityManager;

public abstract class Scene {
	
	// Personal Entity manager for the scene
	public EntityManager entities = new EntityManager();
	
	// List of current events
	public List<Event> events = new ArrayList<>();
	
	// Runs every frame
	public abstract void update();
	
	// Runs at a fixed input // Default : 20 times per second
	public abstract void fixedUpdate();
	
	// Called when switched to
	public abstract void switchedTo();
	
	// Called on quit
	public abstract void kill();
	
}
