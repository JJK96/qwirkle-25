package shared;

import org.apache.commons.lang3.StringUtils;

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
	 * Resets the board to the state at the beginning of the game.
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
	// @ ensures \result == getPossibleMoves();
	public /* @ pure */ Map<Position, PossibleMove> getPossibleMoves() {
		return possibleMoves;
	}

	/**
	 * Get all the stones on the board with their position.
	 * 
	 * @return map of stones and their positions
	 */
	// @ ensures \result == getStones();
	public /* @ pure */ Map<Position, Stone> getStones() {
		return this.stones;
	}

	/**
	 * Get a deep copy of the board.
	 * 
	 * @return a copy of the board
	 */
	// @ ensures \result.getStones().containsAll(getBackup());
	public /* @ pure */ Board deepCopy() {
		Board b = new Board();
		for (Stone s : backup) {
			Stone newS = new Stone(s.getShape(), s.getColor());
			b.makeMove(s.getPosition(), newS);
		}
		return b;
	}

	public /* @ pure */ List<Stone> getBackup() {
		return backup;
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
	public /* @ pure */ boolean isValidMove(int x, int y, Stone stone) {
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
	public /* @ pure */ boolean isValidMove(Position p, Stone stone) {
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
	public /* @ pure */ boolean isValidMove(PossibleMove p, Stone stone) {
		return p != null && p.acceptable(stone);
	}

	/**
	 * places the gien stone at the given place.
	 *
	 * @param x
	 * @param y
	 * @param stone
	 */
	// @ requires isValidMove(x,y,stone);
	public void makeMove(int x, int y, Stone stone) {
		if (isValidMove(x, y, stone)) {
			makeMove(stone, possibleMoves.get(new Position(x, y)));
		}
	}

	/**
	 * places the given stone at the given position.
	 * 
	 * @param p
	 * @param stone
	 */
	// @ requires isValidMove(p, stone);
	public void makeMove(Position p, Stone stone) {
		makeMove(stone, possibleMoves.get(p));
	}

	/**
	 * makes the given moves on the board, if an invalid move is found, the
	 * board should be replaced by it's deepcopy.
	 * 
	 * @param positions
	 * @param
	 * @throws InvalidMoveException
	 */
	/*
	 * @ requires stones.size() == positions.size(); ensures
	 * stones.containsAll(stoneList) || (\forall int i; i<stoneList.size();
	 * !backup.contains(stoneList.get(i)));
	 */
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

	/**
	 * checks if the given positions belong to the same column.
	 * 
	 * @param positions
	 * @return true if the stones have the same x value and there are no gaps
	 *         between the stones that are not filled by stones that are already
	 *         on the board.
	 */
	public /* @ pure */ boolean sameColumn(List<Position> positions) {
		boolean allX = true;
		int x = positions.get(0).getX();
		for (Position p : positions) {
			if (p.getX() != x) {
				allX = false;
			}
		}
		return allX && connectedColumn(positions);
	}

	/**
	 * checks if the given positions belong to the same row.
	 * 
	 * @param positions
	 * @return true if the stones have the same y value and there are no gaps
	 *         between the stones that are not filled by stones that are already
	 *         on the board.
	 */
	public /* @ pure */ boolean sameRow(List<Position> positions) {
		boolean allY = true;
		int y = positions.get(0).getY();
		for (Position p : positions) {
			if (p.getY() != y) {
				allY = false;
			}
		}
		return allY && connectedRow(positions);
	}

	/**
	 * checks if the positions are either in the same column or the same row.
	 * 
	 * @param positions
	 * @return true if sameRow() or sameColumn() is true
	 */
	public /* @ pure */ boolean allStonesOneRow(List<Position> positions) {
		return sameRow(positions) || sameColumn(positions);
	}

	/**
	 * checks if the spaces are in the same row or column.
	 * 
	 * @param spaces
	 * @return true if allStonesOneRow is true;
	 */
	public /* @ pure */ boolean allOneRow(List<Space> spaces) {
		List<Position> positionList = new ArrayList<Position>();
		for (Space s : spaces) {
			positionList.add(s.getPosition());
		}
		return allStonesOneRow(positionList);
	}

	/**
	 * checks if the positions in the row have no gaps between them that are not
	 * filled by the board.
	 * 
	 * @param
	 * @return true if the stones are in the same row/column and have no gaps
	 *         between them that are not filled by the board.
	 */
	/*
	 * @ requires (\forall int i; i<positions.size(); positions.get(i).getY() ==
	 * positions.get(0).get(i).getY());
	 */
	public /* @ pure */ boolean connectedRow(List<Position> positions) {
		int y = positions.get(0).getY();
		int low = positions.get(0).getX();
		int high = low;
		for (Position p : positions) {
			int x = p.getX();
			if (x < low) {
				low = x;
			}
			if (x > high) {
				high = x;
			}
		}
		for (int i = low; i <= high; i++) {
			Position p = new Position(i, y);
			if (!(positions.contains(p) || stones.get(p) != null)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * checks if the positions in the column have no gaps between them that are
	 * not filled by the board.
	 * 
	 * @param positions
	 * @return
	 */
	/*
	 * @ requires (\forall int i; i<positions.size();positions.get(i).getX() ==
	 * positions.get(0).get(i).getX());
	 */
	public /* @ pure */ boolean connectedColumn(List<Position> positions) {
		int x = positions.get(0).getX();
		int low = positions.get(0).getY();
		int high = low;
		for (Position p : positions) {
			int y = p.getY();
			if (y < low) {
				low = y;
			}
			if (y > high) {
				high = y;
			}
		}
		for (int i = low; i <= high; i++) {
			Position p = new Position(x, i);
			if (!(positions.contains(p) || stones.get(p) != null)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Places the stone on the position of the possiblemove on the board.
	 *
	 * @param stone
	 * @param place
	 */
	// @ requires possibleMoves.contains(place) && place.acceptable(stone));
	public void makeMove(Stone stone, PossibleMove place) {
		Stone stoneToMove = stone;
		stoneToMove = place.fill(stoneToMove);
		stones.put(stoneToMove.getPosition(), stoneToMove);
		possibleMoves.remove(place.getPosition());
		List<Position> toAdd = new ArrayList<Position>();
		for (Position p : possibleMoves.keySet()) {
			if (p.getX() == stoneToMove.getPosition().getX() 
							|| p.getY() == stoneToMove.getPosition().getY()) {
				toAdd.add(p);
			}
		}
		for (Position p : toAdd) {
			addPossibleMove(p);
		}
		Position pos = stoneToMove.getPosition();
		addPossibleMove(pos.above());
		addPossibleMove(pos.below());
		addPossibleMove(pos.right());
		addPossibleMove(pos.left());
		backup.add(stoneToMove);
	}

	/**
	 * Adds a possiblemove on the specified position.
	 *
	 * It gets the rows and columns from all stones around. Next it is checked
	 * if the possibleMove can be placed upon. If this is the case the
	 * possibleMove is added to the list.
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
			above = stones.get(new Position(pos.getX(), pos.getY() - 1));
			if (above != null) {
				newPM.addColumn(above.getColumn());
			}
			below = stones.get(new Position(pos.getX(), pos.getY() + 1));
			if (below != null) {
				newPM.addColumn(below.getColumn());
			}
			right = stones.get(new Position(pos.getX() + 1, pos.getY()));
			if (right != null) {
				newPM.addRow(right.getRow());
			}
			left = stones.get(new Position(pos.getX() - 1, pos.getY()));
			if (left != null) {
				newPM.addRow(left.getRow());
			}
			if (newPM.updatePossibilities() != 0) {
				possibleMoves.put(newPM.getPosition(), newPM);
			} else {
				possibleMoves.remove(newPM.getPosition());
			}
		}
	}

	/**
	 * Get the boundaries of the game (the bigness of the board, measured in
	 * biggest and smallest X and Y coordinates).
	 * 
	 * @return Biggest and Smallest X and Y
	 */
	private/* @ pure */ int[] getBoundaries() {
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
	public /* @ pure */ String toString() {
		return toString(new ArrayList<PossibleMove>());
	}

	/**
	 * Creates a string containing the board and the specified possibleMoves.
	 * 
	 * @param pmlist
	 * @return a string containing the boarrd and the possibleMoves.
	 */
	public /* @ pure */ String toString(List<PossibleMove> pmlist) {
		int width = 5;
		int[] boundaries = getBoundaries();
		boundaries[0] -= 1;
		boundaries[1] -= 1;
		boundaries[2] += 1;
		boundaries[3] += 1;
		String res = "     ";
		for (int k = boundaries[0]; k <= boundaries[2]; k++) {
			res += StringUtils.center(Integer.toString(k), width);
		}
		res += "\n";
		for (int i = boundaries[1]; i <= boundaries[3]; i++) {
			res += StringUtils.rightPad(Integer.toString(i), width);
			for (int j = boundaries[0]; j <= boundaries[2]; j++) {
				boolean placeEmpty = true;
				Position pos = new Position(j, i);
				Stone s = stones.get(pos);
				if (s != null) {
					res += s;
					placeEmpty = false;
				} else {
					for (int pmIt = 0; pmIt < pmlist.size(); pmIt++) {
						PossibleMove p = pmlist.get(pmIt);
						if (p.getPosition().equals(pos)) {
							res += StringUtils.center("(" + pmIt + ")", width);
							placeEmpty = false;
							break;
						}
					}
				}
				if (placeEmpty) {
					res += "     ";
				}
			}
			res += "\n";
		}
		return res;
	}

	/**
	 * Calculates how many points the given move is worth.
	 * 
	 * @param stonelist
	 * @param positionlist
	 * @return the points.
	 */
	// @ requires (\forall int i=0; i<stonelist.size();
	// stonelist.get(i).isOnBoard());
	public /* @ pure */ int calculatePoints(List<Stone> stonelist, List<Position> positionlist) {
		int points = 0;
		if (sameRow(positionlist)) {
			int rowsize = stonelist.get(0).getRow().size();
			if (rowsize == Stone.Color.values().length) {
				points += 2 * rowsize;
			} else if (rowsize > 1) {
				points += rowsize;
			}
			for (Stone s : stonelist) {
				int columnsize = s.getColumn().size();
				if (columnsize == Stone.Color.values().length) {
					points += 2 * columnsize;
				} else if (columnsize > 1) {
					points += columnsize;
				}
			}
		} else if (sameColumn(positionlist)) {
			int columnsize = stonelist.get(0).getColumn().size();
			if (columnsize == Stone.Color.values().length) {
				points += 2 * columnsize;
			} else if (columnsize > 1) {
				points += columnsize;
			}
			for (Stone s : stonelist) {
				int rowsize = s.getRow().size();
				if (rowsize == Stone.Color.values().length) {
					points += 2 * rowsize;
				} else if (rowsize > 1) {
					points += rowsize;
				}
			}
		}
		return points;
	}
}
