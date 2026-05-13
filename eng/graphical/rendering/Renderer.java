package graphical.rendering;

import java.util.ArrayList;
import java.util.List;

import ecs.Entity;
import graphical.componets.ESprite;

public abstract class Renderer {
	
	public int trianglesDrawn = 0, drawCalls = 0;
	
	public List<Entity> entities = new ArrayList<>();
	
	public abstract void initalize();
	
	public abstract void render();
	
	public void addEntities(List<Entity> toDraw) {
		this.entities.addAll(toDraw);
	}

}
