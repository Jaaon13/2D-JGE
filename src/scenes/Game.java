package scenes;

import java.awt.Point;
import java.util.List;

import board.BoardGenerator;
import controller.Controller;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import gui.factorys.ButtonFactory;
import gui.factorys.TextFactory;
import gui.factorys.TextFactory.Alignment;
import sceneManagment.Event.type;
import sceneManagment.Scene;

public class Game extends Scene {
	
	private int[][] board;
	
	private final int padding = 96;
	
	private void print() {
		System.out.println("Yippee");
	}

	public Game(int[][] board, int bombs) {
		
		ButtonFactory.CreateButton("Test Button", Alignment.CENTER, new Point(10, 80), new Point(32, 32), (() -> {print();}), entities);
		
		this.board = board;
		
		Controller.graphics.setScreenSize(new Point(board[0].length * 16 + padding, board.length * 16 + padding));
		
		int sX = padding / 2, sY = padding / 2;
		
		BoardGenerator.dPrintBoard(board);
		
		for(int y = 0; y < board.length; y++) {
			
			int[] arr = board[y];
			
			for(int x = 0; x < arr.length; x++) {
				
				if(board[y][x] == 1) {
					
					new Entity(List.of(
							new Pos(sX + (x * 16), sY + (y * 16)),
							new Size(16, 16),
							new TextureC("\\src\\textures\\MS", "Bomb")
							), entities);
					
				} else if(board[y][x] != 0) {
					
					new Entity(List.of(
							new Pos(sX + (x * 16), sY + (y * 16)),
							new Size(16, 16),
							new TextureC("\\src\\textures\\MS", "" + board[y][x] / 10)
							), entities);
					
				}
				
				new Entity(List.of(
						new Pos(sX + (x * 16), sY + (y * 16)),
						new Size(16, 16),
						new TextureC("\\src\\textures\\MS", "Revealed Square"),
						new Depth(-1)
						), entities);
				
			}
			
		}
		
		TextFactory.generateText(
				"Number Of Bombs: " + bombs, new Point(Controller.globals.screenSize.x / 2, 0), Alignment.CENTER, entities, false);
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
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
