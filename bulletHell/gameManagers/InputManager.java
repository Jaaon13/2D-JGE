package gameManagers;

import java.util.HashMap;
import java.util.List;

import sceneManagment.Event;
import sceneManagment.Event.type;

public class InputManager {
	
	private HashMap<Character, Runnable> mappedInputs = new HashMap<>();
	
	public void processInputs(List<Event> events) {
		
		for(Event e : events) {
			
			if(e.type != type.UI_KeyPress) {
				continue;
			} else if(!mappedInputs.containsKey(e.CharVal)) {
				continue;
			}
			
			mappedInputs.get(e.CharVal).run();
			
		}
		
	}
	
	public void mapInput(char c, Runnable r) {
		mappedInputs.put(c, r);
	}
	
	public void removeInput(char c) {
		mappedInputs.remove(c);
	}

}
