package gui.factorys;

import java.awt.Point;

import gui.factorys.TextFactory.Alignment;

public class Text {
	
	public String data;
	
	public Point pos;
	
	public Alignment alignment;
	
	public Text(String d, Point p, Alignment a) {
		this.data = d;
		this.pos = p;
		this.alignment = a;
	}

}
