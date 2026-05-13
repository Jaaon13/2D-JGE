package defaultScene;

import java.awt.Point;

import controller.Controller;
import graphical.componets.ESprite;
import graphical.componets.EngText;
import sceneManagment.Scene;

public class EngineLoadingScene extends Scene {

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fixedUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchedTo() {
		
		EngText loadingText = new EngText("");
		loadingText.setPos(new Point((int) (Controller.globals.screenSize.x * 0.45), (int) (Controller.globals.screenSize.y * 0.45)));
		loadingText.setSize(new Point(6, 10));
		loadingText.updateData("Loading...");
		
		for(ESprite s : loadingText.sprites) {
			objects.addObject(s);
		}
		
		System.out.println(loadingText.getPos());
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
