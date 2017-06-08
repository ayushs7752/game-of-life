package gol;

import java.util.Timer;

public class Main {

	private static GameBoard board = new GameBoard(20, 20, true);
	
	public static void modify(GameBoard board) {
		
//patern 1 
//		board.produce(4, 4);
//		board.produce(5, 4);
//		board.produce(6, 4);
//		board.produce(3, 5);
//		board.produce(4, 5);
//		board.produce(5, 5);
		
//pattern 2 
//		board.produce(2, 1);
//		board.produce(3, 2);
//		board.produce(1, 3);
//		board.produce(2, 3);
//		board.produce(3, 3);
		
//pattern 3 Glider gun 
		board.produce(1, 5);
		board.produce(2, 5);
		board.produce(1, 6);
		board.produce(2, 6);
		
		board.produce(11, 5);
		board.produce(11, 6);
		board.produce(11, 7);
		board.produce(12, 4);
		board.produce(12, 8);
		
		board.produce(13, 3);
		board.produce(14, 3);
		board.produce(13, 8);
		board.produce(14, 8);
		
		board.produce(17, 5);
		board.produce(17, 6);
		board.produce(17, 7);
		
		board.produce(18, 6);
		board.produce(14, 6);
		
		
		
		
		
	}
	
	public static void main(String[] args) {
		
		modify(board);
		System.out.println(board.look());

		Timer timer = new Timer();
		timer.schedule(new runGame(board), 0, 500);

	}

}
