package scenes;

import java.awt.Point;
import java.util.List;

import board.BoardGenerator;
import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.Entity;
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
		
		for(Event e : events) {
			
			if(e.type == type.UI_MouseLClick && e.altType == type.UI_MousePress) {
				
				Point click = e.PointVal;
				
				checkIfCollied(click);
				
			}
			
		}
		
	}

	private void checkIfCollied(Point click) {
		
		for(int x = 0; x < 4; x++) {
			
			Pos p = (Pos) buttons[x].componets.get("Pos");
			Size s = (Size) buttons[x].componets.get("Size");
			
			if( (click.x >= p.x && click.x <= p.x + s.x) && (click.y >= p.y - 100 && p.y + s.y - 100 >= click.y) ) {
				
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
		
		entities.clear();
		
		buttons[0] = new Entity(List.of(
				Controller.componets.new Pos(250, 200),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				));
		
		List<Entity> button1Text = TextFactory.generateText("Easy Mode", new Point(400, 160), Alignment.CENTER);
		
		buttons[1] = new Entity(List.of(
				Controller.componets.new Pos(250, 350),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				));
		
		List<Entity> button2Text = TextFactory.generateText("Intermediate Mode", new Point(400, 310), Alignment.CENTER);
		
		buttons[2] = new Entity(List.of(
				Controller.componets.new Pos(250, 500),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				));
		
		List<Entity> button3Text = TextFactory.generateText("Expert Mode", new Point(400, 460), Alignment.CENTER);
		
		buttons[3] = new Entity(List.of(
				Controller.componets.new Pos(250, 650),
				Controller.componets.new Size(300, 100),
				Controller.componets.new TextureC("\\src\\textures\\Rectangle")
				));
		
		List<Entity> button4Text = TextFactory.generateText("Custom Mode", new Point(400, 610), Alignment.CENTER);
		
		entities.add(buttons[0]);
		
		for(Entity e : button1Text) {
			entities.add(e);
		}
		
		entities.add(buttons[1]);
		
		for(Entity e : button2Text) {
			entities.add(e);
		}
		
		entities.add(buttons[2]);
		
		for(Entity e : button3Text) {
			entities.add(e);
		}
		
		entities.add(buttons[3]);
		
		for(Entity e : button4Text) {
			entities.add(e);
		}
		
	}

	@Override
	public void fixedUpdate() {}
	
	@Override
	public void kill() {}

}
