package defaultScene;

import java.awt.Point;

import controller.Controller;
import graphical.rendering.fonts.Text;
import graphical.rendering.fonts.TextFactory.Alignment;
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
		
		text.add(new Text("Loading...", new Point(Controller.globals.screenSize.x / 2, Controller.globals.screenSize.y / 2), Alignment.CENTER));
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
