package assets;

import java.awt.Point;

public class Texture extends Asset {

	public Point size;

	public Atlas atlas;
	
	public void release() {
		// TODO Add a removal tool
	}
	
	public Texture(Point s, int i, String filePath) {
		this.size = s;
		this.id = i;
		this.filePath = filePath;
	}

}
