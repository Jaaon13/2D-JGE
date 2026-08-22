package physics;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import controller.Controller;
import ecs.EngineComponets.Collision;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.Pos;
import ecs.Entity;
import gameManagers.InputManager;
import logger.Logger.LoggerInfo;
import sceneManagment.Event;
import sceneManagment.World;
import sceneManagment.World.Container.EntityType;

public class PhysicsManager {
	
	private World world;
	
	public InputManager im = new InputManager();
	
	private float fdelta = 1f/20f;
	
	private long lastTick = System.nanoTime();
	
	public PhysicsManager(World world) {
		this.world = world;
		
		Controller.graphics.addTextOverlay("p1", new Point(0, 0));
		
	}
	
	public void addToDirection(Vector dir, Entity e) {
		
		MoveableObj mo = world.ecs.get(e, MoveableObj.class);
		if(mo == null) {return;}

		mo.direction.add(dir);
		
	}
	
	public void update(List<Event> events) {
		
		im.processInputs(events);
		
		interpPositions();
		
	}
	
	// Allows for a fully fixed rate physics engine but shows smooth movement on a 1 - 0 tick delay
	
	private List<Entity> tointerp = new ArrayList<>();
	
	private void interpPositions() {
		
		float percentoftick = ((float)(System.nanoTime() - lastTick) / 1_000_000_000f) / fdelta;
		
		for(Entity e : tointerp) {
			
			try {
				
				MoveableObj mo = world.ecs.get(e, MoveableObj.class);
				
				Vector newPos = vecLerp(mo.lastPosition, mo.position, percentoftick);
				
				world.ecs.update(e, Pos.class, new Point((int)newPos.x, (int)newPos.y));
				
			} catch(Exception err) { 
				// allowed to just pass errors as it means the entity was deleted and needs no interpolation
			}
			
		}
		
	}
	
	private Vector vecLerp(Vector start, Vector end, float percent) {
		
		Vector r = new Vector();
		
		r.x = start.x + (end.x - start.x) * percent;
		r.y = start.y + (end.y - start.y) * percent;
		
		return r;
		
	}
	
	public void fixedUpdate() {;
		
		processMovement();
		
		calculateCollisons();
		
		lastTick = System.nanoTime();
		
	}

	private void calculateCollisons() {
		
		for(Entity e : world.container.getAll(EntityType.PHYSICS)) {
			
			if(!world.ecs.contains(e, MoveableObj.class)) {continue;}
			
			Pos p = world.ecs.get(e, Pos.class);
			
			if(e == null) {
				System.out.println("ERROR entity is null!");
			} else if(p == null) {
				System.out.println("ERROR pos of entity: " + e.id + " is null!");
			}
			
			Point cell = new Point(p.x / world.container.cellSize, p.y / world.container.cellSize);
			
			if(world.container.getCellByPosition(cell, EntityType.PHYSICS) == null) {
				return;
			}
			
			List<Entity> toCheck = new ArrayList<>();
			toCheck.addAll(world.container.getCellByPosition(cell, EntityType.PHYSICS));
			
			// Add all surrounding cells
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x + 1, cell.y), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x + 1, cell.y + 1), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x + 1, cell.y - 1), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x - 1, cell.y), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x - 1, cell.y + 1), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x - 1, cell.y - 1), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x, cell.y + 1), EntityType.PHYSICS));
			toCheck.addAll(world.container.getCellByPosition(new Point(cell.x, cell.y - 1), EntityType.PHYSICS));
			
			for(Entity e2 : toCheck) {
				
				if(e.id == e2.id) {
					continue;
				}
				
				if(AABBcheck(e, e2)) {
					((Collision)(world.ecs.get(e, Collision.class))).strat.collision(e, e2, world.ecs, fdelta);
					((Collision)(world.ecs.get(e2, Collision.class))).strat.collision(e2, e, world.ecs, fdelta);
				}
				
			}
			
		}
		
	}
	
	public boolean AABBcheck(Entity e1, Entity e2) {
		
		Collision col1 = world.ecs.get(e1, Collision.class);
		Collision col2 = world.ecs.get(e2, Collision.class);
		
		Pos rpos1 = world.ecs.get(e1, Pos.class);
		Pos rpos2 = world.ecs.get(e2, Pos.class);
		
		Vector p1 = (world.ecs.contains(e1, MoveableObj.class))
				? ((MoveableObj)(world.ecs.get(e1, MoveableObj.class))).position.copy()
				: new Vector(rpos1.x, rpos1.y);
		
		Vector p2 = (world.ecs.contains(e2, MoveableObj.class))
				? ((MoveableObj)(world.ecs.get(e2, MoveableObj.class))).position.copy()
				: new Vector(rpos2.x, rpos2.y);
		
		Point pos1 = new Point((int)p1.x + col1.offset.x, (int)p1.y + col1.offset.y);
		Point pos2 = new Point((int)p2.x + col2.offset.x, (int)p2.y + col2.offset.y);
		
		if(
				pos1.x < pos2.x + col2.size &&
				pos1.x + col1.size > pos2.x &&
				pos1.y < pos2.y + col2.size &&
				pos1.y + col1.size > pos2.y) {
			
				return true;
			
		}
		
		return false;
		
	}

	private void processMovement() {
		
		tointerp.clear();
		
		for(Entity e : world.container.getAll(EntityType.PHYSICS)) {
			
			if(!world.ecs.contains(e, MoveableObj.class)) {continue;}
			
			MoveableObj mo = world.ecs.get(e, MoveableObj.class);

			if(mo.direction.isZero() && mo.velocity.isZero()) {
				continue;
			}
			
			tointerp.add(e);
			
			Vector normal = mo.direction.normalize();
			
			if((mo.velocity.x < 0 && normal.x > 0) || (mo.velocity.x > 0 && normal.x < 0)) {
				mo.velocity.x = 0;
			}
			if((mo.velocity.y < 0 && normal.y > 0) || (mo.velocity.y > 0 && normal.y < 0)) {
				mo.velocity.y = 0;
			}
			
			if(mo.direction.x == 0) {
				mo.velocity.x -= mo.velocity.normalize().x * (float)(mo.friction) * fdelta;
			}
			if(mo.direction.y == 0) {
				mo.velocity.y -= mo.velocity.normalize().y * (float)(mo.friction) * fdelta;
			}
			
			if(!normal.isZero()) {
				
				mo.velocity.x += normal.x * (float)(mo.acceleration) * fdelta;
				mo.velocity.y += normal.y * (float)(mo.acceleration) * fdelta;
				
			} else {
				if(mo.velocity.magnitude() < (float)(mo.friction) * fdelta) {
					mo.velocity.x = 0;
					mo.velocity.y = 0;
					mo.triggers.onStop(e);
				}
			}
			
			mo.velocity.clamp(mo.maxSpeed);
			
			mo.lastPosition = mo.position.copy();
			
			mo.position.x += mo.velocity.x * fdelta;
			mo.position.y += mo.velocity.y * fdelta;
			
			world.ecs.update(e, Pos.class, new Point((int)mo.lastPosition.x, (int)mo.lastPosition.y));
			
			Point screen = Controller.globals.screenSize, camera = (Controller.globals.camera != null) ?
					new Point(Controller.globals.camera.pos) : new Point(0, 0);
			
			if((int)mo.position.x < camera.x - 50 || (int)mo.position.x > screen.x + camera.x + 50 
					|| (int)mo.position.y < camera.y - 50 || (int)mo.position.y > screen.y + camera.y + 50) {
				
				mo.triggers.onMapLeave(e);
				
			}
			
			mo.direction.reset();
			
		}
		
	}

}
