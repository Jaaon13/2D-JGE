package scenes;

import java.awt.Point;

import board.BoardGenerator;
import controller.Controller;
import gui.factorys.ButtonFactory;
import gui.factorys.Text;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Scene;

public class MainMenu extends Scene {
	
	public MainMenu() {
		
	}
	
	@Override
	public void update() {
		
		
	}

	private void easyClick() {
		Game newGame = new Game(BoardGenerator.generateBoard(9, 9, 10), 10, this.id);
		Controller.scenes.setScene(Controller.scenes.addScene(newGame));
	}

	private void mediumClick() {
		Game newGame = new Game(BoardGenerator.generateBoard(16, 16, 40), 40, this.id);
		Controller.scenes.setScene(Controller.scenes.addScene(newGame));
	}

	private void expertClick() {
		Game newGame = new Game(BoardGenerator.generateBoard(16, 30, 99), 99, this.id);
		Controller.scenes.setScene(Controller.scenes.addScene(newGame));
	}

	private void customClick() {
		Game newGame = new Game(BoardGenerator.generateBoard(80, 155, 4000), 4000, this.id);
		Controller.scenes.setScene(Controller.scenes.addScene(newGame));
	}

	@Override
	public void switchedTo() {
		
		entities.clear();
		
		Point size = new Point(300, 100);
		String text = "\\src\\textures\\Rectangle";
		
		ButtonFactory.createButton("Easy Mode", new Point(250, 50), size, 0, text, (() -> {easyClick();}), entities);
		
		ButtonFactory.createButton("Intermediate Mode", new Point(250, 200), size, 0, text, (() -> {mediumClick();}), entities);
		
		ButtonFactory.createButton("Expert Mode", new Point(250, 350), size, 0, text, (() -> {expertClick();}), entities);
		
		//ButtonFactory.createButton("Custom Mode", new Point(250, 500), size, 0, text, (() -> {customClick();}), entities);
		
		ButtonFactory.createButton("Quit", new Point(250, 650), size, 0, text, (() -> {Controller.close();}), entities);
		
	}

	@Override
	public void fixedUpdate() {}
	
	@Override
	public void kill() {}

}
