package ecs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import ecs.EngineComponets.Listener;
import ecs.EngineComponets.Listener.EventWrapper;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import logger.Logger.LoggerInfo;
import sceneManagment.Event;

public class EntityManager {
	
	private List<Entity> entities = new ArrayList<>();
	
	// Sorted by the class of the Componet, and indexed by the entity ID its associated with
	private Map<Class<? extends Componet>, Map<Integer, Componet>> componets = new HashMap<>();
	
	public void add(Entity e) {
		entities.add(e);
		Controller.currentScene.phys.add(e);
	}
	
	public void addComponet(int id, Componet c) {
		
		if(componets.get(c.getClass()) == null) {
			componets.put(c.getClass(), new HashMap<>());
		}
		
		componets.get(c.getClass()).put(id, c);
		
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public <T extends Componet> T get(Entity e, Class<? extends Componet> c) {
		try {
			return (T) get(e.id, c);
		} catch(NullPointerException err) {
			if(false) { // Will overflow the logging!
				Controller.logger.log(List.of("Tried to get a componet that does not exist!", "Entity ID: " + e.id), LoggerInfo.WARNING);
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Componet> T get(int id, Class<? extends Componet> c) {
		
		return (T) componets.get(c).get(id);
		
	}
	
	public List<Componet> getAllComponets(Entity e) {
		return getallcmp(e.id);
	}
	
	public List<Componet> getAllComponets(int id) {
		return getallcmp(id);
	}
	
	private List<Componet> getallcmp(int id) {
		
		List<Componet> cmpts = new ArrayList<>();
		
		for(Class<? extends Componet> c : componets.keySet()) {
			
			Componet comp;
			
			try {
				comp = componets.get(c).get(id);
			} catch(Exception e) {
				comp = null;
			}
			
			if(comp != null) {
				
				cmpts.add(comp);
				
			}
			
		}
		
		return cmpts;
	}

	public <T> void update(Entity e, Class<? extends Componet> c, T t) {
		update(e.id, c, t);
	}

	public <T> void update(int id, Class<? extends Componet> c, T t) {
		Componet comp = get(id, c);
		if(comp == null) {
			Controller.logger.log(List.of("Failed to update entity with id: " + id,
					"Tried to update: " + c + " componet and it did not exist"), LoggerInfo.WARNING);
			return;
		}
		
		comp.update(t);
	}



	public void remove(Entity e) {
		
		entities.remove(e);
		
		for(Map<Integer, Componet> map : componets.values()) {
			
			while(map.containsKey(e.id)) {
				
				map.remove(e.id);
				
			}
			
		}
		
		Controller.currentScene.phys.remove(e);
		
	}
	
	public void remove(int id) {
		
		for(Entity e : entities) {
			
			if(e.id == id) {
				
				remove(e);
				return;
				
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
		try {
			return componets.get(c).containsKey(id);
		} catch(Exception e) {
			return false;
		}
		
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
			Size sz = (Size) get(e, Size.class);
			
			if( (p.x >= 0 - sz.x && p.x <= s.x + sz.x) && (p.y >= 0 - sz.y && p.y <= s.y + sz.y)) {
				visible.add(e);
			}
			
		}
		
		return visible;
		
	}



	public List<Entity> getAll() {
		return entities;
	}



	public void provokeListerners(List<Event> events) {
		
		Map<Integer, Componet> listeners = componets.get(Listener.class);
		
		if(listeners == null || listeners.isEmpty()) {return;}
		
		for(int key : listeners.keySet()) {
			
			Listener l = (Listener) listeners.get(key);
			
			for(Event e : events) {
				
				if(l.check(e)) {
					
					l.script.apply(new EventWrapper(e, key, this));
					
					break;
					
				}
				
			}
			
		}
		
	}
	
}
