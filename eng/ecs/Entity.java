package ecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;

public class Entity {
	
	public Map<String, Componet> componets = new HashMap<>();
	
	// Can be used as a kind of pointer
	public final int id = Controller.assets.genEntityID();

	public Entity(List<Componet> cpnets) {
		
		for(Componet c : cpnets) {
			
			componets.put(c.getName(), c);
			
		}
		
	}
	
	public boolean containsComponet(String s) {
		
		return componets.containsKey(s);
		
	}
	
	public boolean containsComponets(List<String> s) {
		
		int inc = 0;
		
		for(String str : s) {
			
			if(componets.containsKey(str)) {
				inc++;
			}
			
		}
		
		return inc == s.size();
		
	}
	
}
