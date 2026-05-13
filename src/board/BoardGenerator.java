package board;

import java.util.Random;

import controller.Controller;
import logger.Logger.LoggerInfo;

public class BoardGenerator {

	public static long seed = 0;
	
	public static int[][] generateBoard(int height, int width, int bombs) {
		
		Random rand = new Random(seed);
		
		int[][] board = new int[height][width];
		
		int max = height*width;
		
		for(int i = 0; i < bombs; i++) {
			
			if(i >= max) {
				Controller.logger.log("Tried to put more bombs than the board can hold!", LoggerInfo.WARNING);
				return board;
			}
			
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			
			if(board[y][x] == 1) {
				i--;
				continue;
			}
			
			board[y][x] = 1;
			
		}
		
		return board;
		
	}
	
	public static void dPrintBoard(int[][] board) {
		
		for(int[] arr : board) {
			
			for(int i : arr) {
				
				System.out.print(i);
				
			}
			
			System.out.println();
			
		}
		
	}
	
}
