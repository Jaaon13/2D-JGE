package fileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SaveFile {

	private String dir;
	
	private Path file;
	
	public SaveFile(String path) {
		
		if(path.isBlank()) {
			path = System.getProperty("user.dir");
		}
		
		if(path.contains(".")) {
			
			StringBuilder sb = new StringBuilder();
			
			for(char c : path.toCharArray()) {
				
				if(c == '.') {
					
					sb.append(".atlas");
					break;
					
				}
				
				sb.append(c);
				
			}
			
			path = sb.toString();
			
		} else {
			
			path += "\\temp.atlas";
			
		}
		
		dir = path;
		
		file = Path.of(dir);
		
		try {
			Files.writeString(file, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void writeString(String data) {
		
		try {
			
			Files.writeString(file, data, StandardOpenOption.APPEND);
			
		} catch (Exception e) {System.out.println(e);}
	}
	
}
