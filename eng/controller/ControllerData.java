package controller;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import assets.Texture;
import graphical.componets.Camera;

public class ControllerData {
	
	public String dir = System.getProperty("user.dir") + "\\assets\\";

	public Texture defaultFont;
	
	public Point screenSize;
	
	public Camera camera = null;
	
	public Point mousePos = null;
	
	public long window;
	
	public Set<Character> keys = new HashSet<>();
	
	public float fixedDelta = 1f / 20f;
	
	// for timing how long things happen at
	public long tick = 0;
	
}
