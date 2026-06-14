package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import controller.Controller;
import ecs.EntityManager;
import gui.factorys.Text;
import gui.factorys.TextFactory;

// TODO: Actually make a proper and useable multithreaded solution that isnt bad

public class ThreadPool {

	public static int threads = Runtime.getRuntime().availableProcessors();
	
	private static ExecutorService threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private static List<List<Runnable>> tasks = createTasks();
	
	private static int balancer = 0;
	
	private static List<List<Runnable>> createTasks() {
		
		tasks = new ArrayList<>();
		
		for(int x = 0; x < threads; x++) {
			
			tasks.add(x, new ArrayList<>());
			
		}
		
		return tasks;
	}
	
	private static void reset() {
		
		balancer = 0;
		
		tasks = new ArrayList<>();
		
		for(int x = 0; x < threads; x++) {
			
			tasks.add(x, new ArrayList<>());
			
		}
		
		threadpool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
	}
	
	public static void submit(Runnable script) {
		
		threadpool.submit(script);
		
	}
	
	public static void blockUntilCompletion() {
		
		threadpool.shutdown();
		
		try {
			threadpool.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		reset();
		
	}
	
	private static class TextThreadWrap {
		
		List<Text> text;
		
		EntityManager em;
		
		public TextThreadWrap(List<Text> text, EntityManager em) {
			
			this.text = text;
			this.em = em;
			
		}
		
	}

	public static void createText(List<Text> text, EntityManager textManager) {
		
		TextThreadWrap[] wraps = new TextThreadWrap[threads];
		
		for(int x = 0; x < threads; x++) {
			wraps[x] = new TextThreadWrap(new ArrayList<>(), textManager);
		}
		
		for(Text t : text) {
			
			if(balancer >= threads) {balancer = 0;}
			
			wraps[balancer].text.add(t);
			balancer++;
			
		}
		
		for(int i = 0; i < threads; i++) {
			
			int i2 = i;
			
			threadpool.submit(() -> {
				
				TextThreadWrap w = wraps[i2];
				
				for(Text t : w.text) {
					
					if(t.data.isEmpty()) {continue;}
					
					TextFactory.generateText(t.data, t.pos, t.alignment, textManager, true);
					
				}
				
			});
			
		}
		
	}
	
}
