package student_player;

import java.util.ArrayList;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoMove;
import student_player.MyTools;

//i am assigning position values to each position on the board, that is the grid spaces 'under' the swapping quadrants
//the value of a position is the number of 5-in-a-row lines it can participate in for each color
//as chips are placed, the position values of every spot are updated as a function of how many lines it can still participate in,
//while adding it's streak value; ie, the number of chips it has in a row


public class Tracker {
 private PentagoBoardState myState; 
 public int[][] whiteTracker = {{3,4,4,4,4,3},{4,6,5,5,6,4},{4,5,7,7,5,4},{4,5,7,7,5,4},{4,6,5,5,6,4},{3,4,4,4,4,3}};//initial utility values when boards are empty
 public int[][] blackTracker = {{3,4,4,4,4,3},{4,6,5,5,6,4},{4,5,7,7,5,4},{4,5,7,7,5,4},{4,6,5,5,6,4},{3,4,4,4,4,3}}; 

 public Tracker(PentagoBoardState pbs) {
  myState = pbs;
 }
 
 public PentagoMove getBestMove(PentagoBoardState pbs, int turnPlayer) {
  
 this.myState = pbs; 


  //while the centers of each quadrant are still empty, i want to use them. they are the most valuable places on the board
  
  if (myState.getPieceAt(1,1) == Piece.EMPTY) {
   PentagoMove bestMove = new PentagoMove(1,1,Quadrant.TR,Quadrant.BR, turnPlayer);
   return bestMove; 
  }
  else if (myState.getPieceAt(1,4) == Piece.EMPTY) {
   PentagoMove bestMove = new PentagoMove(1,4,Quadrant.TR,Quadrant.BR, turnPlayer);
   return bestMove;
  }
  else if (myState.getPieceAt(4,1) == Piece.EMPTY) {
   PentagoMove bestMove = new PentagoMove(4,1,Quadrant.TR,Quadrant.BR, turnPlayer);
   return bestMove;

  }
  else if (myState.getPieceAt(4,4) == Piece.EMPTY) {
   PentagoMove bestMove = new PentagoMove(4,4,Quadrant.TR,Quadrant.BR, turnPlayer);
   return bestMove; 
  }
  
   else { //all centers of quadrants are full

  //initializing bestMove variable + creating arraylist
   PentagoMove bestMove = new PentagoMove(0,0,Quadrant.TL,Quadrant.TR,turnPlayer);
   ArrayList<PentagoMove> legalMoves = pbs.getAllLegalMoves();  
   
   
  for (PentagoMove m : legalMoves) {
    //cloning boardState to myState
    PentagoBoardState newPBS = (PentagoBoardState) pbs.clone();
    
    //updating in both tracker boards for best value
  updatePosValues(m,newPBS); 
    
    //blocking attempts + high value positions
    int whiteMax = 0; 
    int blackMax = 0;
    int i = m.getMoveCoord().getX(); 
    int j = m.getMoveCoord().getY(); 
    
    if (turnPlayer == 0) { //i'm white player
     if (currentBlackStreak(m,newPBS) >= 8) { //4 or 5 in a row for black on next turn
      return m;  
     }
     while (this.whiteTracker[i][j] > whiteMax) { //move's position value higher than current best
      whiteMax = this.whiteTracker[i][j];  
      bestMove = m; //found best 
     }
     
    }
    else { //black player
     if (currentWhiteStreak(m,newPBS) >= 8) { //4 or 5 in a row for white on next turn
      return m; 
     }
     while (this.blackTracker[i][j] > blackMax) {
      blackMax = this.blackTracker[i][j];
      bestMove = m;
     }
    }
  }
   
  
    Quadrant[] chosenSwap = checkSwaps(myState, bestMove, turnPlayer); 
    int chosenX = bestMove.getMoveCoord().getX(); 
    int chosenY = bestMove.getMoveCoord().getY(); 
    
    PentagoMove chosenMove = new PentagoMove(chosenX, chosenY, chosenSwap[0], chosenSwap[1], turnPlayer);  
    System.out.print(chosenMove);
    return chosenMove;//use highest value move
    
  }
  }

 
 
