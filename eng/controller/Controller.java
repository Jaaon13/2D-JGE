package controller;

import java.awt.Point;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import defaultScene.EngineLoadingScene;
import ecs.EngineComponets;
import ecs.Entity;
import graphical.rendering.PainterRenderer;
import gui.factorys.Text;
import logger.Logger;
import logger.Logger.LoggerInfo;
import sceneManagment.Scene;
import utilities.FpsCounter;

public class Controller {
	
	// Developer Mode
	private static boolean devMode = false;
	
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
	public static final Logger logger = new Logger(System.getProperty("user.dir") + "\\logs\\");
	
	// Compononets
	public static final EngineComponets componets = new EngineComponets();
	
	public static Scene currentScene;
	
	public static volatile boolean running = true;
	
	public static void createFPS() {
		Controller.graphics.addTextOverlay("FPS", new Point(0, 0));
		Controller.graphics.addTextOverlay("GPU", new Point(0, 10));
		Controller.graphics.addTextOverlay("MEM", new Point(0, 20));
	}
	
	public static void removeFPS() {
		Controller.graphics.removeTextOverlay("FPS");
		Controller.graphics.removeTextOverlay("GPU");
		Controller.graphics.removeTextOverlay("MEM");
	}
	
	public static void engineLoop() {
		
		if (GLFW.glfwRawMouseMotionSupported())
			GLFW.glfwSetInputMode(Controller.globals.window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		
		// Call the controller closer if program quits
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {close();}));
		
		if(devMode) {
			Controller.graphics.addTextOverlay("MousePos", new Point(-100, -100));
		}
		
		//render.setRenderer(render.addRenderer(new BasicRenderer()));
		render.setRenderer(render.addRenderer(new PainterRenderer()));
		
		DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
		
		while(running) {
			
			xBuffer.clear();
			yBuffer.clear();
			
			GLFW.glfwGetCursorPos(globals.window, xBuffer, yBuffer);
			
			globals.mousePos = new Point((int) xBuffer.get(0), (int) yBuffer.get(0));
			
			if(devMode) { // Outputs the mouse's current position
				Controller.graphics.updateTextOverlay("MousePos", globals.mousePos.x + "." + globals.mousePos.y, new Point(globals.mousePos.x - 20, globals.mousePos.y - 10));
			}
			
			FpsCounter.start();
			
			List<Entity> toDraw = scenes.execute();
			
			FpsCounter.sceneEnd();
			
			List<Text> text = new ArrayList<>();
			
			FpsCounter.startText();
			text.addAll(graphics.getOverlays());
			
			text.addAll(scenes.getText());
			
			render.setText(text);
			
			// Update
			
			FpsCounter.startRender();
			
			render.addEntities(toDraw);
			
			render.render();
			
			FpsCounter.rendererEnd();

			updateEngineOverlays();
			
		}
		
		close();
		
	}
	
	private static long tps = 0;
	private static long last = System.currentTimeMillis();
	private static long lasttick = 0;

	private static Runtime runtime = Runtime.getRuntime();
	
	private static void updateEngineOverlays() {
		
		if(!debug.fps) {return;}
		
		if(System.currentTimeMillis() - last >= 1000) {
			tps = globals.tick - lasttick;
			lasttick = globals.tick;
			last = System.currentTimeMillis();
		}
		
		String fpsdata = "FPS AVG: " + FpsCounter.fpsAvg + " FPS HIGH: " 
		+ FpsCounter.fpsHigh + " FPS LOW: " + FpsCounter.fpsLow + " TPS: " + tps;
		
		String fpsPer = "", gpuPer = "", textPer = "";
		
		Controller.debug.avgFps = FpsCounter.fpsAvg;
		
		char[] fpsp = ("" + FpsCounter.sceneUsagePercent).toCharArray(),
				gpup = ("" + FpsCounter.rendererUsagePercent).toCharArray(),
				trend = ("" + FpsCounter.textUsagePercent).toCharArray();
		
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
			if(x < trend.length) {	
				textPer += trend[x];
			} else {
				textPer += '0';
			}
			
		}
		
		String gpudata = "NUM DRAW CALLS: " + render.getDrawCalls() + " NUM TRIANGLES: " + render.getTotalSpritesDrawn() + " SCENE USAGE: "
		+ fpsPer + "% RENDER USAGE: " + gpuPer + "% TEXT RENDER USAGE: " + textPer + "%";
		
		runtime.gc();
		
		float mem = (float)(runtime.totalMemory() - runtime.freeMemory()) / (1024f * 1024f);
		
		if(graphics.overlayExists("FPS") && debug.fps) {
			graphics.updateTextOverlay("FPS", fpsdata);
		}
		if(graphics.overlayExists("GPU") && debug.fps) {
			graphics.updateTextOverlay("GPU", gpudata);
		}
		if(graphics.overlayExists("MEM") && debug.fps) {
			graphics.updateTextOverlay("MEM", mem + " MB");
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
		
			logger.log(List.of("Closing information", "Total Entities Generated: " + (assets.entityInc+1),
					"Total Characters Generated: " + (assets.tempInc+1), "Number of total ticks ran: " + Controller.globals.tick), LoggerInfo.INFO);
			
			logger.close();
			running = false;
			hasClosed = true;
			
			GLFW.glfwTerminate();
			
		}
		
	}
	
}
