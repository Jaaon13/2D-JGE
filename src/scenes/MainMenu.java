package scenes;

import java.awt.Point;
import java.util.List;

import board.BoardGenerator;
import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.Entity;
import graphical.rendering.fonts.Text;
import graphical.rendering.fonts.TextFactory;
import graphical.rendering.fonts.TextFactory.Alignment;
import sceneManagment.Event;
import sceneManagment.Scene;
import sceneManagment.Event.type;

public class MainMenu extends Scene {

	// Main difficulty buttons
	private Entity[] buttons = new Entity[4];
	
	public MainMenu() {
		
	}
	
	@Override
	public void update() {
		
		if(Controller.debug.avgFps > 60) {
			
			for(int x = 0; x < 100; x++) {
				new Entity(List.of(
						Controller.componets.new Pos(-100, 100),
						Controller.componets.new Size(300, 100),
						Controller.componets.new TextureC("\\src\\textures\\Rectangle")
						), entities);
			}
			
		}
		
		for(Event e : events) {
			
			if(e.type == type.UI_MouseLClick && e.altType == type.UI_MousePress) {
				
				Point click = e.PointVal;
				
				checkIfCollied(click);
				
			}
			
		}
		
		this.text.getFirst().data = "Number of Entities: " + entities.getAll().size();
		
	}

	private void checkIfCollied(Point click) {
		
		for(int x = 0; x < 4; x++) {
			
			Pos p = (Pos) entities.get(buttons[x], Pos.class);
			Size s = (Size) entities.get(buttons[x], Size.class);
			
			if( (click.x >= p.x && click.x <= p.x + s.x) && (click.y >= p.y && p.y + s.y >= click.y) ) {
				
				switch(x) {
				
				case 0:
					easyClick();
					return;
					
				case 1:
					mediumClick();
					return;
					
				case 2:
					expertClick();
					return;
					
				case 3:
					customClick();
					return;
				
				}
				
			}
			
		}
		
	}

	private void easyClick() {
		System.out.println("easy clicked");
		
		BoardGenerator.dPrintBoard(BoardGenerator.generateBoard(10, 40, 40));;
		
		new Entity(List.of(
				Controller.componets.new Pos(100, 100),
				Controller.componets.new Size(16, 16),
				Controller.componets.new TextureC("\\src\\textures\\MS", "1")
				), entities);
		
	}

	private void mediumClick() {
		System.out.println("medium clicked");
		
	}

	private void expertClick() {
		System.out.println("expert clicked");
		
	}

	private void customClick() {
		System.out.println("custom clicked");
		
	}

	@Override
	public void switchedTo() {
		
		this.text.add(new Text("Number of Entities: " + entities.getVisible().size(), new Point(400, 0), Alignment.CENTER));
		
		entities.clear();
		
		buttons[0] = new Entity(List.of(
				Controller.componets.new Pos(250, 100),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Easy Mode", new Point(400, 150 - 5), Alignment.CENTER));
		
		buttons[1] = new Entity(List.of(
				Controller.componets.new Pos(250, 250),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Intermediate Mode", new Point(400, 300 - 5), Alignment.CENTER));
		
		buttons[2] = new Entity(List.of(
				Controller.componets.new Pos(250, 400),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Expert Mode", new Point(400, 450 - 5), Alignment.CENTER));
		
		buttons[3] = new Entity(List.of(
				Controller.componets.new Pos(250, 550),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Custom Mode", new Point(400, 600 - 5), Alignment.CENTER));
		
		for(Entity e : buttons) {
			
			System.out.println(e.id);
			
		}
		
	}

	@Override
	public void fixedUpdate() {}
	
	@Override
	public void kill() {}

}