 public Quadrant[] checkSwaps(PentagoBoardState pbs, PentagoMove m, int turnPlayer) {
  
  //only six possible swaps per turn
  //i'm going to explore them all and see, once i've picked the coordinates, if any of the swaps maximize my board's value
  //assume my agent is playing white
  //the best swap to choose is the one where sum(whiteTracker values) - sum(blackTracker values) is maximized
  
  //creating the opposing player just for ease
  int oppPlayer; 
  if (turnPlayer == 0) {
   oppPlayer = 1; 
  }
  else {
   oppPlayer = 0; 
  }
  
  //understanding the current move
  int x = m.getMoveCoord().getX(); 
  int y = m.getMoveCoord().getY(); 
  Quadrant swap1 = m.getASwap(); 
  Quadrant swap2 = m.getBSwap(); 
  
  this.myState = (PentagoBoardState) pbs.clone(); 
  this.myState.processMove(m);
  
  //current max is the current coordinates and the swap that my agent decided on based on maximizing the coordinate value
  //now i want to optimize the swaps
  int currentMax = trackerBoardValue(m,turnPlayer);
  
  //currently our bestMove, will change
  PentagoMove bestMove = m;
   
  

  for (Quadrant q : Quadrant.values()) {
   if ( q == swap1) { //fix one quadrant
    for (Quadrant k : Quadrant.values()) { //for quadrants not equal to fixed and not known from move
     while (k != q && k != swap2) { //2 swaps performed here (3 - (move's swap))
      PentagoMove alternateMove = new PentagoMove(x,y,swap1,k,turnPlayer);
      if (trackerBoardValue(alternateMove, turnPlayer) > currentMax) {
       currentMax = trackerBoardValue(alternateMove, turnPlayer) - trackerBoardValue(alternateMove,oppPlayer);
       bestMove = alternateMove; 
      }
     }
    }
   }
   else if (q == swap2) { //fixing next quadrant to swap around, 2 swaps performed here
    for (Quadrant k : Quadrant.values()) { //for quadrants not equal to fixed and not known from move
     while (k != q && k != swap1) {
      PentagoMove alternateMove = new PentagoMove(x,y,swap2,k,turnPlayer);
      currentMax = trackerBoardValue(alternateMove, turnPlayer); 
      if (trackerBoardValue(alternateMove, turnPlayer) > currentMax) {
       currentMax = trackerBoardValue(alternateMove, turnPlayer) - trackerBoardValue(alternateMove,oppPlayer);
       bestMove = alternateMove; 
      }
     }
    }
   }
   else if (q != swap1 && q != swap2){ //1 swap performed here, swapping quadrants that arent't swap1 or spwa2
    for (Quadrant k : Quadrant.values()) {
     while (k!=q && k != swap1 && k!= swap2) {
      PentagoMove alternateMove = new PentagoMove(x,y,q,k,turnPlayer);
      currentMax = trackerBoardValue(alternateMove, turnPlayer);
      if (trackerBoardValue(alternateMove, turnPlayer) > currentMax) {
       currentMax = trackerBoardValue(alternateMove, turnPlayer) - trackerBoardValue(alternateMove,oppPlayer); 
       bestMove = alternateMove; 
      }
     }
    } 
   }  
  }
  Quadrant[] bestSwap = {bestMove.getASwap(),bestMove.getBSwap()}; 
  return bestSwap; 
  
  
 }
 
 //sums over every current entry in the tracker board
 public int trackerBoardValue(PentagoMove m, int turnPlayer) {
  
  this.updatePosValues(m, this.myState); 
  int sum = 0; 
  if (turnPlayer == 0) {
   for (int i=0;i<6;i++) {
    for (int j=0;j<6;j++) {
     sum = sum + this.whiteTracker[i][j]; 
    }
   }
  }
  else {
   for (int i=0;i<6;i++) {
    for (int j=0;j<6;j++) {
     sum = sum + this.blackTracker[i][j]; 
    }
   }
  }
  return sum; 
 }

 
  
