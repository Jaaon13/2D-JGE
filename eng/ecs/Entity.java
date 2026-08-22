package ecs;

import java.util.List;

import controller.Controller;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.Pos;
import physics.Vector;
import sceneManagment.World;

public class Entity {
	
	// Can be used as a kind of pointer
	public final int id;

	// Does not require manual addition to the Entity Manager with this route
	public Entity(List<Componet> cpnets, World world) {
		
		id = Controller.assets.genEntityID();
		
		for(Componet c : cpnets) {
			
			world.ecs.addComponet(id, c);
			
		}
		
		world.container.add(this);
		
	}
	
	public Entity(List<Componet> cpnets, World world, boolean isTemporary) {
		
		if(!isTemporary) {
			id = Controller.assets.genEntityID();
		} else {
			id = Controller.assets.genTempID();
		}
		
		for(Componet c : cpnets) {
			
			world.ecs.addComponet(id, c);
			
		}
		
		world.container.add(this);
		
		if(world.ecs.contains(this, MoveableObj.class)) {
			Pos p = world.ecs.get(this, Pos.class);
			((MoveableObj)world.ecs.get(this, MoveableObj.class)).position = new Vector(p.x, p.y);
		}
		
	}
	
}
