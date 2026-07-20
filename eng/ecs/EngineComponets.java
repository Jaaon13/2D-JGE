package ecs;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;

import assets.Atlas;
import assets.Texture;
import controller.Controller;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.Pos;
import logger.Logger.LoggerInfo;
import physics.Vector;
import sceneManagment.Event;

public class EngineComponets {
	
	public static <T> void updateErr(T t, Class<? extends Componet> c) {
		Controller.logger.log(List.of("Tried to use an invalid type to update a " + c + " component!",
				"Generic class: " + t.getClass()), LoggerInfo.WARNING);
	}

	public static class Pos extends Componet {

		public int x, y;
		
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		protected <T> void update(T t) {
			if(t instanceof Point) {
				
				this.x = ((Point) t).x;
				this.y = ((Point) t).y;
				
			} else if(t instanceof Pos) {
				
				this.x = ((Pos) t).x;
				this.y = ((Pos) t).y;
				
			} else {
				updateErr(t, Pos.class);
			}
		}
		
	}
	
	public static class Size extends Componet {

		public int x, y;
		
		public Size(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		protected <T> void update(T t) {
			if(t instanceof Point) {
				
				this.x = ((Point) t).x;
				this.y = ((Point) t).y;
				
			} else if(t instanceof Size) {
				
				this.x = ((Size) t).x;
				this.y = ((Size) t).y;
				
			} else {
				updateErr(t, Size.class);
			}
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

		@Override
		protected <T> void update(T t) {
			if(t instanceof Integer) {
				this.depth = (int) t;
			} else {
				updateErr(t, Size.class);
			}
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

		@Override
		protected <T> void update(T t) {
			updateErr(t, Listener.class);
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

		@Override
		protected <T> void update(T t) {
			if(t instanceof TextureC) {
				this.texture = ((TextureC) t).texture;
				this.atlas = ((TextureC) t).atlas;
				this.TextureID = ((TextureC) t).TextureID;
			} else {
				updateErr(t, TextureC.class);
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

		@Override
		protected <T> void update(T t) {
			updateErr(t, PlainShape.class);
		}
		
	}
	
	public static class Collision extends Componet {

		public static abstract class CollisionStrat {
			
			public abstract void collision(Entity me, Entity other, EntityManager ecs, float delta);
			
		}
		
		public static class AvoidStrat extends CollisionStrat {

			@Override
			public void collision(Entity me, Entity other, EntityManager ecs, float delta) {
				
				MoveableObj mo = ecs.get(me, MoveableObj.class);
				
				mo.position.x -= mo.velocity.x * delta;
				mo.position.y -= mo.velocity.y * delta;
				
				ecs.update(me, Pos.class, new Point((int)mo.position.x, (int)mo.position.y));
				
			}
			
		}
		
		public static enum shape {
			
			COL_SQRUARE,
			
		}
		
		public static enum movement {
			
			MOV_STATIC,
			MOV_DYNAMIC
			
		}
		
		public Collision.shape shape;
		public Collision.movement movement;
		
		public int size;
		public Point offset;
		
		public CollisionStrat strat;
		
		public Collision(Collision.shape s, Collision.movement m, int size, Point posOffset, CollisionStrat strat) {
			construct(s, m, size, posOffset, strat);
		}
		
		// The size variable will either be the radius or square side length,
		// the posOffset is the position from the top left of the entity
		public Collision(Collision.shape s, Collision.movement m, int size, Point posOffset) {
			construct(s, m, size, posOffset, new AvoidStrat());
		}
		
		private void construct(Collision.shape s, Collision.movement m, int size, Point posOffset, CollisionStrat strat) {
			this.shape = s;
			this.movement = m;
			this.size = size;
			this.offset = posOffset;
			this.strat = strat;
			
		}

		@Override
		protected <T> void update(T t) {
			
			if(t instanceof shape) {
				shape = (Collision.shape) t;
			} else if(t instanceof movement) {
				movement = (Collision.movement) t;
			} else if(t instanceof Integer) {
				size = (int) t;
			} else if(t instanceof Point) {
				offset = (Point) t;
			} else {
				updateErr(t, Collision.class);
			}
			
		}
	}
	
	public static class MoveableObj extends Componet {
		
		// This is supposed to be explicitly changed by anyone
		public Vector direction = new Vector(), velocity = new Vector(), position = new Vector();
		public int maxSpeed, acceleration, friction;
		
		public MoveableObj(int max, int accel, int frict) {
			this.maxSpeed = max;
			this.acceleration = accel;
			this.friction = frict;
		}

		@Override
		protected <T> void update(T t) {
			updateErr(t, MoveableObj.class);
		}
		
	}
}