 public void updatePosValues(PentagoMove m, PentagoBoardState pbs) {
  //move processed by tracker board
  int x = m.getMoveCoord().getX(); 
  int y = m.getMoveCoord().getY();  
  pbs.processMove(m); 
  this.myState = (PentagoBoardState) pbs.clone(); 
  
  ArrayList<PentagoMove> legalMoves = myState.getAllLegalMoves(); 
  
//updating all positions of both tracker boards given this new move
  for (PentagoMove move:legalMoves) {

   int i = move.getMoveCoord().getX(); 
   int j = move.getMoveCoord().getY();
   
   if (i==x && j==y) {
    //this spot has no value now as it is occupied
    this.whiteTracker[x][y] = 0; 
    this.blackTracker[x][y] = 0; 
   }
   else {
   //all other available positions updated in whiteTracker
   this.whiteTracker[i][j] = this.whiteTracker[i][j] + this.getWhiteUtility(this, move) + currentWhiteStreak(move,this.myState);
   
   //all other available positions updated in blackTracker
   this.blackTracker[i][j] = this.blackTracker[i][j] + this.getBlackUtility(this, move) + currentBlackStreak(move,this.myState); 
   }
  }
   
 }
  
  
  
 
 
 public int getWhiteUtility(Tracker tbs, PentagoMove m) {
  //describes how many axes a particular spot can participate in, given what's around it
  int x = m.getMoveCoord().getX(); 
  int y = m.getMoveCoord().getY(); 
  int utility = tbs.whiteTracker[x][y]; 
  tbs.myState.processMove(m); 
  Piece[][] currBoard = tbs.myState.getPentagoBoard();
   
   //evaluating row - if any of them are black, white can no longer use that axis for a win
   for (int i=0;i<6;i++) {
    Piece p = currBoard[x][i]; 
    if (MyTools.isEdge(x,i)) { //a chip on an edge doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.BLACK) {
     utility--; 
     break; 
    }
   }
   //evaluating column
   for (int i=0;i<6;i++) {
    Piece p = currBoard[i][y]; 
    if (MyTools.isEdge(i,y)) { //a chip on an edge doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.BLACK) {
     utility--; 
     break; 
    }
   }
   
   //evaluating full diagonal
   for (int i=0;i<6;i++) {
    Piece p = currBoard[i][i]; 
    if (MyTools.isCorner(i,i) || MyTools.isCorner(i,5-i) || MyTools.isCorner(5-i,i)) { //a chip on a corner doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.BLACK) {
     utility--; 
     break; 
    }
 
   }
   
   //evaluating 5-slot diagonals (4)
   //playing in edges affects the entire diagonal regardless
   for (int i=0;i<5;i++) {
    if (currBoard[i][i+1] == Piece.BLACK) {
     utility--;
    }
    else if (currBoard[i+1][i] == Piece.BLACK) {
     utility--;
    }
    else if (currBoard[4-i][i] == Piece.BLACK) {
     utility--;
    }
    else if (currBoard[i+1][5-i] == Piece.BLACK) {
     utility--;
    }
        
   }
    return utility; 

   }
    
    
 public int getBlackUtility(Tracker tbs, PentagoMove m) {
  //describes how many axes a particular spot can participate in, given what's around it
  int x = m.getMoveCoord().getX(); 
  int y = m.getMoveCoord().getY(); 
  int utility = tbs.blackTracker[x][y]; 
  
  
  tbs.myState.processMove(m); 
  Piece[][] currBoard = tbs.myState.getPentagoBoard();
  //blackPlayer
   
   //evaluating row - if any of them are white, black can no longer use that axis for a win
   for (int i=0;i<6;i++) {
    Piece p = currBoard[x][i]; 
    if (MyTools.isEdge(x,i)) { //a chip on an edge doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.WHITE) {
     utility--; 
     break; 
    }
   }
   //evaluating column
   for (int i=0;i<6;i++) {
    Piece p = currBoard[i][y]; 
    if (MyTools.isEdge(i,y)) { //a chip on an edge doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.WHITE) {
     utility--; 
     break; 
    }
   }
   
   //evaluating full diagonal
   for (int i=0;i<6;i++) {
    Piece p = currBoard[i][i]; 
    if (MyTools.isCorner(i,i) || MyTools.isCorner(i,5-i) || MyTools.isCorner(5-i,i)) { //a chip on a corner doesn't invalidate possibility of 5 in a row in that row/column
     break; 
    }
    else if (p == Piece.WHITE) {
     utility--; 
     break; 
    }
 
   }
   
   //evaluating 5-slot diagonals (4)
   //playing in edges affects the entire diagonal regardless
   for (int i=0;i<5;i++) {
    if (currBoard[i][i+1] == Piece.WHITE) {
     utility--;
    }
    else if (currBoard[i+1][i] == Piece.WHITE) {
     utility--;
    }
    else if (currBoard[i][4-i] == Piece.WHITE) {
     utility--;
    }
    else if (currBoard[5-i][i+1] == Piece.WHITE) {
     utility--;
    }
        
   }
    return utility; 

   } 
 public  int currentBlackStreak(PentagoMove m, PentagoBoardState pbs) {
  
  int streak = 0; 
  int x = m.getMoveCoord().getX(); 
  int y = m.getMoveCoord().getY(); 
  PentagoBoardState newPBS = (PentagoBoardState) pbs.clone(); 
  Piece[][] boardStatus = newPBS.getPentagoBoard(); 
   
  int addToStreak = 2 * bLengthOfLine(boardStatus,x,y); //doubling as streak has more value than utility
  streak = streak + addToStreak; 
  return streak; 
  }
   

