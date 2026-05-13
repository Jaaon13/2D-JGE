package controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import graphical.componets.ESprite;

public class ObjectManager {
	
	private final int gridSize = 64;

	private Map<Point, List<ESprite>> objs = new HashMap<>();
	
	public void addObject(ESprite e) {
		
		Point newCord = new Point(e.getPos().x / gridSize, e.getPos().y / gridSize);
		
		if(Objects.nonNull(objs.get(newCord))) {
			
			objs.get(newCord).add(e);
			
		} else {
			
			objs.put(newCord, new ArrayList<>());
			objs.get(newCord).add(e);
			
		}
		
	}
	
	public void deleteObject(ESprite e) {
		
		Point newCord = new Point(e.getPos().x / gridSize, e.getPos().y / gridSize);
		
		if(Objects.nonNull(objs.get(newCord))) {
			
			objs.get(newCord).remove(e);
			
		}
		
	}
	
	public void deleteAll(List<ESprite> toDelete) {
		
		for(ESprite e : toDelete) {
			
			Point newCord = new Point(e.getPos().x / gridSize, e.getPos().y / gridSize);
			
			if(Objects.nonNull(objs.get(newCord))) {
				
				objs.get(newCord).remove(e);
				
			}
			
		}
		
	}
	
	public void clear() {
		
		for(List<ESprite> list : objs.values()) {
			list.clear();
		}
		
	}
	
	public List<ESprite> getAllInGrid(Point p) {
		
		Point newCord = new Point(p.x / gridSize, p.y / gridSize);
		
		if(Objects.nonNull(objs.get(newCord))) {
			
			return objs.get(newCord);
			
		} else {
			return null;
		}
		
	}
	
	public List<ESprite> getAllVisible() {
		
		Point topLeft = (Objects.isNull(Controller.globals.camera)) ? new Point(0, 0) : new Point(Controller.globals.camera.pos);
		Point screen = Controller.globals.screenSize;
		
		List<ESprite> toSend = new ArrayList<>();
		
		for(int y = 0; y < screen.y / gridSize; y++) {
			
			for(int x = 0; x < screen.x / gridSize; x++) {
				
				Point p = new Point(x, y);
				
				if(Objects.nonNull(objs.get(p))) {
					toSend.addAll(objs.get(p));
				}
				
			}
			
		}
		
		return toSend;
		
	}
	
	public List<ESprite> getAll() {
		
		List<ESprite> tosend = new ArrayList<>();
		
		for(List<ESprite> held : objs.values()) {
			tosend.addAll(held);
		}
		
		return tosend;
		
	}
	
}
