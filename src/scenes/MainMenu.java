package scenes;

import java.awt.Point;
import java.util.List;

import board.BoardGenerator;
import controller.Controller;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import graphical.rendering.PainterRenderer;
import ecs.Entity;
import gui.factorys.Text;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

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
		
		int[][] bombs = BoardGenerator.generateBoard(10, 40, 40);
		
		Game newGame = new Game(bombs, 40);
		
		int newGameId = Controller.scenes.addScene(newGame);
		Controller.scenes.setScene(newGameId);
		
	}

	private void mediumClick() {
		
		Controller.scenes.setScene(Controller.scenes.addScene(new buttontest()));
		
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
				new Pos(250, 100),
				new Size(300, 100),
				new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Easy Mode", new Point(400, 150 - 5), Alignment.CENTER));
		
		buttons[1] = new Entity(List.of(
				new Pos(250, 250),
				new Size(300, 100),
				new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Intermediate Mode", new Point(400, 300 - 5), Alignment.CENTER));
		
		buttons[2] = new Entity(List.of(
				new Pos(250, 400),
				new Size(300, 100),
				new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Expert Mode", new Point(400, 450 - 5), Alignment.CENTER));
		
		buttons[3] = new Entity(List.of(
				new Pos(250, 550),
				new Size(300, 100),
				new TextureC("\\src\\textures\\Rectangle")
				), entities);
		
		this.text.add(new Text("Custom Mode", new Point(400, 600 - 5), Alignment.CENTER));
		
	}

	@Override
	public void fixedUpdate() {}
	
	@Override
	public void kill() {}

}
