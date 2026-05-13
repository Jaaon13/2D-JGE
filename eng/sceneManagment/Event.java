package sceneManagment;

import java.awt.Point;

public class Event {

	// List of events that can happen
	public enum type {
		
		UI_NULL,
		
		UI_KeyPress,
		UI_MouseLClick,
		UI_MouseRClick,
		UI_Mouse3Click,
		UI_Mouse4Click,
		
		UI_MousePress,
		UI_MouseRelease,
		
	}
	
	// Type of event that did happen
	public type type, altType;
	
	// Various different values
	public char CharVal;
	public Point PointVal;
	
	// Different values based on what happened
	public Event(type t, char key) {;
		
		this.type = t;
		this.CharVal = key;
		
		this.altType = type;
		
	}
	
	public Event(type t, type t2, Point p) {
		
		this.type = t;
		this.altType = t2;
		this.PointVal = p;
		
	}
	
	public Event(type t, Point p) {
		
		this.type = t;
		this.PointVal = p;
		
	}
	
}
