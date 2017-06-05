package gol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class gameBoardTest {



	@Test(expected = AssertionError.class)
	public void testAssertionsEnabled() {
		assert false; // make sure assertions are enabled with VM argument: -ea
	}
	
	
	@Test 
	public void createBoardEmpty() {
		GameBoard board = new GameBoard(12, 12, false); 
		System.out.println(board.look());
	}
	
	@Test 
	public void createBoardRandom() {
		GameBoard board = new GameBoard(10, 10, true); 
		System.out.println(board.look());
		int alive = 0; 
		for (int i = 0; i < board.numRows(); ++i) {
			for (int j = 0; j <board.numColumns(); ++j) { 
				if (board.getBoard().get(j).get(i).isAlive()) {
					alive +=1;
				}
			}
		}
		System.out.println("alive cells: " + alive);
	}
	
	
	@Test
	public void produceCell(){
		GameBoard board = new GameBoard(10, 10, false); 
		board.produce(1, 1);
		board.produce(2, 1);
		board.produce(1, 3);
		board.produce(4, 6);
		System.out.println("producing cells");
		System.out.println(board.look());
	}
	
	@Test
	public void updateCellTest(){
		GameBoard board = new GameBoard(10, 10, false); 
		board.produce(1, 1);
		System.out.println("producing cells");
		System.out.println(board.look());
		Cell c1 = board.getBoard().get(1).get(1); 
		Cell c2 = board.getBoard().get(5).get(6); 
		assertEquals(false,board.cellUpdate(c1));
		assertEquals(false, board.cellUpdate(c2));
		
		
		
	}
	
	
	@Test
	public void updateBoard(){
		GameBoard board = new GameBoard(10, 10, false); 
		board.produce(1, 1);
		System.out.println("producing cells");
		System.out.println(board.look());
		System.out.println("updating board");
		board.updateBoard();
		System.out.println(board.look());
		
	}
	

}
