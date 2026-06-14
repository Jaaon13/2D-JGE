package gui.factorys;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;

import ecs.EngineComponets.Depth;
import ecs.EngineComponets.Listener;
import ecs.EngineComponets.Listener.EventWrapper;
import ecs.EngineComponets.PlainShape;
import ecs.EngineComponets.PlainShape.Shape;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.Entity;
import ecs.EntityManager;
import sceneManagment.Event;
import sceneManagment.Event.type;

public class ButtonFactory {
	
	// Allows depth input and a custom listener script // Dangerous!
	public static void CreateButton(String data, TextFactory.Alignment a, Point pos, Point size, int depth,
			Function<EventWrapper, Boolean> script, Event.type event, EntityManager em) {
		
		create(data, a, pos, size, depth, script, event, em);
		
	}
	
	// Allows a custom listener script // Dangerous!
	public static void CreateButton(String data, TextFactory.Alignment a, Point pos, Point size, 
			Function<EventWrapper, Boolean> script, Event.type event, EntityManager em) {
		
		create(data, a, pos, size, Integer.MAX_VALUE, script, event, em);
		
	}
	
	// Allows a custom depth input
	public static void CreateButton(String data, TextFactory.Alignment a, Point pos, Point size, int depth, 
			Runnable r, EntityManager em) {
		
		createf(data, a, pos, size, depth, r, type.UI_MouseLClick, em);
		
	}
	
	// Basic call
	public static void CreateButton(String data, TextFactory.Alignment a, Point pos, Point size, 
			Runnable r, EntityManager em) {
		
		createf(data, a, pos, size, Integer.MAX_VALUE, r, type.UI_MouseLClick, em);
		
	}
	
	// Needed for the default script to be created for the user
	private static void createf(String data, TextFactory.Alignment a, Point pos, Point size, int depth, 
			Runnable r, Event.type event, EntityManager em) {
		
		Function<EventWrapper, Boolean> func = (EventWrapper wrapper) -> {
			
			if(wrapper.e.altType != type.UI_MousePress) {
				return false;
			}
			
			Pos pos2 = (Pos) wrapper.em.get(wrapper.EntityId, Pos.class);
			Size size2 = (Size) wrapper.em.get(wrapper.EntityId, Size.class);
			
			Point click = wrapper.e.PointVal;
			
			if((pos2.x < click.x && click.x < pos2.x + size2.x) && (pos2.y < click.y && click.y < pos2.y + size2.y)) {
				
				r.run();
				
			}
			
			return true;
		};
		
		create(data, a, pos, size, depth, func, event, em);
		
	}
	
	// Creates all the entities
	private static void create(String data, TextFactory.Alignment a, Point pos, Point size, int depth, 
			Function<EventWrapper, Boolean> script, Event.type event, EntityManager em) {
		
		new Entity(List.of(
				new Pos(pos.x, pos.y),
				new Size(size.x, size.y),
				new Depth(depth),
				new Listener(event, script),
				new PlainShape(255, 255, 255, Shape.RECTANGLE)
				), em);
		
		TextFactory.generateText(data, pos, depth, a, em, false);
		
	}

}
