package gol;

import java.util.TimerTask;

public class runGame extends TimerTask {

	private final GameBoard gameBoard;

	public runGame(GameBoard b) {
		this.gameBoard = b;
	}

	@Override
	public void run() {
		this.gameBoard.updateBoard();
		System.out.println(this.gameBoard.look());
		System.out.println("-------------------------------------");

	}

}
