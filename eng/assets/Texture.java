package assets;

import java.awt.Point;

public class Texture extends Asset {

	public Point size;

	public Atlas atlas;
	
	public void release() {
		
		
		
	}
	
	public Texture(Point s, int i, String filePath) {
		this.size = s;
		this.id = i;
		this.filePath = filePath;
	}

}
