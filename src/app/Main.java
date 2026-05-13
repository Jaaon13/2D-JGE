package app;

import controller.Controller;
import scenes.MainMenu;

public class Main {
	
	public static final MainMenu menu = new MainMenu();

	public static void main(String[] args) {
		
		Controller.scenes.setScene(Controller.scenes.addScene(menu));
		
		Controller.start("Mine Sweeper", 800, 800);

	}

}
