package defaultScene;

import java.awt.Point;

import controller.Controller;
import gui.factorys.Text;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Scene;

public class EngineLoadingScene extends Scene {

	@Override
	public void update() {}

	@Override
	public void fixedUpdate() {}

	@Override
	public void switchedTo() {
		
		text.add(new Text("Loading...", new Point(Controller.globals.screenSize.x / 2, Controller.globals.screenSize.y / 2), Alignment.CENTER));
		
	}

	@Override
	public void kill() {}

}
