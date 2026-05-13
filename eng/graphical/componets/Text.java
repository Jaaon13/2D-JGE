package graphical.componets;

import java.awt.Point;
import java.util.List;

public abstract class Text {
	
	// the top left starting point
	public Point tl;
	
	// Should return every current character as a sprite to be drawn
	public abstract List<ESprite> getCharacters();

}
