package ecs;

import java.util.List;

import controller.Controller;

public class Entity {
	
	// Can be used as a kind of pointer
	public final int id;

	// Does not require manual addition to the Entity Manager with this route
	public Entity(List<Componet> cpnets, EntityManager entities) {
		
		id = Controller.assets.genEntityID();
		
		entities.add(this);
		
		for(Componet c : cpnets) {
			
			entities.addComponet(id, c);
			
		}
		
	}
	
	public Entity(List<Componet> cpnets, EntityManager entities, boolean isTemporary) {
		
		if(!isTemporary) {
			id = Controller.assets.genEntityID();
		} else {
			id = Controller.assets.genTempID();
		}
		
		entities.add(this);
		
		for(Componet c : cpnets) {
			
			entities.addComponet(id, c);
			
		}
		
	}
	
}
