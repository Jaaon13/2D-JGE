package scenes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.Controller;
import ecs.EngineComponets.Collision;
import ecs.EngineComponets.Collision.CollisionStrat;
import ecs.EngineComponets.Collision.material;
import ecs.EngineComponets.Collision.movement;
import ecs.EngineComponets.Collision.shape;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.MoveableObj.Triggers;
import gui.factorys.TextFactory.Alignment;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import ecs.EntityManager;
import objs.NPC;
import objs.Player;
import physics.Vector;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;
import sceneManagment.World.Container.EntityType;

public class TestScene extends Scene {
	
	public Player player;

	public class BulletStrat extends CollisionStrat {
		
		private int parent;
		
		public BulletStrat(int id) {
			parent = id;
		}

		@Override
		public void collision(Entity me, Entity other, EntityManager ecs, float delta) {
			
			if(other.id == parent || ((Collision)(world.ecs.get(other, Collision.class))).material == material.NOT_SOLID) {
				return;
			}
			
			world.container.remove(me);
			
		}

	}
	
	List<Entity> bullets = new ArrayList<>();
	
	@Override
	public void update() {
		
		if(player == null) {
			return;
		}		
		
		for(Event e : events) {
			
			if(e.type == type.UI_MouseLClick) {
				if(e.altType == type.UI_MousePress) {
					
					MoveableObj p = world.ecs.get(player.e, MoveableObj.class);
					Size s = world.ecs.get(player.e, Size.class);
					
					Point pos = new Point((int)p.position.x, (int)p.position.y);
					
					Entity bullet = new Entity(List.of(
							new Pos(pos.x, pos.y),
							new Size(3, 3),
							new TextureC("bullethell\\bullet"),
							new Collision(shape.COL_SQRUARE, movement.MOV_DYNAMIC, material.NOT_SOLID, 1, new Point(1, 1), new BulletStrat(player.e.id)),
							new MoveableObj(1000, 100 * 30, 140/4, new Triggers() {
								@Override
								public void onStop(Entity e) {
									world.container.remove(e);
									bullets.remove(e);
								}

								@Override
								public void onMapLeave(Entity e) {
									world.container.remove(e);
									bullets.remove(e);
								}
							})
							), world);
					
					bullets.add(bullet);
					
					Vector dir = new Vector(
							e.PointVal.x - pos.x,
							e.PointVal.y - pos.y
							);
					
					world.phys.addToDirection(dir, bullet);
				}
			}
			
		}
	}

	@Override
	public void fixedUpdate() {
		
		
		
	}
	
	private String[] trees = new String[] {
			"bullethell\\Tree_01",
			"bullethell\\Tree_02"
			};

	@Override
	public void switchedTo() {
		
		player = new Player(new Entity(List.of(
				new Pos(400,400),
				new Size(16,16),
				new TextureC("bullethell\\Player"),
				new Collision(shape.COL_SQRUARE, movement.MOV_DYNAMIC, material.IS_SOLID, 16, new Point(0,0)),
				new MoveableObj(70, 70*8, 70*12)
				), world));
		
		System.out.println("Player id: " + player.e.id);
		
		Random r = new Random();
		
		for(int x = 0; x < 30; x++) {
			
			new Entity(List.of(
					new Pos(r.nextInt(801), r.nextInt(800)),
					new Size(64,64),
					new Depth(1),
					new TextureC(trees[r.nextInt(trees.length)]),
					new Collision(shape.COL_SQRUARE, movement.MOV_STATIC, material.IS_SOLID, 6, new Point(29, 29))
					), world);
			
		} 
		
		NPC npc = new NPC(new Point(400, 100), world);
		
	}

	@Override
	public void kill() {}

}
