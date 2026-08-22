package fileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile {
	
	private Scanner reader;
	
	public ReadFile(String p) {
		
		if(!p.contains(".")) {
			System.out.println("Invalid Path! File Does Not Exist!");
			return;
		}
		
		try {
			reader = new Scanner(new File(p));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public String nextLine() {
		
		if(reader.hasNextLine()) {
			
			return reader.nextLine();
			
		}
		
		reader.close();
		return null;
		
	}

}
