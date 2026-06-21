package scenes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import board.BoardGenerator;
import controller.Controller;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.EngineComponets.Depth.Layer;
import ecs.Entity;
import gui.factorys.ButtonFactory;
import gui.factorys.TextFactory;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Event;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class Game extends Scene {
	
	private int bombs, rows, col;
	
	private int quitId;
	
	private int[][] board;
	private int[][] revealed;
	
	Map<Point, Entity> squares = new HashMap<>();
	List<Entity> bombsLst = new ArrayList<>();
	
	private final int padding = 48;
	
	private boolean lost = false, won = false;
	
	private void quit() {
		Controller.scenes.setScene(quitId);
	}
	
	private void restart() {
		entities.clear();
		lost = false;
		squares.clear();
		flagIds.clear();
		bombsLst.clear();
		create(BoardGenerator.generateBoard(rows, col, bombs), bombs, quitId);
	}

	public Game(int[][] board, int bombs, int sceneId) {
		create(board, bombs, sceneId);
	}

	private void create(int[][] board, int bombs, int sceneId) {
		
		this.bombs = bombs;
		this.rows = board.length;
		this.col = board[0].length;
		
		ButtonFactory.CreateButton("Quit", Alignment.CENTER, new Point(6, 60), new Point(40, 40), (() -> {quit();}), entities);
		ButtonFactory.CreateButton("Restart", Alignment.CENTER, new Point(6, 100), new Point(40, 40), (() -> {restart();}), entities);
		
		this.quitId = sceneId;
		this.board = board;
		this.revealed = new int[board.length][board[0].length];
		
		for(int y = 0; y < board.length; y++) {
			
			int[] arr = board[y];
			
			for(int x = 0; x < arr.length; x++) {
				
				if(board[y][x] == 1) {
					
					Entity e = new Entity(List.of(
							new Pos(padding + (x * 16), padding + (y * 16)),
							new Size(16, 16),
							new TextureC("\\src\\textures\\MS", "Bomb"),
							new Depth(0)
							), entities);
					
					bombsLst.add(e);
					
					
				} else if(board[y][x] != 0) {
					
					new Entity(List.of(
							new Pos(padding + (x * 16), padding + (y * 16)),
							new Size(16, 16),
							new TextureC("\\src\\textures\\MS", "" + board[y][x] / 10)
							), entities);
					
				}
				
				Entity e = new Entity(List.of(
						new Pos(padding + (x * 16), padding + (y * 16)),
						new Size(16, 16),
						new TextureC("\\src\\textures\\MS", "Blank Square"),
						new Depth(1)
						), entities);
				
				squares.put(new Point(x, y), e);
				
			}
			
		}
		
		TextFactory.generateText(
				"Number Of Bombs: " + bombs, new Point(Controller.globals.screenSize.x / 2, 0), Alignment.CENTER, entities, false);
	}

	@Override
	public void update() {
		
		if(lost || won) {
			return;
		}
		
		for(Event e : events) {
			
			if(e.type == type.UI_MouseLClick) {
				if(e.altType == type.UI_MousePress) {
					
					mouseclick(e.PointVal);
					
				}
			} else if(e.type == type.UI_MouseRClick) {
				if(e.altType == type.UI_MousePress) {
					
					placeFlag(e.PointVal);
					
				}
			}
			
		}
		
		hasWon();
		
	}
	
	private void hasWon() {
		
		int totalR = rows * col;
		
		for(int[] iarr : revealed) {
			for(int i : iarr) {
				totalR -= i;
			}
		}
		
		if(totalR == bombs) {
			
			won = true;
			TextFactory.generateText("You Won!", new Point((Controller.globals.screenSize.x / 2), padding - 10),
					Alignment.CENTER, entities, false);
			
		}

	}

	private Map<Point, Integer> flagIds = new HashMap<>();

	private void placeFlag(Point click) {
		
		if(!(padding <= click.x && click.x <= (board[0].length * 16) + padding)) {
			return;
		}
		
		if(!(padding <= click.y && click.y <= (board.length * 16) + padding)) {
			return;
		}
		
		Point boardPos = new Point((click.x - padding) / 16, (click.y - padding) / 16);
		
		if(flagIds.containsKey(boardPos)) {
			
			int key = flagIds.get(boardPos);
			entities.remove(key);
			flagIds.remove(boardPos);
			
		} else {
			
			if(revealed[boardPos.y][boardPos.x] == 1) {
				return;
			}
			
			Entity e = new Entity(List.of(
					new Pos(padding + (boardPos.x * 16), padding + (boardPos.y * 16)),
					new Size(16, 16),
					new TextureC("\\src\\textures\\MS", "Flag"),
					new Depth(2)
					), entities);
			
			flagIds.put(boardPos, e.id);
			
		}
		
	}

	private void mouseclick(Point click) {
		
		if(!(padding <= click.x && click.x <= (board[0].length * 16) + padding)) {
			return;
		}
		
		if(!(padding <= click.y && click.y <= (board.length * 16) + padding)) {
			return;
		}
		
		Point boardPos = new Point((click.x - padding) / 16, (click.y - padding) / 16);
		
		if(flagIds.containsKey(boardPos)) {
			return;
		}
		
		search(boardPos);
		
	}
	
	private void search(Point boardPos) {
		
		if(!((0 <= boardPos.x && boardPos.x < board[0].length) && (0 <= boardPos.y && boardPos.y < board.length))) {
			return;
		}
		if(revealed[boardPos.y][boardPos.x] != 0) {
			return;
		}
		if(board[boardPos.y][boardPos.x] != 0) {
			revealSquare(boardPos);
			return;
		}
		
		revealSquare(boardPos);
		
		// Edges
		search(new Point(boardPos.x + 1, boardPos.y));
		search(new Point(boardPos.x - 1, boardPos.y));
		search(new Point(boardPos.x, boardPos.y + 1));
		search(new Point(boardPos.x, boardPos.y - 1));
		
		// Corners
		search(new Point(boardPos.x + 1, boardPos.y + 1));
		search(new Point(boardPos.x - 1, boardPos.y + 1));
		search(new Point(boardPos.x + 1, boardPos.y - 1));
		search(new Point(boardPos.x - 1, boardPos.y - 1));
		
	}

	private void revealSquare(Point pos) {
		
		revealed[pos.y][pos.x] = 1;
		
		int id = squares.get(pos).id;
		
		entities.update(id, TextureC.class, new TextureC("\\src\\textures\\MS", "Revealed Square"));
		entities.update(id, Depth.class, -1);
		
		if(board[pos.y][pos.x] == 1) {
			
			lost = true;
			TextFactory.generateText("You Lost!", new Point((Controller.globals.screenSize.x / 2), padding - 10),
					Alignment.CENTER, entities, false);
			
			for(Entity e : bombsLst) {
				
				entities.update(e, Depth.class, 99);
				
			}
			
			for(int fId : flagIds.values()) {
				
				entities.remove(fId);
				
			}
			
			flagIds.clear();
			
		}
		
	}

	@Override
	public void fixedUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchedTo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub
		
	}

}
