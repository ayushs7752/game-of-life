/* Copyright (c) 2007-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import minesweeper.Cell.State;

/**
 * a mutable thread-safe data type representing a minesweeper GameBoard
 */
public class GameBoard {

	private final int numRows;
	private final int numColumns;

	private final List<List<Cell>> board;

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
		boolean hasBomb = false;

		if (random) {

			List<Integer> randomList = new ArrayList<>();

			randomList.addAll(Arrays.asList(1, 2, 3, 4));

			Random randomizer = new Random();

			if (randomList.get(randomizer.nextInt(4)) == 1) {
				hasBomb = true;
			}
		}
		board = new ArrayList<>();
		List<List<Cell>> boardSync = Collections.synchronizedList(board);

		for (int i = 0; i < rows; ++i) {

			List<Cell> rowArray = new ArrayList<Cell>();
			List<Cell> rowArraySync = Collections.synchronizedList(rowArray);

			for (int j = 0; j < columns; ++j) {
				rowArraySync.add(new Cell(j, i, State.UNTOUCHED, hasBomb));
			}
			boardSync.add(rowArray);
		}

	}

	/**
	 * number of rows in the board
	 * 
	 * @return number of rows in this board
	 */

	public synchronized int numRows() {
		return this.numRows;
	}

	/**
	 * number of columns in the board
	 * 
	 * @return number of columns in this board
	 */
	public synchronized int numColumns() {
		return this.numColumns;
	}

	/**
	 * get the board associated with this minesweeper instance
	 * 
	 * @return board of this minesweeper
	 */

	public synchronized List<List<Cell>> getBoard() {
		return Collections.synchronizedList(this.board);
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
	public synchronized Set<Cell> adjacentCells(int x, int y) {
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
	 * return a viewable representation of GameBoard according to the
	 * specs of ps4
	 * 
	 * @return view of the current GameBoard
	 */

	public synchronized String look() {

		String boardView = "";
		for (int i = 0; i < this.numRows; ++i) {

			String rowView = "";

			for (int j = 0; j < this.numColumns; ++j) {
				Cell thisCell = this.board.get(i).get(j);
				String cellView = "";

				if (thisCell.getState().equals(State.UNTOUCHED)) {
					cellView = "-";
				}

				if (thisCell.getState().equals(State.FLAGGED)) {
					cellView = "F";
				}

				if (thisCell.getState().equals(State.DUG)) {
					Set<Cell> adjacentCells = this.adjacentCells(j, i);
					int countBombs = 0;
					for (Cell c : adjacentCells) {
						if (c.hasBomb()) {
							countBombs += 1;
						}
					}
					if (countBombs == 0) {
						cellView = " ";
					} else {
						cellView = Integer.toString(countBombs);
					}

				}
				if (j == this.numColumns - 1) {
					rowView += cellView;
				} else {
					rowView += cellView + " ";
				}

			}

			boardView += rowView + "\n";

		}

		return boardView;
	}

	/**
	 * recursively dig minesweeper cell according to standard rules as per the spec of ps4
	 * @param x x coordinate of minesweeper square
	 * @param y y coordinate of minesweeper square
	 */
	public synchronized String dig(int x, int y) {

		if (x > this.numColumns - 1 || x < 0 || y < 0 || y > this.numRows - 1) {
			return this.look();
		}

		Cell thisCell = this.board.get(y).get(x);

		if (!(thisCell.getState().equals(State.UNTOUCHED))) {
			return this.look();
		}

		if (thisCell.getState().equals(State.UNTOUCHED)) {
			this.board.get(y).set(x, new Cell(x, y, State.DUG, thisCell.hasBomb()));

			if (this.board.get(y).get(x).hasBomb()) {

				this.board.get(y).set(x, new Cell(x, y, State.DUG, false));

				digRecursive(x, y);
				return this.boom();

			}

		}

		return this.look();

	}

	/**
	 * helper function for recursive part of dig method--If the square x,y has
	 * no neighbor squares with bombs, then for each of x,y’s untouched neighbor
	 * squares, change said square to dug and repeat this step (not the entire
	 * DIG procedure) recursively for said neighbor square unless said neighbor
	 * square was already dug before said change.
	 * 
	 * @param x
	 *            x coordinate of given Cell
	 * @param y
	 *            y coordinate of given Cell
	 */
	private synchronized void digRecursive(int x, int y) {

		Set<Cell> adjacentCells = this.adjacentCells(x, y);

		boolean noAdjacentBombs = true;
		for (Cell c : adjacentCells) {
			if (c.hasBomb()) {
				noAdjacentBombs = false;
			}
		}

		if (noAdjacentBombs) {
			for (Cell c : adjacentCells) {
				if (c.getState().equals(State.UNTOUCHED)) {

					this.board.get(c.getY()).set(c.getX(),
							new Cell(c.getX(), c.getY(), State.DUG, this.board.get(c.getY()).get(c.getX()).hasBomb()));

					digRecursive(c.getX(), c.getY());
				}

			}
		}
		return;
	}

	/**
	 * deflag the given Cell
	 * 
	 * @param x
	 *            x coordinate of the given Cell
	 * @param y
	 *            y coordinate of the given cell
	 * @return Board view of the current gameBoard
	 */
	public synchronized String deflag(int x, int y) {
		if (x < this.numColumns && y < this.numRows && this.board.get(y).get(x).getState().equals(State.FLAGGED)) {

			this.board.get(y).set(x, new Cell(x, y, State.UNTOUCHED, this.board.get(y).get(x).hasBomb()));

			return this.look();
		}
		return this.look();
	}

	/**
	 * flag the given Cqell
	 * 
	 * @param x
	 *            x coordinate of the given Cell
	 * @param y
	 *            y coordinate of the given cell
	 * @return Board view of the current gameBoard
	 */
	public synchronized String flag(int x, int y) {
		if (x < this.numColumns && y < this.numRows && this.board.get(y).get(x).getState().equals(State.UNTOUCHED)) {

			this.board.get(y).set(x, new Cell(x, y, State.FLAGGED, this.board.get(y).get(x).hasBomb()));

			return this.look();
		}
		return this.look();
	}

	/**
	 * send a boom message 
	 * @return boom message
	 */
	public synchronized String boom() {
		return "BOOM!\n";

	}
	/**
	 * send a help message
	 * @return help message
	 */
	public synchronized String help() {

		return "Valid Commands include: DIG, LOOK, FLAG, DEFLAG,BYE \n";
	}
	

	

}
