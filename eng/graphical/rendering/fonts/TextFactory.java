package graphical.rendering.fonts;

import java.awt.Point;
import java.util.List;

import controller.Controller;
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
					Controller.componets.new Pos(start.x + offset, start.y), 
					Controller.componets.new Size(defaultTextSize.x, defaultTextSize.y),
					Controller.componets.new TextureC("\\eng\\graphical\\rendering\\fonts\\minogram_6x10", s + "")
					), em, isTemporary);
			
			offset += defaultTextSize.x;
			
		}
		
	}
	
}
