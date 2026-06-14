package ecs;

import java.awt.Point;
import java.util.function.Function;

import assets.Atlas;
import assets.Texture;
import controller.Controller;
import logger.Logger.LoggerInfo;
import sceneManagment.Event;

public class EngineComponets {

	public static class Pos extends Componet {

		public int x, y;
		
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	public static class Size extends Componet {

		public int x, y;
		
		public Size(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}
	
	public static class Depth extends Componet {
		
		public enum Layer {
			
			GUI,
			CENTER,
			BACKGROUND
			
		}
		
		public int depth;
		
		public Depth(Layer l) {
			
			switch(l) {
			
			case BACKGROUND:
				this.depth = Integer.MIN_VALUE;
				break;
				
			case CENTER:
				this.depth = 0;
				break;
				
			case GUI:
				this.depth = Integer.MAX_VALUE;
				break;
				
			default:
				this.depth = 0;
				break;
			
			}
			
		}
		
		public Depth(int d) {
			this.depth = d;
		}
		
		public Depth() {
			this.depth = 0;
		}
		
	}
	
	public static class Listener extends Componet {
		
		private Event.type eventListener;
		public Function<EventWrapper, Boolean> script;
		
		public static class EventWrapper {
			
			public Event e;
			public int EntityId;
			
			// Read Only Please God
			public final EntityManager em;
			
			public EventWrapper(Event e, int id, EntityManager em) {
				this.e = e;
				this.EntityId = id;
				this.em = em;
			}
			
		}
		
		public Listener(Event.type event, Function<EventWrapper, Boolean> f) {
			
			this.eventListener = event;
			this.script = f;
			
		}
		
		public boolean check(Event event) {
			return (eventListener == event.type);
		}
		
	}
	
	public static class TextureC extends Componet {
		
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
	
	
	public static class PlainShape extends Componet {

		
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
