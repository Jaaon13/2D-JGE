package scenes;

import java.awt.Point;
import java.util.List;

import controller.Controller;
import ecs.Componet;
import ecs.Entity;
import gui.factorys.ButtonFactory;
import gui.factorys.Text;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Scene;

public class buttontest extends Scene {

	@Override
	public void update() {
		
		this.text.getFirst().data = "Number of Entities: " + entities.getVisible().size();
		
	}

	@Override
	public void fixedUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchedTo() {
		
		this.text.add(new Text("", new Point(Controller.globals.screenSize.x / 2, 0), Alignment.CENTER));
		
		ButtonFactory.CreateButton("tst", Alignment.CENTER, new Point(50, 50), new Point(100, 100), ( () -> clicked() ), entities);
		
		List<Entity> es = entities.getVisible();
		
		for(Entity e : es) {
			
			System.out.println(e.id);
			
			for(Componet c : entities.getAllComponets(e)) {
				
				System.out.println("\t" + c.getClass());
				
			}
			
			System.out.println();
			
		}
		
	}

	private void clicked() {
		
		System.out.println("Clicked");
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
