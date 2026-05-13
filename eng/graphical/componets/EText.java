package graphical.componets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import controller.Controller;

public abstract class EText extends ESprite {
	
	private String data;
	
	public List<ESprite> sprites = new ArrayList<>();
	
	public EText(String data) {
		updateData(data);
	}
	
	public void updateData(String data) {
		this.data = data;
		generateSprites();
	}
	public String getData() {
		return this.data;
	}

	private void generateSprites() {
		
		sprites.clear();
		
		int inc = 0;
		int yoff = 0;
		
		for(char c : data.toCharArray()) {
			
			int x = getPos().x + (getSize().x * inc);
			int y = Controller.globals.screenSize.y - (getPos().y + (getSize().y * yoff));
			
			EngSprite s = new EngSprite();
			s.setPos(new Point(x, y));
			s.setSize(getSize());
			s.setTags(getTags());
			
			Point tl = Controller.globals.defaultFont.atlas.section.get("" + c);
			
			if(Objects.isNull(tl)) {
				System.out.println("ERROR!! VALUE: " + c);
			}
			
			s.setAtlas(tl);
			s.setTexture(Controller.globals.defaultFont);
			
			sprites.add(s);
			
			inc++;
			
			if((inc * getSize().x) + getPos().x >= Controller.globals.screenSize.x) {
				yoff++;
				inc = 0;
			}
			
		}
		
	}

}
