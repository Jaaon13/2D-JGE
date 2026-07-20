package app;

import controller.Controller;
import scenes.MainMenu;

public class BMain {

	public static void main(String[] args) {
		
		Controller.scenes.setScene(Controller.scenes.addScene(new MainMenu()));
		
		Controller.start("B hell", 800, 800);
		
	}
	
}
