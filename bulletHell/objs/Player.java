package objs;

import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.Entity;
import gameManagers.InputManager;
import physics.PhysicsManager;
import physics.Vector;

public class Player {
	
	public Entity e;
	
	public Player(Entity e) {
		
		this.e = e;
		
		PhysicsManager phys = Controller.currentScene.world.phys;
		
		// Input Manager Setup
		
		phys.im.mapInput('W', (() -> {phys.addToDirection(new Vector(0f, -1f), e);}));
		phys.im.mapInput('A', (() -> {phys.addToDirection(new Vector(-1f, 0f), e);}));
		phys.im.mapInput('S', (() -> {phys.addToDirection(new Vector(0f, 1f), e);}));
		phys.im.mapInput('D', (() -> {phys.addToDirection(new Vector(1f, 0f), e);}));
		
	}
	
}
