package server;

import java.util.HashMap;
import java.util.Map;

public class Board {

	private Map<Position, Stone> stones;
	private Map<Position, PossibleMove> possibleMoves;

	public Board() {
		reset();
	}

	public void reset() {
		stones = new HashMap<Position, Stone>();
		possibleMoves = new HashMap<Position, PossibleMove>();
		PossibleMove init = new PossibleMove(Stone.Shape.values(), Stone.Color.values());
		init.setPosition(new Position(0, 0));
		possibleMoves.put(init.getPosition(), init);
	}

	public Map<Position, PossibleMove> getPossibleMoves() {
		return possibleMoves;
	}

	public void update() {

	}

	public Map<Position, Stone> getStones() {
		return this.stones;
	}

	public Board deepCopy() {
		return null;
	}

	public boolean isValidMove(int row, int column) {
		return false;
	}

	public void makeMove(int row, int column) {
		if (isValidMove(row, column)) {

		}
	}

	//@ requires possibleMoves.contains(place) && place.acceptable(stone));
	public void makeMove(Stone stone, PossibleMove place) {
		stone = place.fill(stone);
		stones.put(stone.getPosition(), stone);
		// update all possible moves in row and column of place.
		// create new possiblemoves (if no stone is there) above, below, right
		// and left of stone
		possibleMoves.remove(place);
	}

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

	public String toString() {
		return null;
	}
}
