package scenes;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import ecs.EngineComponets.Collision;
import ecs.EngineComponets.Collision.movement;
import ecs.EngineComponets.Collision.shape;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.MoveableObj;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import objs.NPC;
import objs.Player;
import physics.Vector;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class TestScene extends Scene {
	
	public Player player;

	@Override
	public void update() {
		
		if(player == null) {
			return;
		}		
		
		this.phys.update(events);
		
		for(Event e : events) {
			
			if(e.type == type.UI_MouseLClick) {
				if(e.altType == type.UI_MousePress) {
					
					Pos p = entities.get(player.e, Pos.class);
					Size s = entities.get(player.e, Size.class);
					
					Point pos = new Point(p.x + (s.x/2), p.y + (s.y/2));
					
					Entity bullet = new Entity(List.of(
							new Pos(pos.x, pos.y),
							new Size(3, 3),
							new TextureC("bullethell\\bullet"),
							new Collision(shape.COL_SQRUARE, movement.MOV_DYNAMIC, 1, new Point(1, 1)),
							new MoveableObj(140, 140*8, 140/4)
							), entities);
					
					MoveableObj mo = entities.get(bullet, MoveableObj.class);
					
					Vector dir = new Vector(
							e.PointVal.x - pos.x,
							e.PointVal.y - pos.y
							);
					
					dir = dir.normalize();
					
					// #TODO: Not following the cursor correctly, seems to snap to certain angles
					
					mo.direction = dir;
					mo.velocity = new Vector(1000 * mo.direction.x, 1000 * mo.direction.y);
					
					mo.position.x += dir.x * 32;
					mo.position.y += dir.y * 32;
					
					System.out.println(dir.x + ", " + dir.y);
					
				}
			}
			
		}
	}

	@Override
	public void fixedUpdate() {
		
		this.phys.fixedUpdate();
		
	}

	@Override
	public void switchedTo() {
		
		player = new Player(new Entity(List.of(
				new Pos(400,400),
				new Size(16,16),
				new TextureC("bullethell\\Player"),
				new Collision(shape.COL_SQRUARE, movement.MOV_DYNAMIC, 16, new Point(0,0)),
				new MoveableObj(70, 70*8, 70*12)
				), entities));
		
		Random r = new Random();
		
		for(int x = 0; x < 30; x++) {
			
			Entity e = new Entity(List.of(
					new Pos(r.nextInt(801), r.nextInt(800)),
					new Size(64,64),
					new Depth(1),
					new TextureC("bullethell\\tree_01"),
					new Collision(shape.COL_SQRUARE, movement.MOV_STATIC, 6, new Point(29, 29))
					), entities);
			
		}
		
		NPC npc = new NPC(new Point(400, 100), entities);
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
