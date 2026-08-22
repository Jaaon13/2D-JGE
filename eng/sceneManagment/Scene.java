package sceneManagment;

import java.util.ArrayList;
import java.util.List;

import gui.factorys.Text;

public abstract class Scene {
	
	public int id;
	
	public World world = new World();
	
	// Text wanted to be drawn
	public List<Text> text = new ArrayList<>();
	
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
