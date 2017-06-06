package gol;

import java.util.Timer;

public class Main {

	private static GameBoard board = new GameBoard(10, 10, true);
	
	public static void modify(GameBoard board) {
		board.produce(4, 4);
		board.produce(5, 4);
		board.produce(6, 4);
//		board.produce(5, 5);
	}
	
	public static void main(String[] args) {
		
//		modify(board);

		Timer timer = new Timer();
		timer.schedule(new runGame(board), 0, 500);

	}

}
