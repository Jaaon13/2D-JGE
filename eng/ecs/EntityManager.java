package ecs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import logger.Logger.LoggerInfo;

public class EntityManager {
	
	private final int gridSize = 64;
	
	// Holds the entity ID's which one entity can encompass multiple squares
	private Map<Point, List<Integer>> grid = new HashMap<>();

	// Sorted by Entity ID to allow fast get and insertions
	private Map<Integer, Entity> entities = new HashMap<>();
	
	// Entity must have a Pos and Size componet
	public void add(Entity e) {
		
		entities.put(e.id, e);
		
		modifyEntityInGrid(e, ( () -> {
			
			Point p = new Point(gridX, gridY);
			
			if(!grid.containsKey(p)) {
				grid.put(p, new ArrayList<>());
			}
			
			grid.get(p).add(e.id);
			
		} ));
		
	}
	
	// Entity must have a Pos and Size componet
	public void remove(Entity e) {
		
		entities.remove(e.id, e);
		
		modifyEntityInGrid(e, ( () -> {
			
			Point p = new Point(gridX, gridY);
			
			if(!grid.containsKey(p)) {
				grid.put(p, new ArrayList<>());
			}
			
			grid.get(p).remove(e.id);
			
		} ));
		
	}
	
	public void clear() {
		
		entities.clear();
		grid.clear();
		
	}
	
	public Entity get(int id) {
		return entities.get(id);
	}
	
	public List<Entity> getCell(Point p) {
		
		List<Integer> ids = grid.get(p);
		
		Set<Entity> re = new HashSet<>();
		
		for(int id : ids) {
			
			re.add(entities.get(id));
			
		}
		
		return setToList(re);
		
		
	}
	
	public List<Entity> getVisible() {
		
		Set<Entity> re = new HashSet<>();
		
		Point topLeft = (Controller.globals.camera == null) ? new Point(0, 0) : new Point(Controller.globals.camera.pos);
		Point screen = new Point(Controller.globals.screenSize);
		
		for(int x = topLeft.x; x <= (screen.x / gridSize) + topLeft.x; x++) {
			
			for(int y = topLeft.y; y <= (screen.y / gridSize) + topLeft.y; y++) {
				
				List<Integer> ids = grid.get(new Point(x, y));
				
				if(ids == null) {continue;}
				
				for(int i : ids) {
					re.add(entities.get(i));
				}
				
			}
			
		}
		
		return setToList(re);
		
	}
	
	private int gridX = 0, gridY= 0;
	
	private void modifyEntityInGrid(Entity e, Runnable run) {
		
		if(!e.containsComponet("Pos")) {
			Controller.logger.log("Tried to modify an entity without a Position componet! ID: " + e.id, LoggerInfo.ERROR);
			return;
		} else if(!e.containsComponet("Size")) {
			Controller.logger.log("Tried to modify an entity without a Size componet! ID: " + e.id, LoggerInfo.ERROR);
			return;
		}
		
		Pos pos = (Pos) e.componets.get("Pos");
		Size size = (Size) e.componets.get("Size");
		
		Point topLeft = new Point(pos.x / gridSize, pos.y / gridSize);
		Point bottomRight = new Point((pos.x + size.x) / gridSize, (pos.y + size.y) / gridSize);
		
		for(int x = topLeft.x; x <= bottomRight.x + topLeft.x; x++) {
			
			for(int y = topLeft.y; y <= bottomRight.y + topLeft.y; y++) {
				
				gridX = x;
				gridY = y;
				
				run.run();
				
			}
			
		}
		
	}
	
	private <T> List<T> setToList(Set<T> set) {
		
		List<T> list = new ArrayList<>();
		
		for(T t : set) {
			list.add(t);
		}
		
		return list;
		
	}
	
}
