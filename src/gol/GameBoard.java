
package gol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * a mutable thread-safe data type representing a minesweeper GameBoard
 */
public class GameBoard {

	private final int numRows;
	private final int numColumns;

	private List<List<Cell>> board;

	/*
	 * AF(numRows,numColumns, board) = a minesweeper board of size numRows x
	 * numColumns
	 * 
	 * rep invariant: numRows, numColumns > 0
	 * 
	 * safety from rep exposure: all fields are private final and mutators are
	 * used within the internal implementation only.
	 */

	/**
	 * initialize a GameBoard instance with either no bombs or 0.25 prob bombs
	 * (depending on random)
	 * 
	 * @param rows
	 *            number of rows in board
	 * @param columns
	 *            number of columns in board
	 * @param random
	 *            whether to put random bombs with prob 0.25 or not at all
	 */
	public GameBoard(int rows, int columns, boolean random) {
		this.numRows = rows;
		this.numColumns = columns;

		board = new ArrayList<>();

		if (random) {
			for (int i = 0; i < rows; ++i) {
				List<Cell> rowArray = new ArrayList<Cell>();
				for (int j = 0; j < columns; ++j) {
					rowArray.add(new Cell(j, i, randomAlive()));
				}
				board.add(rowArray);
			}
		}

		else {
			for (int i = 0; i < rows; ++i) {
				List<Cell> rowArray = new ArrayList<Cell>();
				for (int j = 0; j < columns; ++j) {
					rowArray.add(new Cell(j, i, false));
				}
				board.add(rowArray);
			}
		}

	}

	public boolean randomAlive() {

		List<Integer> randomList = new ArrayList<>();
		randomList.addAll(Arrays.asList(1, 2, 3, 4));
		Random randomizer = new Random();

		if (randomList.get(randomizer.nextInt(4)) == 1) {
			return true;
		}
		return false;

	}

	/**
	 * number of rows in the board
	 * 
	 * @return number of rows in this board
	 */

	public int numRows() {
		return this.numRows;
	}

	/**
	 * number of columns in the board
	 * 
	 * @return number of columns in this board
	 */
	public int numColumns() {
		return this.numColumns;
	}

	/**
	 * get the board associated with this minesweeper instance
	 * 
	 * @return board of this minesweeper
	 */

	public List<List<Cell>> getBoard() {
		return this.board;
	}

	/**
	 * get all the adjacent cells of the given cell
	 * 
	 * @param x
	 *            x coordinate of given cell
	 * @param y
	 *            y coordinate of given cell
	 * @return set of valid adjacent cells
	 */
	public Set<Cell> adjacentCells(int x, int y) {
		Set<Cell> adjacentCells = new HashSet<>();

		try {
			Cell topCell = this.board.get(y - 1).get(x);
			adjacentCells.add(topCell);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell bottomCell = this.board.get(y + 1).get(x);
			adjacentCells.add(bottomCell);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell leftCell = this.board.get(y).get(x - 1);
			adjacentCells.add(leftCell);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell rightCell = this.board.get(y).get(x + 1);
			adjacentCells.add(rightCell);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell upperLeftDiagonal = this.board.get(y - 1).get(x - 1);
			adjacentCells.add(upperLeftDiagonal);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell lowerRightDiagonal = this.board.get(y + 1).get(x + 1);
			adjacentCells.add(lowerRightDiagonal);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell upperRightDiagonal = this.board.get(y - 1).get(x + 1);
			adjacentCells.add(upperRightDiagonal);
		} catch (IndexOutOfBoundsException e) {

		}
		try {
			Cell lowerLeftDiagonal = this.board.get(y + 1).get(x - 1);
			adjacentCells.add(lowerLeftDiagonal);
		} catch (IndexOutOfBoundsException e) {

		}

		return adjacentCells;
	}

	/**
	 * return a viewable representation of GameBoard according to the specs of
	 * ps4
	 * 
	 * @return view of the current GameBoard
	 */

	public String look() {

		String boardView = "";
		for (int i = 0; i < this.numRows; ++i) {

			String rowView = "";

			for (int j = 0; j < this.numColumns; ++j) {
				Cell thisCell = this.board.get(i).get(j);
				String cellView = "";

				if (thisCell.isAlive()) {
					cellView = "#";
				}

				if (!(thisCell.isAlive())) {
					cellView = " ";
				}

				if (j == this.numColumns - 1) {
					rowView += cellView;
				} else {
					rowView += cellView + "";
				}

			}

			boardView += rowView + "\n";

		}

		return boardView;
	}

	/**
	 * kill the given Cell
	 * 
	 * @param x
	 *            x coordinate of the given Cell
	 * @param y
	 *            y coordinate of the given cell
	 */
	public void kill(int x, int y) {
		if (x < this.numColumns && y < this.numRows) {
			this.board.get(y).set(x, new Cell(x, y, false));

		}
	}

	/**
	 * make the given cell alive
	 * 
	 * @param x
	 *            x coordinate of the given Cell
	 * @param y
	 *            y coordinate of the given cell
	 */
	public void produce(int x, int y) {
		if (x < this.numColumns && y < this.numRows) {

			this.board.get(y).set(x, new Cell(x, y, true));
		}
	}
	
	public int countAliveAdjacent(Cell c) {
		Set<Cell> adj = adjacentCells(c.getX(), c.getY());
		int countAliveAdjacent = 0;
		for (Cell cell : adj) {
			if (cell.isAlive()) {
				countAliveAdjacent += 1;
			}
		}
		return countAliveAdjacent;
	}
	
	public Cell getCell(int x, int y) {
		return this.getBoard().get(y).get(x); 
	}

	/**
	 * update a given with the following rules--
	 * 
	 * Any live cell with fewer than two live neighbours dies, as if caused by
	 * underpopulation. Any live cell with two or three live neighbours lives on
	 * to the next generation. Any live cell with more than three live
	 * neighbours dies, as if by overpopulation. Any dead cell with exactly
	 * three live neighbours becomes a live cell, as if by reproduction.
	 * 
	 * @return true iff the cell is alive in the next step
	 */
	

	public boolean cellUpdate(Cell c) {
		int countAliveAdjacent = countAliveAdjacent(c);

		if (c.isAlive()) {
			if (countAliveAdjacent < 2) {
				return false;
			}
			if (countAliveAdjacent == 2 || countAliveAdjacent == 3) {
				return true;
			}
			if (countAliveAdjacent > 3) {
				return false;
			}

		} else {
			if (countAliveAdjacent == 3) {
				System.out.println("should come here");
				return true;

			}
		}

		return false;

	}

	public void updateBoard() {
		List<List<Cell>> boardTemp = new ArrayList<>();
		int rows = this.numRows;
		int columns = this.numColumns;

		for (int i = 0; i < rows; ++i) {

			List<Cell> rowArray = new ArrayList<Cell>();

			for (int j = 0; j < columns; ++j) {
				boolean isAlive = cellUpdate(this.getCell(i, j));
				if (isAlive) {
					System.out.println("here" + i + " " + j );
				}
				rowArray.add(new Cell(j, i, isAlive));
			}
			boardTemp.add(rowArray);
		}
		this.board = boardTemp;
	}

}
