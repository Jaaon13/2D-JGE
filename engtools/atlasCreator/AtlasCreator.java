package atlasCreator;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import assets.Region;
import assets.Texture;
import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import fileManager.SaveFile;
import graphical.userInput.AltKeys;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class AtlasCreator extends Scene {

	boolean firstRun = true;
	
	File selectedImagePath;
	
	Point leftclick = new Point(0, 0);
	
	String path = "";
	
	String highlight = "\\engtools\\atlasCreator\\highlight.png";
	Texture selected = null;
	
	Entity image;
	
	public void update() {
		
		if(firstRun) {
			
			firstRun = false;
			
			findFile();
				
			path = selectedImagePath.getPath();
			
			System.out.println(path);
				
			selected = (Texture) Controller.assets.load(path);
			
			if(Objects.isNull(selected)) {return;}
				
			image = new Entity(List.of(
					new Pos(defaultSizeX, defaultSizeY),
					new Size(selected.size.x, selected.size.y),
					new TextureC(path)
					), entities);
				
			Controller.graphics.setScreenSize(new Point(selected.size.x + defaultSizeX, selected.size.y + defaultSizeY));
			
		}
		
		for(Event e : events) {
			
			gridSelection(e);
			
		}
		
	}
	
	Set<Point> grid = new HashSet<>();
	
	List<Region> regions = new ArrayList<>();
	
	final int defaultSizeX = 16, defaultSizeY = 16;
 
	private void gridSelection(Event e) {
		
		if(e.type == type.UI_KeyPress) {
			
			keyInput(e.CharVal);
			
		}
		
		if(e.type == type.UI_MouseLClick && e.altType == type.UI_MousePress) {
			
			mouseInput(e);
			
		}
		
	}
	
	private void mouseInput(Event e) {
		boolean exists = false;
		
		int x = (e.PointVal.x / defaultSizeX) * defaultSizeX;
		int y = ((e.PointVal.y / defaultSizeY) * defaultSizeY) + defaultSizeY;
		
		for(Entity en : entities.getVisible()) {
			
			Pos pos = (Pos) entities.get(en, Pos.class);
			
			if(pos.x == x && pos.y == y) {
			
				exists = true;
				entities.remove(en);
				break;
				
			}
			
		}
		
		Point gridPos = new Point(
				(e.PointVal.x / defaultSizeX) * defaultSizeX,
				(e.PointVal.y / defaultSizeY) * defaultSizeY
				);
		
		if(!exists) {
			
			new Entity(List.of(
					new Pos(x, y),
					new Size(defaultSizeX, defaultSizeY),
					new TextureC(highlight)
					), entities);
			
			grid.add(gridPos);
			
		} else {
			
			grid.remove(gridPos);
			
		}
		
	}
	
	private void keyInput(char c) {
		switch(c) {
		
		case AltKeys.ENTER:
				
			Region r = new Region(grid);
			
			if(r.tl.x != -1) {
				regions.add(new Region(grid));
			}
				
			grid.clear();
			
			entities.clear();
			
			if(Objects.nonNull(image)) {
				entities.add(image);
			}
			
			break;
			
		case AltKeys.ESCAPE:
			
			SaveFile f = new SaveFile(path);
			
			for(Region region : regions) {
				
				f.writeString(region.tl.x + "," + region.tl.y + ":" + region.br.x + "," + region.br.y + "\n");
				
			}
			
			break;
	
	}
	}

	private void findFile() {
		
		try {
			
			File dirToOpen = new File(System.getProperty("user.dir"));
			
			JFileChooser jf = new JFileChooser(dirToOpen);
			
			JFrame temp = new JFrame();
			
			jf.showOpenDialog(temp);
			
			selectedImagePath = jf.getSelectedFile();
			
			temp.dispose();
			
		} catch(Exception e) {
			
			System.out.println(e);
			
		}
		
	}

	@Override
	public void fixedUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchedTo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}
	
}
