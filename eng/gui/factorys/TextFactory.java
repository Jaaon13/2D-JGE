package gui.factorys;

import java.awt.Point;
import java.util.List;

import ecs.EngineComponets.Depth;
import ecs.EngineComponets.Depth.Layer;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import ecs.EntityManager;

public class TextFactory {

	public static enum Alignment {
		
		LEFT,
		CENTER,
		RIGHT
		
	}
	
	private static final Point defaultTextSize = new Point(6, 10);
	
	public static void generateText(String data, Point pos, Alignment a, EntityManager em, boolean isTemporary) {
		
		genText(data, pos, Integer.MAX_VALUE, a, em, isTemporary);
		
	}
	
	public static void generateText(String data, Point pos, int depth, Alignment a, EntityManager em, boolean isTemporary) {
		
		genText(data, pos, depth, a, em, isTemporary);
		
	}
	
	private static void genText(String data, Point pos, int depth, Alignment a, EntityManager em, boolean isTemporary) {
		
		Point start;
		
		switch(a) {
		
		case LEFT: // Do nothing case
			start = new Point(pos);
			break;
		
		case CENTER:
			int l = data.length() * defaultTextSize.x;
			start = new Point(pos.x - (l / 2), pos.y);
			break;
			
		case RIGHT:
			int l2 = data.length() * defaultTextSize.x;
			start = new Point(pos.x - l2, pos.y);
			break;
			
		default: // Edge case
			return;
		
		}
		
		int offset = 0;
		
		for(char s : data.toCharArray()) {
			
			new Entity(List.of( 
					new Pos(start.x + offset, start.y), 
					new Size(defaultTextSize.x, defaultTextSize.y),
					new TextureC("fonts\\minogram_6x10", s + ""),
					new Depth(Layer.GUI)
					), em, isTemporary);
			
			offset += defaultTextSize.x;
			
		}
		
	}
	
}
