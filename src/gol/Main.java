package gol;

public class Main {
	
	private static GameBoard board = new GameBoard(10, 10, false);
	
	public static void main(String[] args) {
		Cell cell = board.getBoard().get(4).get(1); 
		board.produce(cell.getX(), cell.getY());
		System.out.println(board.look());
		board.updateBoard();
		System.out.println();
	}

}
