package student_player;

import student_player.MyTools; 
import boardgame.Move;
import student_player.Tracker;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260675803");
    }
    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
     
     //first my agent needs to know what color it is
     int turnPlayer; 
     if (boardState.getTurnNumber() == 1) {
      //my agent is white from here on
      turnPlayer = 0;  
     }
     else  {
      //my agent is black from here on
      turnPlayer = 1; 
     }
     
     //creating my TrackerBoardState
     Tracker trckr = new Tracker(boardState); 
     
     //calling the best move for my agent
     PentagoMove m = trckr.getBestMove(boardState, turnPlayer); 
     
     return (Move) m; 

    }
}