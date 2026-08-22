package sceneManagment;

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
import ecs.Componet;
import ecs.Entity;
import ecs.EntityManager;
import logger.Logger.LoggerInfo;
import physics.PhysicsManager;

// This is where all of the entities in a scene are stored
// Also it holds onto the entity component system as well as a physics system
public class World {

	public EntityManager ecs = new EntityManager(this);
	public PhysicsManager phys = new PhysicsManager(this);
	
	public Container container = new Container();
	
	// This is a slow debug function
	public String describeEntity(Entity e) {
				
		StringBuilder sb = new StringBuilder();
				
		sb.append("\n\t---Entity #" + e.id + "---");
				
		for(Componet c : ecs.getAllComponets(e)) {
			sb.append("\n" + c.toString());
		}
				
		return sb.toString();
	}
	
	public class Container {
		
		// All entities with a position componet
		private Map<Long, Cell> entities = new HashMap<>();
		
		// All entities without a position
		private Set<Entity> others = new HashSet<>();
		
		// A list of dead entities
		private Set<Entity> deadEntities = new HashSet<>();
		
		public final int cellSize = 100;
		
		public static enum EntityType {
			PHYSICS,
			OTHER,
			ALL
		}
		
		public void regenerate() {
			
			List<Entity> allEntities = getAll(EntityType.ALL);
			
			entities.clear();
			
			for(Entity e : deadEntities) {
				ecs.remove(e);
				allEntities.remove(e);
			}
			
			for(Entity e : allEntities) {
				newAdd(e, false);
			}
			
			deadEntities.clear();
			
		}
		
		// Gives the index for the hashmap
		private long hash(int x, int y) {
			return ((long)(x)) << 32 | (y);
		}
		
		public void add(Entity e) {
			
			newAdd(e, true);
			
		}
		
		private void newAdd(Entity e, Boolean isNew) {
			
			Pos pos = ecs.get(e, Pos.class);
			
			if(pos == null) {
				others.add(e);
				return;
			}
			
			if(isNew) {
				
				MoveableObj mo = ecs.get(e, MoveableObj.class);
				
				if(mo != null) {
					mo.position.x = pos.x;
					mo.position.y = pos.y;
					
					mo.lastPosition.x = pos.x;
					mo.lastPosition.y = pos.y;
				}
				
			}
			
			long hash = hash(pos.x / cellSize, pos.y / cellSize);
			
			if(entities.get(hash) == null) {
				entities.put(hash, new Cell());
			}
			entities.get(hash).add(e);
			
		}
		
		public List<Entity> getCellByRawPosition(Point raw, EntityType type) {
			return getCellByPosition(new Point(raw.x / cellSize, raw.y / cellSize), type);
		}
		
		public List<Entity> getCellByPosition(Point cellPos, EntityType type) {
			
			Cell cell = entities.get(hash(cellPos.x, cellPos.y));
			
			if(cell == null) {
				return new ArrayList<>();
			}
			
			switch(type) {
			case ALL:
				List<Entity> all = new ArrayList<>();
				all.addAll(cell.other);
				all.addAll(cell.phys);
				return all;
			case OTHER:
				return cell.other;
			case PHYSICS:
				return cell.phys;
			default:
				return new ArrayList<>();
			
			}
			
		}
		
		public List<Entity> getAllVisible() {
			
			List<Entity> toReturn = new ArrayList<>();
			
			Point screen = Controller.globals.screenSize, camera = (Controller.globals.camera != null) ?
					new Point(Controller.globals.camera.pos.x / cellSize, Controller.globals.camera.pos.y / cellSize) : new Point(0, 0);
			
			for(int x = screen.x / cellSize; x >= 0; x--) {
				for(int y = screen.y / cellSize; y >= 0; y--) {
					toReturn.addAll(getCellByPosition(new Point(x + camera.x, y + camera.y), EntityType.ALL));
				}
			}
			
			return toReturn;
			
		}
		
		public List<Entity> getAll(EntityType type) {
		
			List<Entity> toReturn = new ArrayList<>();
			
			for(Cell c : entities.values()) {
				switch(type) {
				case ALL:
					toReturn.addAll(c.other);
					toReturn.addAll(c.phys);
					break;
				case OTHER:
					toReturn.addAll(c.other);
					break;
				case PHYSICS:
					toReturn.addAll(c.phys);
					break;
				
				}
			}
			
			return toReturn;
			
		}
		
		public void remove(Entity e) {
			deadEntities.add(e);
		}
		
	}
	
	private class Cell {
		
		public List<Entity> phys = new ArrayList<>();
		public List<Entity> other = new ArrayList<>();
		
		public void add(Entity e) {
			if(ecs.contains(e, List.of(Size.class, Collision.class))) {
				phys.add(e);	
			} else {
				other.add(e);
			}
		}
		
	}
	
}
