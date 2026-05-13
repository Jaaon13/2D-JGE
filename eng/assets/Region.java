package assets;

import java.awt.Point;
import java.util.Set;

/*
 * 
 * Requires the shape selected to be a rectangle
 * 
 */

public class Region {
	
	public Point tl = new Point(-1, -1), br;
	
	public String name = "";

	public Region(Set<Point> pos) {
		
		setMinMax(pos);
		
		System.out.println(tl +" | " + br);
		
	}
	
	public Region(Point tl, Point br, String name) {
		this.tl = tl;
		this.br = br;
		this.name = name;
	}
	
	private void setMinMax(Set<Point> pos) {
		
		for(Point p : pos) {
			
			if(tl.x == -1) {
				
				tl = (Point) p.clone();
				br = (Point) p.clone();
				
			}
			
			if(p.x < tl.x) {
				
				tl.x = p.x;
				
			} else if(p.y < tl.y) {
				
				tl.y = p.y;
				
			}
			
			if(p.x > br.x) {
				
				br.x = p.x;
				
			} else if(p.y > br.y) {
				
				br.y = p.y;
				
			}
			
		}
		
	}
	
}
