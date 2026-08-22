package ecs;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;

import assets.Atlas;
import assets.Texture;
import controller.Controller;
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
		
		public String toString() {
			return "Posistion: " + x + ", " + y;
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
		
		public String toString() {
			return "Size: " + x + ", " + y;
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
		
		public String toString() {
			return "Depth: " + depth;
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
		
		public String toString() {
			return "Listener: is listening for event type of, " + eventListener;
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
		
		public String toString() {
			return "Texture: " + "texture data: " + texture.filePath + " | " + texture.id + 
					((atlas != null) ? (" atlas location: " + atlas.x + ", " + atlas.y) : "");
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
		
		public String toString() {
			return "PlainShape: " + shape;
		}
		
	}
	
	public static class Collision extends Componet {

		public static abstract class CollisionStrat {
			
			public abstract void collision(Entity me, Entity other, EntityManager ecs, float delta);
			
		}
		
		public static class AvoidStrat extends CollisionStrat {

			@Override
			public void collision(Entity me, Entity other, EntityManager ecs, float delta) {
				
				Collision oc = Controller.currentScene.world.ecs.get(other, Collision.class);
				
				switch(oc.material) {
					case IS_SOLID:
						break;
					case NOT_SOLID:
						return;
				}
				
				Vector mycenter = getVector(me);
				Vector othercenter = getVector(other);
				
				Vector repel = new Vector(
						othercenter.x - mycenter.x,
						othercenter.y - mycenter.y
						);
				
				repel = repel.normalize().getInverse();
				
				MoveableObj mo = Controller.currentScene.world.ecs.get(me, MoveableObj.class);
				
				while(Controller.currentScene.world.phys.AABBcheck(me, other)) {
					
					if(Math.abs(repel.x) >= Math.abs(repel.y)) {
						mo.position.x += repel.x;
					} else {
						mo.position.y += repel.y;
					}
					
				}
				
			}
			
			private Vector getVector(Entity e) {
				
				Pos pos = Controller.currentScene.world.ecs.get(e, Pos.class);
				
				Vector r = (Controller.currentScene.world.ecs.contains(e, MoveableObj.class))
						? ((MoveableObj)(Controller.currentScene.world.ecs.get(e, MoveableObj.class))).position.copy()
								: new Vector(pos.x, pos.y);
				
				Size s = Controller.currentScene.world.ecs.get(e, Size.class);
				r.x += (float)s.x/2;
				r.y += (float)s.y/2;
				
				return r;
				
			}
			
		}
		
		public static class NullStrat extends CollisionStrat {
			public void collision(Entity me, Entity other, EntityManager ecs, float delta) {}
		}
		
		public enum shape {
			
			COL_SQRUARE,
			
		}
		
		public enum movement {
			
			MOV_STATIC,
			MOV_DYNAMIC
			
		}
		
		public enum material { 
			
			IS_SOLID,
			NOT_SOLID,
			
		}
		
		public Collision.shape shape;
		public Collision.movement movement;
		public Collision.material material;
		
		public int size;
		public Point offset;
		
		public CollisionStrat strat;
		
		public Collision(Collision.shape s, Collision.movement m, Collision.material c, int size, Point posOffset, CollisionStrat strat) {
			construct(s, m, c, size, posOffset, strat);
		}
		
		// The size variable will either be the radius or square side length,
		// the posOffset is the position from the top left of the entity
		public Collision(Collision.shape s, Collision.movement m, Collision.material c, int size, Point posOffset) {
			construct(s, m, c, size, posOffset, new AvoidStrat());
		}
		
		private void construct(Collision.shape s, Collision.movement m, Collision.material c, int size, Point posOffset, CollisionStrat strat) {
			this.shape = s;
			this.movement = m;
			this.size = size;
			this.offset = posOffset;
			if(this.movement != movement.MOV_STATIC) {
				this.strat = strat;
			} else {
				this.strat = new NullStrat();
			}
			this.material = c;
			
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
		
		public String toString() {
			return "Colllision: " + "strat class, " + strat.getClass() + " shape, " + shape + " movement: " 
					+ movement + " material, " + material; 
		}
	}
	
	public static class MoveableObj extends Componet {
		
		// This is supposed to be explicitly changed by anyone
		public Vector direction = new Vector(), velocity = new Vector(), position = new Vector(), lastPosition = new Vector();
		
		// Descriptors
		public int maxSpeed, acceleration, friction;
		public Triggers triggers;
		
		public MoveableObj(int max, int accel, int frict) {
			this.maxSpeed = max;
			this.acceleration = accel;
			this.friction = frict;
			this.triggers = new DefaultTriggers();
		}
		
		public MoveableObj(int max, int accel, int frict, Triggers newTriggers) {
			this.maxSpeed = max;
			this.acceleration = accel;
			this.friction = frict;
			this.triggers = newTriggers;
		}
		
		public static abstract class Triggers {
			
			public abstract void onStop(Entity e);
			
			public abstract void onMapLeave(Entity e);
			
		}
		
		public static class DefaultTriggers extends Triggers {

			@Override
			public void onStop(Entity e) {}

			@Override
			public void onMapLeave(Entity e) {}

		}

		@Override
		protected <T> void update(T t) {
			updateErr(t, MoveableObj.class);
		}
		
		public String toString() {
			String tor = "MoveableObj: data";
			
			tor += "\n\tDirection: " + direction.toString();
			tor += "\n\tVelocity: " + velocity.toString();
			tor += "\n\tPosition: " + position.toString();
			tor += "\n\tLast Position: " + lastPosition.toString();
			tor += "\n\tMax Speed: " + maxSpeed;
			tor += "\n\tAcceleration: " + acceleration;
			tor += "\n\tFriction: " + friction;
			
			return tor;
		}
		
	}
}
