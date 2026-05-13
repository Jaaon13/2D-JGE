package graphical.componets;

import java.awt.Point;

public class Camera {
	
	public Point pos;
	
	public Point viewPortSize;
	
	public float scale;
	
	public Camera(Point pos, Point viewPort, float scale) {
		
		this.pos = pos;
		this.viewPortSize = viewPort;
		this.scale = scale;
		
	}

}
