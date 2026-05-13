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
import fileManager.SaveFile;
import graphical.componets.ESprite;
import graphical.componets.EngSprite;
import graphical.userInput.AltKeys;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class AtlasCreator extends Scene {

	boolean firstRun = true;
	
	File selectedImagePath;
	
	Point leftclick = new Point(0, 0);
	
	String path = "";
	
	Texture highlight = (Texture) Controller.assets.load("\\engtools\\atlasCreator\\highlight.png");
	Texture selected = null;
	
	EngSprite image;
	
	public void update() {
		
		if(firstRun) {
			
			firstRun = false;
			
			findFile();
				
			path = selectedImagePath.getPath();
				
			selected = (Texture) Controller.assets.load(path);
			
			if(Objects.isNull(selected)) {return;}
				
			image = new EngSprite();
			image.setPos(new Point(0, 0));
			image.setSize(selected.size);
			image.setTexture(selected);
			image.getTexture().atlas = null;
				
			objects.addObject(image);
				
			Controller.globals.screenSize = selected.size;
			
		}
		
		for(Event e : events) {
			
			gridSelection(e);
			
		}
		
	}
	
	Set<Point> grid = new HashSet<>();
	
	List<Region> regions = new ArrayList<>();
	
	final int defaultSizeX = 32, defaultSizeY = 32;
 
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
		int y = Controller.globals.screenSize.y - (((e.PointVal.y / defaultSizeY) * defaultSizeY) + defaultSizeY);
		
		System.out.println(Controller.globals.screenSize);
		
		for(ESprite o : objects.getAll()) {
			
			if(o.getPos().x == x && o.getPos().y == y) {
				
				exists = true;
				objects.deleteObject(o);
				break;
				
			}
			
		}
		
		Point gridPos = new Point(
				(e.PointVal.x / defaultSizeX) * defaultSizeX,
				(e.PointVal.y / defaultSizeY) * defaultSizeY
				);
		
		if(!exists) {
			
			EngSprite a = new EngSprite();
			a.setPos(new Point(x, y));
			a.setSize(new Point(defaultSizeX, defaultSizeY));
			a.setTexture(highlight);
			
			objects.addObject(a);
			
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
			
			objects.clear();
			
			if(Objects.nonNull(image)) {
				objects.addObject(image);
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
