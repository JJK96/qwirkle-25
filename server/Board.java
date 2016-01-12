package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

	private Map<Position, Stone> stones;
	private Map<Position, PossibleMove> possibleMoves;

	/**
	 * Creates a new board for a game
	 */
	public Board() {
		reset();
	}

	/**
	 * Resets the board to the state at the start of the game
	 */
	public void reset() {
		stones = new HashMap<Position, Stone>();
		possibleMoves = new HashMap<Position, PossibleMove>();
		PossibleMove init = new PossibleMove(Stone.Shape.values(), Stone.Color.values());
		init.setPosition(new Position(0, 0));
		possibleMoves.put(init.getPosition(), init);
	}

	/**
	 * Get all the possiblemoves
	 * 
	 * @return map of positions and possiblemoves
	 */
	public Map<Position, PossibleMove> getPossibleMoves() {
		return possibleMoves;
	}

	/**
	 * Update the board??
	 */
	public void update() {

	}

	/**
	 * Get the board with all its stones
	 * 
	 * @return stones
	 */
	public Map<Position, Stone> getStones() {
		return this.stones;
	}

	/**
	 * Creates a copy of the board with all its stones
	 * 
	 * @return copy of stones??
	 */
	public Board deepCopy() {
		return null;
	}

	/**
	 * Checks if the move is valid??
	 * 
	 * @param row
	 * @param column
	 * @return true if ??, false otherwise
	 */
	public boolean isValidMove(int row, int column) {
		return false;
	}

	/**
	 * Makes the move ??
	 * 
	 * @param row
	 * @param column
	 */
	public void makeMove(int row, int column) {
		if (isValidMove(row, column)) {

		}
	}

	/**
	 * Makes the specified move and updates the possiblemoves accordingly
	 * 
	 * @param stone
	 * @param place
	 */
	//@ requires possibleMoves.contains(place) && place.acceptable(stone));
	public void makeMove(Stone stone, PossibleMove place) {
		stone = place.fill(stone);
		stones.put(stone.getPosition(), stone);
		// update all possible moves in row and column of place.
		// create new possiblemoves (if no stone is there) above, below, right
		// and left of stone
		possibleMoves.remove(place);
	}

	/**
	 * Adds a possiblemove on the specified position to possiblemoves
	 * 
	 * @param pos
	 */
	public void addPossibleMove(Position pos) {
		PossibleMove newPM = new PossibleMove(Stone.Shape.values(), Stone.Color.values());
		Stone above;
		Stone below;
		Stone right;
		Stone left;
		if ((above = stones.get(new Position(pos.getX(), pos.getY() - 1))) != null) {
			newPM.addColumn(above.getColumn());
		}
		if ((below = stones.get(new Position(pos.getX(), pos.getY() + 1))) != null) {
			newPM.addColumn(below.getColumn());
		}
		if ((right = stones.get(new Position(pos.getX() + 1, pos.getY()))) != null) {
			newPM.addRow(right.getRow());
		}
		if ((left = stones.get(new Position(pos.getX() - 1, pos.getY()))) != null) {
			newPM.addRow(left.getRow());
		}
		if (newPM.updatePossibilities() != 0) {
			possibleMoves.put(newPM.getPosition(), newPM);
		}
	}

	/**
	 * Sets the possiblemoves to a String-representation with each possiblemove
	 * accompanied by a number, starting with 0, so the human player can choose
	 * the move he wants to make by entering that number
	 */
	public String toString() {
		String movesList = "";
		int i = 0;
		for (Position p : possibleMoves.keySet()) {
			String moveNumber = i + " = " + possibleMoves.get(p).toString();
			i = i + 1;
			movesList = movesList + moveNumber + ", ";
		}
		return movesList;
	}
}
