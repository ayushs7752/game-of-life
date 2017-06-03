package minesweeper;

/**
 * a immutable data type representing individual cell of GameBoard
 * 
 * @author ayushs
 *
 */

public class Cell {

	private boolean isAlive;
	private int x;
	private int y;

	//TODO: AF, REP and rep safety 

	/**
	 * make a Cell
	 * @param state state of the cell
	 * @param hasBomb 
	 */
	public Cell(int x, int y, boolean isAlive) {

		this.isAlive = isAlive;
		this.x = x;
		this.y = y;

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
	public boolean isAlive() {
		return this.isAlive;
	}


	

}
