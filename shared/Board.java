package shared;

import java.util.*;

public class Board {

	private Map<Position, Stone> stones;
	private Map<Position, PossibleMove> possibleMoves;
	private List<Stone> backup;

	/**
	 * Creates a new board.
	 */
	public Board() {
		reset();
	}

	/**
	 * Resets the board to the state at the begin of the game.
	 */
	public void reset() {
		stones = new HashMap<Position, Stone>();
		possibleMoves = new HashMap<Position, PossibleMove>();
		backup = new ArrayList<Stone>();
		PossibleMove init = new PossibleMove(new Position(0, 0));
		possibleMoves.put(init.getPosition(), init);
	}

	/**
	 * Get the possiblemoves.
	 * 
	 * @return a map of possiblemoves
	 */
	public Map<Position, PossibleMove> getPossibleMoves() {
		return possibleMoves;
	}

	/**
	 * Get all the stones on the board with their position.
	 * 
	 * @return map of stones and their positions
	 */
	public Map<Position, Stone> getStones() {
		return this.stones;
	}

	/**
	 * Get a copy of the board.
	 * 
	 * @return a copy of the board
	 */
	public Board deepCopy() {
		Board b = new Board();
		for (Stone s : backup) {
			Stone newS = new Stone(s.getShape(), s.getColor());
			b.makeMove(s.getPosition(), newS);
		}
		return b;
	}

	/**
	 * Checks if the specified stone can be placed on the specified positions
	 * according to the board.
	 * 
	 * @param x
	 * @param y
	 * @param stone
	 * @return true if the move is valid, otherwise false
	 */
	public boolean isValidMove(int x, int y, Stone stone) {
		Position p = new Position(x, y);
		return isValidMove(p, stone);
	}

	/**
	 * Checks if the specified stone can be placed on the specified position
	 * according to the board.
	 * 
	 * @param p
	 * @param stone
	 * @return
	 */
	public boolean isValidMove(Position p, Stone stone) {
		PossibleMove pm = possibleMoves.get(p);
		return isValidMove(pm, stone);
	}

	/**
	 * Checks if the specified stone can be placed on the specified posiblemove
	 * according to the board.
	 * 
	 * @param p
	 * @param stone
	 * @return
	 */
	public boolean isValidMove(PossibleMove p, Stone stone) {
		return p != null && p.acceptable(stone);
	}

	/**
	 * Make a move at the spefified position with the spefified stone if that is
	 * valid.
	 * 
	 * @param x
	 * @param y
	 * @param stone
	 */
	//@ requires isValidMove(x,y,stone);
	public void makeMove(int x, int y, Stone stone) {
		if (isValidMove(x, y, stone)) {
			makeMove(stone, possibleMoves.get(new Position(x, y)));
		}
	}

	//@ requires isValidMove(p, stone);
	public void makeMove(Position p, Stone stone) {
		makeMove(stone, possibleMoves.get(p));
	}

	/**
	 * makes the given moves on the board, if an invalid move is found, 
	 * the board should be replaced by it's deepcopy.
	 * 
	 * @param positions
	 * @param
	 * @throws InvalidMoveException
     */
	//@ requires stones.size() == positions.size();
	public void makeMoves(List<Position> positions, List<Stone> stonesList)
			throws InvalidMoveException {
		if (allStonesOneRow(positions)) {
			int movesmade = 0;
			while (movesmade < positions.size()) {
				boolean validmovefound = false;
				for (int j = 0; j < positions.size(); j++) {
					Position p = positions.get(j);
					Stone s = stonesList.get(j);
					if (isValidMove(p, s)) {
						makeMove(p, s);
						movesmade += 1;
						validmovefound = true;
					}
				}
				if (!validmovefound) {
					for (int i = 0; i < movesmade; i++) {
						backup.remove(backup.size() - 1);
					}
					throw new InvalidMoveException();
				}
			}
		} else {
			throw new InvalidMoveException();
		}
	}

	public boolean sameRow(List<Position> positions) {
		boolean allX = true;
		int x = positions.get(0).getX();
		for (Position p : positions) {
			if (p.getX() != x) {
				allX = false;
			}
		}
		return allX;
	}

	public boolean sameColumn(List<Position> positions) {
		boolean allY = true;
		int y = positions.get(0).getY();
		for (Position p : positions) {
			if (p.getY() != y) {
				allY = false;
			}
		}
		return allY;
	}

	public boolean allStonesOneRow(List<Position> positions) {
		return sameRow(positions) || sameColumn(positions);
	}

	public boolean allOneRow(List<Space> spaces) {
		List<Position> positionList = new ArrayList<>();
		for (Space s : spaces) {
			positionList.add(s.getPosition());
		}
		return allStonesOneRow(positionList);
	}
	/**
	 * Places the stone on the position of the possiblemove on the board.
	 * 
	 * @param stone
	 * @param place
	 */
	//@ requires possibleMoves.contains(place) && place.acceptable(stone));
	public void makeMove(Stone stone, PossibleMove place) {
		Stone stoneToMove = stone;
		stoneToMove = place.fill(stone);
		stones.put(stoneToMove.getPosition(), stoneToMove);
		possibleMoves.remove(place.getPosition());
		for (Position p : possibleMoves.keySet()) {
			if (p.getX() == stoneToMove.getPosition().getX() ||
							p.getY() == stoneToMove.getPosition().getY()) {
				addPossibleMove(p);
			}
		}
		Position pos = stoneToMove.getPosition();
		addPossibleMove(pos.above());
		addPossibleMove(pos.below());
		addPossibleMove(pos.right());
		addPossibleMove(pos.left());
		backup.add(stoneToMove);
	}

	/**
	 * Adds a possiblemove on the specified position ??
	 * 
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
				// 
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
	 * Get the boundaries of the game (the bigness of the board, measured in
	 * biggest and smalles X and Y coordinates).
	 * 
	 * @return Biggest and Smallest X and Y
	 */
	public int[] getBoundaries() {
		int smallestX = 0;
		int biggestX = 0;
		int smallestY = 0;
		int biggestY = 0;
		for (Position p : stones.keySet()) {
			if (p.getX() > biggestX) {
				biggestX = p.getX();
			}
			if (p.getX() < smallestX) {
				smallestX = p.getX();
			}
			if (p.getY() > biggestY) {
				biggestY = p.getY();
			}
			if (p.getY() < smallestY) {
				smallestY = p.getY();
			}
		}
		return new int[] {smallestX, smallestY, biggestX, biggestY};
	}

	/**
	 * Sets the board and all the possiblemoves to a string representation for
	 * the TUI. The board is first in the string, then an enter, then the
	 * possiblemoves numbered from 0.
	 */
	public String toString() {
		int[] boundaries = getBoundaries();
		String res = "   ";
		for (int k = boundaries[0]; k <= boundaries[2]; k++) {
			res += "  " + k + "  ";
		}
		res += "\n" + boundaries[1];
		res += (boundaries[1] < 0) ? " " : "  ";
		for (int i = boundaries[1]; i <= boundaries[3]; i++) {
			for (int j = boundaries[0]; j <= boundaries[2]; j++) {
				Stone s = null;
				if ((s = stones.get(new Position(j, i))) != null) {
					res += s;
				} else {
					res += "     ";
				}
			}
			int nextnumber = i + 1;
			res += "\n" + nextnumber;
			res += (nextnumber < 0) ? " " : "  ";
		}
		return res;
	}
}
