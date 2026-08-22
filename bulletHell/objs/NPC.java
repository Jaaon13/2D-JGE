package objs;

import java.awt.Point;
import java.util.List;

import ecs.EngineComponets.Collision;
import ecs.EngineComponets.Collision.material;
import ecs.EngineComponets.Collision.movement;
import ecs.EngineComponets.Collision.shape;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import sceneManagment.World;

public class NPC {
	
	public Entity npc;
	
	public NPC(Point pos, World world) {
		
		npc = new Entity(List.of(
				new Pos(pos.x, pos.y),
				new Size(16,16),
				new TextureC("bullethell\\Hostile_01"),
				new Collision(shape.COL_SQRUARE, movement.MOV_DYNAMIC, material.IS_SOLID, 16, new Point(0,0))
				), world);
		
	}

}
