package minesweeper;

/**
 * a immutable data type representing individual cell of GameBoard
 * 
 * @author ayushs
 *
 */

public class Cell {

	private State state;
	private boolean hasBomb;
	private int x;
	private int y;

	//TODO: AF, REP and rep safety 
	
	public enum State {
		FLAGGED, UNTOUCHED, DUG
	}

	/**
	 * make a Cell
	 * @param state state of the cell
	 * @param hasBomb 
	 */
	public Cell(int x, int y, State state, boolean hasBomb ) {
		this.state = state;
		this.hasBomb = hasBomb;
		this.x = x;
		this.y = y;

	}
	
	


	/**
	 * get state of given cell
	 */
	public State getState() {
		return this.state;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

	/**
	 * return whether given cell has a bomb
	 * 
	 * @return true or false depending on whether this cell has a bomb or not
	 */
	public boolean hasBomb() {
		return this.hasBomb;
	}


	

}
