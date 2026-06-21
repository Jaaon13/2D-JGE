package board;

import java.util.Random;

import controller.Controller;
import logger.Logger.LoggerInfo;

public class BoardGenerator {
	
	public static int[][] generateBoard(int height, int width, int bombs) {
		
		Random rand = new Random();
		
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
			
			for(int x2 = -1; x2 < 2; x2++) {
				
				for(int y2 = -1; y2 < 2; y2++) {
					
					if((x + x2 >= width ||  x + x2 < 0) || (y + y2 >= height || y + y2 < 0)) {
						
					} else {
						board[y + y2][x + x2] = (board[y + y2][x + x2] != 1) ? board[y + y2][x + x2] + 10 : board[y + y2][x + x2];
					}
					
				}
				
			}
			
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
