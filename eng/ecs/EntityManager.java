package ecs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import ecs.EngineComponets.Pos;

public class EntityManager {
	
	private List<Entity> entities = new ArrayList<>();
	
	// Sorted by the class of the Componet, and indexed by the entity ID its associated with
	private Map<Class<? extends Componet>, Map<Integer, Componet>> componets = new HashMap<>();
	
	public void add(Entity e) {
		
		entities.add(e);
		
	}
	
	
	
	public void addComponet(int id, Componet c) {
		
		if(componets.get(c.getClass()) == null) {
			componets.put(c.getClass(), new HashMap<>());
		}
		
		componets.get(c.getClass()).put(id, c);
		
	}
	
	public Componet get(Entity e, Class<? extends Componet> c) {
		return get(e.id, c);
	}
	
	public Componet get(int id, Class<? extends Componet> c) {
		
		return componets.get(c).get(id);
		
	}
	
	public void remove(Entity e) {
		
		entities.remove(e);
		
		for(Map<Integer, Componet> map : componets.values()) {
			
			while(map.containsKey(e.id)) {
				
				map.remove(e.id);
				
			}
			
		}
		
	}
	
	public boolean contains(Entity e, List<Class<? extends Componet>> c) {
		return contains(e.id, c);
	}
	
	public boolean contains(int id, List<Class<? extends Componet>> c) {
		
		for(Class<? extends Componet> cl : c) {
			if(!contains(id, cl)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	public boolean contains(Entity e, Class<? extends Componet> c) {
		return contains(e.id, c);
	}
	
	public boolean contains(int id, Class<? extends Componet> c) {
		
		return componets.get(c).containsKey(id);
		
	}
	
	public void clear() {
		
		entities.clear();
		componets.clear();
		
	}
	
	public List<Entity> getVisible() {
		
		List<Entity> visible = new ArrayList<>();
		
		Point s = Controller.globals.screenSize;
		
		for(Entity e : entities) {
			
			Pos p = (Pos) get(e, Pos.class);
			
			if( (p.x >= 0 && p.x <= s.x) && (p.y >= 0 && p.y <= s.y)) {
				visible.add(e);
			}
			
		}
		
		return visible;
		
	}



	public List<Entity> getAll() {
		return entities;
	}
	
}
