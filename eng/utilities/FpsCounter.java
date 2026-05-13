package utilities;

import java.util.LinkedList;
import java.util.Queue;

public class FpsCounter {

	private static long startTime, cpuEnd, gpuEnd;
	
	public static long fpsAvg, fpsLow, fpsHigh;
	
	public static float sceneUsagePercent, sceneUsageMS, rendererUsagePercent, rendererUsageMS;
	
	private static Queue<Long> fpsBuffer = new LinkedList<>();
	
	public static void start() {
		startTime = System.nanoTime();
	}
	
	// Returns fps average, 1% low, 1% high
	public static void end() {
		
		long timedif = System.nanoTime() - startTime;
		
		if(timedif == 0) {
			timedif = 1;
		}
		
		long fps = 1000000000 / timedif;
		
		fpsBuffer.offer(fps);
		
		long fpsAVG = 0;
		long lowFps = fps;
		long highFps = fps;
		
		for(long f : fpsBuffer) {
			fpsAVG += f;
			
			if(f < lowFps) {
				lowFps = f;
			} else if(f > highFps) {
				highFps = f;
			}
		}
		
		fpsAVG /= fpsBuffer.size();
		
		if(fpsBuffer.size() > 100) {
			fpsBuffer.remove();
		}
		
		fpsAvg = fpsAVG;
		fpsLow = lowFps;
		fpsHigh = highFps;
		
		long sceneUsageNano = (cpuEnd - startTime);
		long rendererUsageNano = (gpuEnd - (startTime + (cpuEnd - startTime)));
		
		sceneUsagePercent = ((float) sceneUsageNano / (float) timedif) * 100f;
		rendererUsagePercent = ((float) rendererUsageNano / (float) timedif) * 100f;
		
		sceneUsageMS = (float) sceneUsageNano / 1000000f;
		rendererUsageMS = (float) rendererUsageNano / 1000000f;
		
	}

	public static void sceneEnd() {
		
		cpuEnd = System.nanoTime();
		
	}

	public static void rendererEnd() {
		
		gpuEnd = System.nanoTime();
		
		end();
		
	}
	
}
