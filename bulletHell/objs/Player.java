package objs;

import java.awt.Point;
import java.util.List;

import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.Entity;
import gameManagers.InputManager;
import physics.PhysicsManager;
import physics.Vector;
import sceneManagment.Event;

public class Player {

	public InputManager im = new InputManager();
	
	public Entity e;
	
	// PX per second
	public int maxSpeed = 70,
			acceleration = maxSpeed * 8, // Takes one full second to reach this acceleration
			friction = maxSpeed * 12; // Takes one full second to remove this much speed
	
	private long lasttime = System.nanoTime();
	
	Vector inVel = new Vector();
	Vector vel = new Vector();
	Vector position = new Vector();
	
	public Player(Entity e) {
		
		this.e = e;
		
		Pos p = Controller.currentScene.entities.get(e, Pos.class);
		this.position.x = p.x;
		this.position.y = p.y;
		
		PhysicsManager phys = Controller.currentScene.phys;
		
		// Input Manager Setup
		
		phys.im.mapInput('W', (() -> {phys.addToDirection(new Vector(0f, -1f), e);}));
		phys.im.mapInput('A', (() -> {phys.addToDirection(new Vector(-1f, 0f), e);}));
		phys.im.mapInput('S', (() -> {phys.addToDirection(new Vector(0f, 1f), e);}));
		phys.im.mapInput('D', (() -> {phys.addToDirection(new Vector(1f, 0f), e);}));
		
	}
	
	public void update(List<Event> events) {
		
		if((System.nanoTime() - lasttime) < 0) {
			return;
		}
		
		im.processInputs(events);
		
		updateVel();
		
	}
	
	private void updateVel() {
		
		float delta = (float)(System.nanoTime() - lasttime) / 1_000_000_000f;
		lasttime = System.nanoTime();
		
		Vector normal = inVel.normalize();
		
		if((vel.x < 0 && normal.x > 0) || (vel.x > 0 && normal.x < 0)) {
			vel.x = 0;
		}
		if((vel.y < 0 && normal.y > 0) || (vel.y > 0 && normal.y < 0)) {
			vel.y = 0;
		}
		
		if(inVel.x == 0) {
			vel.x -= vel.normalize().x * (float)(friction) * delta;
		}
		if(inVel.y == 0) {
			vel.y -= vel.normalize().y * (float)(friction) * delta;
		}
		
		if(!normal.isZero()) {
			
			vel.x += normal.x * (float)(acceleration) * delta;
			vel.y += normal.y * (float)(acceleration) * delta;
			
		} else {
			if(vel.magnitude() < (float)(friction) * delta) {
				vel.x = 0;
				vel.y = 0;
			}
		}
		
		vel.clamp(maxSpeed);
		
		position.x += vel.x * delta;
		position.y += vel.y * delta;
		
		Controller.currentScene.entities.update(e, Pos.class, new Point((int) (position.x), (int) (position.y)));
		
		inVel.reset();;
		
	}
	
}
