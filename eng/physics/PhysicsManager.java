package physics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.Controller;
import ecs.EngineComponets.Collision;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.Entity;
import ecs.EntityManager;
import gameManagers.InputManager;
import sceneManagment.Event;
import scenes.TestScene;

public class PhysicsManager {
	
	private Map<Point, List<Entity>> physicsObjects = new HashMap<>();
	
	private Set<Entity> moveables = new HashSet<>();
	
	public EntityManager entities;
	public InputManager im = new InputManager();
	
	public final int cellSize = 64;
	
	public PhysicsManager(EntityManager em) {
		this.entities = em;
		
		Controller.graphics.addTextOverlay("p1", new Point(0, 0));
		
	}
	
	public void addToDirection(Vector dir, Entity e) {
		
		MoveableObj mo = entities.get(e, MoveableObj.class);
		if(mo == null) {return;}

		mo.direction.add(dir);
		
	}
	
	public void add(Entity e) {
		
		if(!entities.contains(e, List.of(Pos.class, Size.class, Collision.class))) {
			return;
		}
		
		Pos p = entities.get(e, Pos.class);
		
		if(entities.contains(e, MoveableObj.class)) {
			MoveableObj mo = entities.get(e, MoveableObj.class);
			mo.position.x = p.x;
			mo.position.y = p.y;
			moveables.add(e);
		}
		
		put(new Point(p.x, p.y), e);
		
	}
	
	private void put(Point p, Entity e) {
		Point cell = new Point(p.x/cellSize, p.y/cellSize);
		
		if(physicsObjects.get(cell) == null) {
			physicsObjects.put(cell, new ArrayList<>());
		}
		
		physicsObjects.get(cell).add(e);
	}
	
	private void move(Entity e) {
		
		Pos p = entities.get(e, Pos.class);
		
		Point cell = new Point(p.x/cellSize, p.y/cellSize);
		
		if(physicsObjects.get(cell) != null) {
			if(physicsObjects.get(cell).contains(e)) {
				return;
			}
		}
		
		for(List<Entity> lst : physicsObjects.values()) {
			
			if(lst.contains(e)) {
				lst.remove(e); 
				break;
			}
			
		}
		
		put(new Point(p.x, p.y), e);
		
	}
	
	public void remove(Entity e) {
		
		if(!entities.contains(e, Pos.class)) {
			return;
		}
		
		Pos p = entities.get(e, Pos.class);
		Point cell = new Point(p.x/cellSize, p.y/cellSize);
		
		remove(e, cell);
		
	}
	
	private void remove(Entity e, Point cell) {
		physicsObjects.get(cell).remove(e);
		moveables.remove(e);
	}
	
	public void fixedUpdate() {
		
		
		
	}
	
	private long lastUpdate = System.nanoTime();
	
	public void update(List<Event> events) {
		
		float delta = (float)(System.nanoTime() - lastUpdate) / 1_000_000_000f;
		lastUpdate = System.nanoTime();
		
		im.processInputs(events);
		
		processMovement(delta);
		
		calculateCollisons(delta);
		
	}

	private void calculateCollisons(float delta) {
		
		for(Entity e : moveables) {
			
			Pos p = entities.get(e, Pos.class);
			
			if(e == null) {
				System.out.println("ERROR entity is null!");
			} else if(p == null) {
				System.out.println("ERROR pos of entity: " + e.id + " is null!");
			}
			
			Point cell = new Point(p.x/cellSize, p.y/cellSize);
			
			if(physicsObjects.get(cell) == null) {
				return;
			}
			
			List<Entity> toCheck = new ArrayList<>();
			toCheck.addAll(physicsObjects.get(cell));
			
			try {
				toCheck.addAll(physicsObjects.get(new Point(cell.x + 1, cell.y)));
			} catch(Exception err) {}
			try {
				toCheck.addAll(physicsObjects.get(new Point(cell.x - 1, cell.y)));
			} catch(Exception err) {}
			try {
				toCheck.addAll(physicsObjects.get(new Point(cell.x, cell.y + 1)));
			} catch(Exception err) {}
			try {
				toCheck.addAll(physicsObjects.get(new Point(cell.x, cell.y - 1)));
			} catch(Exception err) {}
			
			for(Entity e2 : toCheck) {
				
				if(e.id == e2.id) {
					continue;
				}
				
				if(AABBcheck(e, e2)) {
					
					((Collision)(entities.get(e, Collision.class))).strat.collision(e, e2, entities, delta);
					
				}
				
			}
			
		}
		
	}
	
	private boolean AABBcheck(Entity e1, Entity e2) {
		
		Collision col1 = entities.get(e1, Collision.class);
		Collision col2 = entities.get(e2, Collision.class);
		
		Pos p1 = entities.get(e1, Pos.class);
		Pos p2 = entities.get(e2, Pos.class);
		
		Point pos1 = new Point(p1.x + col1.offset.x, p1.y + col1.offset.y);
		Point pos2 = new Point(p2.x + col2.offset.x, p2.y + col2.offset.y);
		
		if(
				pos1.x < pos2.x + col2.size &&
				pos1.x + col1.size > pos2.x &&
				pos1.y < pos2.y + col2.size &&
				pos1.y + col1.size > pos2.y) {
			
				return true;
			
		}
		
		return false;
		
	}

	private void processMovement(float delta) {
		
		for(Entity e : moveables) {
			
			MoveableObj mo = entities.get(e, MoveableObj.class);

			if(mo.direction.isZero() && mo.velocity.isZero()) {
				continue;
			}
			
			Vector normal = mo.direction.normalize();
			
			if((mo.velocity.x < 0 && normal.x > 0) || (mo.velocity.x > 0 && normal.x < 0)) {
				mo.velocity.x = 0;
			}
			if((mo.velocity.y < 0 && normal.y > 0) || (mo.velocity.y > 0 && normal.y < 0)) {
				mo.velocity.y = 0;
			}
			
			if(mo.direction.x == 0) {
				mo.velocity.x -= mo.velocity.normalize().x * (float)(mo.friction) * delta;
			}
			if(mo.direction.y == 0) {
				mo.velocity.y -= mo.velocity.normalize().y * (float)(mo.friction) * delta;
			}
			
			if(!normal.isZero()) {
				
				mo.velocity.x += normal.x * (float)(mo.acceleration) * delta;
				mo.velocity.y += normal.y * (float)(mo.acceleration) * delta;
				
			} else {
				if(mo.velocity.magnitude() < (float)(mo.friction) * delta) {
					mo.velocity.x = 0;
					mo.velocity.y = 0;
				}
			}
			
			mo.velocity.clamp(mo.maxSpeed);
			
			mo.position.x += mo.velocity.x * delta;
			mo.position.y += mo.velocity.y * delta;
			
			entities.update(e, Pos.class, new Point((int)mo.position.x, (int)mo.position.y));
			
			mo.direction.reset();
			
			move(e);
			
		}
		
	}

}