 public  int currentWhiteStreak(PentagoMove m, PentagoBoardState pbs) {
 
 int streak = 0; 
 int x = m.getMoveCoord().getX(); 
 int y = m.getMoveCoord().getY(); 
 PentagoBoardState newPBS = (PentagoBoardState) pbs.clone(); 
 Piece[][] boardStatus = newPBS.getPentagoBoard(); 
 
 
 int addToStreak = 2 * wLengthOfLine(boardStatus,x,y); //doubling as streak has more value than utility
 streak = streak + addToStreak; 
 return streak; 
  
 }
  

 
 public int wLengthOfLine(Piece[][] board, int x, int y) { 
  
  //horizontal check
  int hLength = 0; 
  while(board[x][y] == Piece.WHITE) { 
   x++; 
   hLength++;   
  }
  while (board[x][y] == Piece.WHITE) {
   x--; 
   hLength++; 
  }
  
  //vertical check
  int vLength = 0; 
  while(board[x][y] == Piece.WHITE) { 
   y++; 
   vLength++;     
  }
  while (board[x][y] == Piece.WHITE) {
   y--; 
   vLength++; 
  }
  
  //main diagonal check
  int dLength = 0; 
  if (x==y) {
   while(board[x][y] == Piece.WHITE) {
    y++; 
    x--; 
    dLength++; 
   }
   while(board[x][y] == Piece.WHITE) {
    y--; 
    x++; 
    dLength++; 
   }
  }
  else if (y==5-x) {
   while(board[x][y] == Piece.WHITE) {
    y--; 
    x--; 
    dLength++; 
   }
   while(board[x][y] == Piece.WHITE) {
    y++; 
    x++; 
    dLength++; 
   }
   
  }
  
  //side diagonal check
  int sLength = 0; 
  if (x==y+1 || y==x+1) {
   while(board[x][y] == Piece.WHITE) {
    y++; 
    x++; 
    sLength++; 
   }
   while(board[x][y] == Piece.WHITE) {
    y--; 
    x--; 
    sLength++; 
   }
  }
  else if ((x==4-y) ||(y==6-x)) {
   while(board[x][y] == Piece.WHITE) {
    y++; 
    x--; 
    sLength++; 
   }
   while(board[x][y] == Piece.WHITE) {
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
 
 

