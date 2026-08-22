package scenes;

import java.awt.Point;

import controller.Controller;
import gui.factorys.ButtonFactory;
import sceneManagment.Scene;

public class MainMenu extends Scene {
	
	@Override
	public void update() {
		
		
	}

	@Override
	public void fixedUpdate() {
		ButtonFactory.createButton("Start", new Point(250, 150), new Point(300, 100), 0, "bullethell\\Rectangle", (() -> start()), world);
	}

	private void start() {
		
		Controller.scenes.setScene(Controller.scenes.addScene(new TestScene()));
		
	}

	@Override
	public void switchedTo() {
		
		
	}

	@Override
	public void kill() {
		
		
	}
	
	

}
