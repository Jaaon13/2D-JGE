package controller;

import graphical.userInput.AltKeys;

public class DebugController {
	
	public boolean fps = true;
	
	public long avgFps = 0;
	
	// Runs a debug command if a certain input is pressed
	/*
	 *  F10 is the atlas creator tool shortcut
	 */
	public boolean parseCommands(char key) {
			
		switch(key) {
		
		case AltKeys.F1:
			fps = true;
			return true;
			
		case AltKeys.F10:
				
			int id = Controller.scenes.addScene(new atlasCreator.AtlasCreator());
			
			Controller.scenes.setScene(id);
				
			return true;
				
		case '`':
			System.out.println("Terminal opening...");
			
			return true;
			
		}
		
		
		return false;
			
	}

}
