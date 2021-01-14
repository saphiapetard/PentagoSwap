package student_player;

import java.lang.Math;
import java.util.ArrayList;

import pentago_swap.PentagoBoardState.Piece;


public class MyTools {
    
    
public static boolean isEdge(int x, int y) {
		
		if (x == 0) {
			return true; 
		}
		else if (y == 0) {
			return true; 
		}
		else if (x==5) {
			return true; 
		}
		else if (y == 5) {
			return true; 
		}
		return false; 

	}
	
	public static boolean isCorner(int x, int y) {
		if ((x == y && x == 0) || (x==y && x==5) ) {
			return true; 
		}
		else if ((x == 0 && y == 5)||(x==5 && y==0)) {
			return true; 
		}
		return false; 
	}
	
public int bLengthOfLine(Piece[][] board, int x, int y) { 
		
		//horizontal check
		int hLength = 0; 
		while(board[x][y] == Piece.BLACK) { 
			x++; 
			hLength++; 			
		}
		while (board[x][y] == Piece.BLACK) {
			x--; 
			hLength++;
		}
		
		//vertical check
		int vLength = 0; 
		while(board[x][y] == Piece.BLACK) { 
			y++; 
			hLength++; 			
		}
		while (board[x][y] == Piece.BLACK) {
			y--; 
			hLength++;
		}
		
		//main diagonal check
		int dLength = 0; 
		if (x==y) {
			while(board[x][y] == Piece.BLACK) {
				y++; 
				x++; 
				dLength++;
			}
			while(board[x][y] == Piece.BLACK) {
				y--; 
				x--; 
				dLength++;
			}
		}
		else if (y==5-x) {
			while(board[x][y] == Piece.BLACK) {
				y++; 
				x--; 
				dLength++;
			}
			while(board[x][y] == Piece.BLACK) {
				y--; 
				x++; 
				dLength++;
			}
			
		}
		
		//side diagonal check
		int sLength = 0; 
		if (x==y+1 || y==x+1) {
			while(board[x][y] == Piece.BLACK) {
				y++; 
				x++; 
				sLength++;
			}
			while(board[x][y] == Piece.BLACK) {
				y--; 
				x--; 
				sLength++;
			}
		}
		else if ((y==4-x) ||(y==6-x)) {
			while(board[x][y] == Piece.BLACK) {
				y++; 
				x--; 
				sLength++;
			}
			while(board[x][y] == Piece.BLACK) {
				y--; 
				x++; 
				sLength++;
			}
			
		}
		int largest; 
		if (sLength>=dLength && sLength>=hLength && sLength>=vLength) {
			largest = sLength; 
		}
		else if (dLength>=sLength && dLength>=hLength && dLength>=vLength) {
			largest = dLength; 
		}
		else if (hLength>=dLength && hLength>=sLength && hLength>=vLength) {
			largest = hLength; 
		}
		else {
			largest =  vLength; 
		}
		return largest; 
	}
	
    

    
  
    



    	
}