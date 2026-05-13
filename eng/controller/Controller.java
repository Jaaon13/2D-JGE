package controller;

import java.awt.Point;
import java.nio.DoubleBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import defaultScene.EngineLoadingScene;
import ecs.EngineComponets;
import ecs.Entity;
import graphical.componets.ESprite;
import graphical.rendering.BasicRenderer;
import logger.Logger;
import logger.Logger.LoggerInfo;
import utilities.FpsCounter;

public class Controller {
	
	// Controllers
	public static final GraphicsController graphics = new GraphicsController();
	public static final RenderController render = new RenderController();
	public static final SceneController scenes = new SceneController();
	public static final DebugController debug = new DebugController();
	
	// Managers
	public static final AssetManager assets = new AssetManager();
	
	// Global data
	public static final ControllerData globals = new ControllerData();
	
	// Global logger
	public static final Logger logger = new Logger(globals.dir + "\\logs\\");
	
	// ESC TEST
	public static final EngineComponets componets = new EngineComponets();
	
	public static volatile boolean running = true;
	
	public static void engineLoop() {
		
		// Call the controller closer if program quits
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {close();}));
		
		Controller.graphics.addTextOverlay("FPS", new Point(0, 10));
		Controller.graphics.addTextOverlay("GPU", new Point(0, 20));
		
		//render.setRenderer(render.addRenderer(new EngineRenderer()));
		render.setRenderer(render.addRenderer(new BasicRenderer()));
		
		DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
		
		while(running) {
			
			xBuffer.clear();
			yBuffer.clear();
			
			GLFW.glfwGetCursorPos(globals.window, xBuffer, yBuffer);
			
			globals.mousePos = new Point((int) xBuffer.get(0), (int) yBuffer.get(0));
			
			FpsCounter.start();
			
			List<Entity> toDraw = scenes.execute();
			
			FpsCounter.sceneEnd();
			
			toDraw.addAll(graphics.getOverlays());
			
			// Update
			render.addEntities(toDraw);
			
			render.render();
			
			FpsCounter.rendererEnd();
			
			updateEngineOverlays();
			
		}
		
		close();
		
	}
	
	private static Runtime r = Runtime.getRuntime();

	private static void updateEngineOverlays() {
		
		r.gc();
		
		long usedMem = r.totalMemory() - r.freeMemory();
		
		String fpsdata = "FPS AVG: " + FpsCounter.fpsAvg + " FPS HIGH: " 
		+ FpsCounter.fpsHigh + " FPS LOW " + FpsCounter.fpsLow;
		
		String fpsPer = "", gpuPer = "";
		
		char[] fpsp = ("" + FpsCounter.sceneUsagePercent).toCharArray(),
				gpup = ("" + FpsCounter.rendererUsagePercent).toCharArray();
		
		for(int x = 0; x < 6; x++) {
			if(x < fpsp.length) {
				fpsPer += fpsp[x];
			} else {
				fpsPer += '0';
			}
			if(x < gpup.length) {	
				gpuPer += gpup[x];
			} else {
				gpuPer += '0';
			}
			
		}
		
		String gpudata = "NUM DRAW CALLS: " + render.getDrawCalls() + " NUM TRIANGLES: " + render.getTotalSpritesDrawn() + " MEM USE MB " 
		+ (usedMem / (1024 * 1024)) + " SCENE USAGE: " + fpsPer 
		+ "% RENDER USAGE: " + gpuPer + "%";
		
		if(graphics.overlayExists("FPS") && debug.fps) {
			graphics.updateTextOverlay("FPS", fpsdata);
		}
		if(graphics.overlayExists("GPU") && debug.fps) {
			graphics.updateTextOverlay("GPU", gpudata);
		}
		
	}

	public static void start(String string, int x, int y) {
		
		if(scenes.curIndex == -1) {
			scenes.setScene(scenes.addScene(new EngineLoadingScene()));
		}
		
		graphics.startNewWindow(string, x, y);
		
	}
	
	private static boolean hasClosed = false;
	
	public static void close() {
		
		if(!hasClosed) {
		
			logger.log(List.of("Closing information", "Total Entities Generated: " + (assets.entityInc-1),
					"Total Text Fields Generated: " + (assets.textInc-1), "Number of total ticks ran: " + Controller.globals.tick), LoggerInfo.INFO);
			
			logger.close();
			running = false;
			hasClosed = true;
			
		}
		
	}
	
}
