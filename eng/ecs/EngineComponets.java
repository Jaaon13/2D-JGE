package ecs;

import java.awt.Point;

import assets.Atlas;
import assets.Texture;
import controller.Controller;
import logger.Logger.LoggerInfo;

public class EngineComponets {

	public class Pos extends Componet {

		@Override
		public String getName() {
			return "Pos";
		}

		public int x, y;
		
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	public class Size extends Componet {

		@Override
		public String getName() {
			return "Size";
		}

		public int x, y;
		
		public Size(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	public class TextureC extends Componet {

		@Override
		public String getName() {
			return "TextureC";
		}
		
		public Texture texture;
		public Point atlas;
		
		public int TextureID;
		
		public TextureC(String file) {
			genTexture(file, "");
		}
		
		public TextureC(String file, String atlas) {
			genTexture(file, atlas);
		}
		
		private void genTexture(String file, String atlas) {
			
			if(file.contains(".")) {
				
				String parsed = file.substring(file.indexOf(".") + 1);
				
				switch(parsed) {
				
				case "png":
					this.texture = (Texture) Controller.assets.load(file);
					break;
					
				case "atlas":
					if(this.texture != null) {
						this.texture.atlas = (Atlas) Controller.assets.load(file);
					}
				
					default:
						Controller.logger.log("Tried to generate a texture without a proper file ending : " + parsed, LoggerInfo.ERROR);
						return;
					
				}
				
				return;
				
			}
			
			this.texture = (Texture) Controller.assets.load(file + ".png");
			this.texture.atlas = (!atlas.isEmpty()) ? (Atlas) Controller.assets.load(file + ".atlas") : null;
			this.TextureID = texture.getRawID();
			
			if(texture.atlas != null && !atlas.isEmpty()) {
				
				this.atlas = texture.atlas.section.get(atlas);
				
			}
			
		}
		
	}
	
	
	public class PlainShape extends Componet {

		@Override
		public String getName() {
			return "PlainShape";
		}
		
		public enum Shape {
			
			RECTANGLE,
			CORNERED_RECTANGLE,
			CIRCLE,
			
		}
		
		public int[] color = new int[3];
		public Shape shape;
		
		public PlainShape(int r, int g, int b, Shape s) {
			this.color[0] = r;
			this.color[1] = g;
			this.color[2] = b;
			
			this.shape = s;
		}
		
	}
}
