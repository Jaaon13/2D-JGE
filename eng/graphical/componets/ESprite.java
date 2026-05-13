package graphical.componets;

import java.awt.Point;
import java.util.List;

import assets.Texture;
import controller.Controller;

public abstract class ESprite {
	
	public boolean updated = true, usesTexture = false;
	
	public final int spriteID = Controller.assets.genSpriteID();
	
	private Texture texture;
	private int[] color;
	
	private Point pos, size, atlas;
	private List<Tags> tags = List.of();
	
	public Texture getTexture() {
		return texture;
	}
	public void setTexture(Texture texture) {
		this.updated = true;
		this.texture = texture;
		this.usesTexture = true;
	}
	public int[] getColor() {
		return color;
	}
	public void setColor(int[] color) {
		this.updated = true;
		this.color = color;
	}
	public Point getPos() {
		return pos;
	}
	public void setPos(Point pos) {
		this.updated = true;
		this.pos = pos;
	}
	public Point getSize() {
		return size;
	}
	public void setSize(Point size) {
		this.updated = true;
		this.size = size;
	}
	public Point getAtlas() {
		return atlas;
	}
	public void setAtlas(Point atlas) {
		this.updated = true;
		this.atlas = atlas;
	}
	public List<Tags> getTags() {
		return tags;
	}
	public void setTags(List<Tags> tags) {
		this.updated = true;
		this.tags = tags;
	}
	
}
