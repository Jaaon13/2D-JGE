package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ecs.Entity;
import graphical.rendering.Renderer;
import graphical.rendering.fonts.Text;
import logger.Logger.LoggerInfo;

public class RenderController {
	
	private Map<Integer, Renderer> renderImplementations = new HashMap<>();
	private int incrementalId = 0;
	
	private int curRenderer = -1;
	
	public int addRenderer(Renderer r) {
		
		r.initalize();
		
		renderImplementations.put(incrementalId, r);
		incrementalId++;
		
		return incrementalId - 1;
		
	}
	
	public void setText(List<Text> text) {
		renderImplementations.get(curRenderer).text = text;
	}
	
	public void setRenderer(int id) {
		
		Controller.logger.log("Renderer set to ID of: " + id, LoggerInfo.INFO);
		
		curRenderer = id;
		
		renderImplementations.get(curRenderer).windowResized();
	}
	
	public void addEntities(List<Entity> toDraw) {
		renderImplementations.get(curRenderer).addEntities(toDraw);
	}
	
	public void render() {
		
		if(curRenderer == -1) {
			System.out.println("NO RENDERER!");
			return;
		}
		
		renderImplementations.get(curRenderer).render();
		
	}
	
	public int getDrawCalls() {
		return renderImplementations.get(curRenderer).drawCalls;
	}
	
	public int getTotalSpritesDrawn() {
		return renderImplementations.get(curRenderer).trianglesDrawn;
	}

	public void windowResized() {
		renderImplementations.get(curRenderer).windowResized();
	}

}
