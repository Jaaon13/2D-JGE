package logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import controller.Controller;

public class Logger {
	
	// #TODO: Add severity level to logged information
	
	private FileWriter file;
	
	private String path;
	
	private List<String> lines = new ArrayList<>();
	
	public enum LoggerInfo {
		
		INFO,
		WARNING,
		ERROR,
		
	}
	
	public Logger(String homeDir) {
		
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy_HH.mm.ss"));
		
		path = homeDir + date + ".txt";
		
		try {
			
			Files.createFile(Paths.get(path));
			
			file = new FileWriter(homeDir + date + ".txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void log(String data, LoggerInfo info) {
		
		String temp;
		
		switch(info) {
		case ERROR:
			temp = "|ERROR| Tick: " + Controller.globals.tick + " | ";
			break;
		case INFO:
			temp = "|INFO| Tick: " + Controller.globals.tick + " | ";
			break;
		case WARNING:
			temp = "|WARNING| Tick: " + Controller.globals.tick + " | ";
			break;
		default:
			temp = "|null| Tick: " + Controller.globals.tick + " | ";
			break;
		}
		
		lines.add(temp + data);
		
	}
	
	public void log(List<String> data, LoggerInfo info) {
		
		String temp;
		
		switch(info) {
		case ERROR:
			temp = "|ERROR| ";
			break;
		case INFO:
			temp = "|INFO| ";
			break;
		case WARNING:
			temp = "|WARNING| ";
			break;
		default:
			temp = "|null| ";
			break;
		}
		
		lines.add(temp + data.get(0));
		
		for(int x = 1; x < data.size(); x++) {
			
			lines.add("|+| " + data.get(x));
			
		}
		
	}
	
	public void close() {
		
		try (BufferedWriter bFile = new BufferedWriter(file)) {
			
			for(String line : lines) {
				
				bFile.write(line);
				bFile.newLine();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
