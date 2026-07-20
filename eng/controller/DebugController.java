package controller;

import graphical.userInput.AltKeys;

public class DebugController {
	
	public boolean fps = false;
	
	public boolean hitBoxes = false;
	
	public long avgFps = 0;
	
	// Runs a debug command if a certain input is pressed
	/*
	 *  F10 is the atlas creator tool shortcut
	 */
	public boolean parseCommands(char key) {
			
		switch(key) {
		
		case AltKeys.F1:
			fps = !fps;
			
			if(fps) {
				Controller.createFPS();
			} else {
				Controller.removeFPS();
			}
			
			return true;
			
		case AltKeys.F10:
			hitBoxes = !hitBoxes;
			return true;
				
		case '`':
			System.out.println("Terminal opening...");
			
			return true;
			
		}
		
		
		return false;
			
	}

}
