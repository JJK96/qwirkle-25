package client;

import java.util.ArrayList;
import shared.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientBoard {

	private Map<Position, Stone> stones;
	private Map<Position, PossibleMove> possibleMoves;

	/**
	 * Creates a new board
	 */
	public ClientBoard() {
		reset();
	}

	/**
	 * Resets the board to the state at the begin of the game
	 */
	public void reset() {
		stones = new HashMap<Position, Stone>();
		possibleMoves = new HashMap<Position, PossibleMove>();
		PossibleMove init = new PossibleMove(new Position(0, 0));
		possibleMoves.put(init.getPosition(), init);
	}

	/**
	 * Get the possiblemoves
	 * 
	 * @return a map of possiblemoves
	 */
	public Map<Position, PossibleMove> getPossibleMoves() {
		return possibleMoves;
	}

	/**
	 * Update??
	 */
	public void update() {

	}

	/**
	 * Get all the stones on the board with their position
	 * 
	 * @return map of stones and their positions
	 */
	public Map<Position, Stone> getStones() {
		return this.stones;
	}

	/**
	 * Get a copy of the board ??
	 * 
	 * @return a copy of the board ??
	 */
	public ClientBoard deepCopy() {
		return null;
	}

	/**
	 * Checks if the specified stone can be placed on the spefified positions
	 * according to the board
	 * 
	 * @param x
	 * @param y
	 * @param stone
	 * @return true if the move is valid, otherwise false
	 */
	public boolean isValidMove(int x, int y, Stone stone) {
		PossibleMove p = possibleMoves.get(new Position(x, y));
		return p != null && p.acceptable(stone);
	}

	/**
	 * Make a move at the spefified position with the spefified stone if that is
	 * valid
	 * 
	 * @param x
	 * @param y
	 * @param stone
	 */
	public void makeMove(int x, int y, Stone stone) {
		if (isValidMove(x, y, stone)) {
			makeMove(stone, possibleMoves.get(new Position(x, y)));
		}
	}

	/**
	 * Places the stone on the position of the possiblemove on the board
	 * 
	 * @param stone
	 * @param place
	 */
	//@ requires possibleMoves.contains(place) && place.acceptable(stone));
	public void makeMove(Stone stone, PossibleMove place) {
		stone = place.fill(stone);
		stones.put(stone.getPosition(), stone);
		possibleMoves.remove(place.getPosition());
		for (Position p : possibleMoves.keySet()) {
			if (p.getX() == stone.getPosition().getX() || p.getY() == stone.getPosition().getY()) {
				addPossibleMove(p);
			}
		}
		Position pos = stone.getPosition();
		addPossibleMove(pos.above());
		addPossibleMove(pos.below());
		addPossibleMove(pos.right());
		addPossibleMove(pos.left());
	}

	/**
	 * Adds a possiblemove on the specified position ??
	 * @param pos
	 */
	public void addPossibleMove(Position pos) {
		if (!stones.keySet().contains(pos)) {
			PossibleMove newPM = new PossibleMove(pos);
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
	}

	/**
	 * Get the boundaries of the game 
	 * (the bigness of the board, measured in biggest and smalles X and Y coordinates)
	 * @return Biggest and Smallest X and Y
	 */
	public int[] getBoundaries() {
		int SmallestX = 0;
		int BiggestX = 0;
		int SmallestY = 0;
		int BiggestY = 0;
		for (Position p : stones.keySet()) {
			if (p.getX() > BiggestX) {
				BiggestX = p.getX();
			}
			if (p.getX() < SmallestX) {
				SmallestX = p.getX();
			}
			if (p.getY() > BiggestY) {
				BiggestY = p.getY();
			}
			if (p.getY() < SmallestY) {
				SmallestY = p.getY();
			}
		}
		return new int[] { SmallestX, SmallestY, BiggestX, BiggestY };
	}

	/**
	 * Sets the board and all the possiblemoves to a string representation for the TUI.
	 * The board is first in the string, then an enter, then the possiblemoves numbered from 0.
	 */
	public String toString() {
		int[] boundaries = getBoundaries();
		String res = "";
		for (int i = boundaries[1]; i <= boundaries[3]; i++) {
			for (int j = boundaries[0]; j <= boundaries[2]; j++) {
				Stone s = null;
				if ((s = stones.get(new Position(j, i))) != null) {
					res += s;
				} else {
					res += "     ";
				}
			}
			res += "\n";
		}
		res += "\nPossible moves: ";
		Object[] possibleMovesArray = possibleMoves.values().toArray();
		for (int i = 0; i < possibleMovesArray.length; i++) {
			res += "\n" + i + " : " + possibleMovesArray[i];
		}
		return res;
	}
}
