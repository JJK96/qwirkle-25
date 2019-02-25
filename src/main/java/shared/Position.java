package shared;

/**
 * Created by jjk on 1/12/16.
 */
public class Position {
	private int x;
	private int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * The position above this one.
	 * @return
     */
	public Position above() {
		return new Position(x, y - 1);
	}

	/**
	 * The position below this one.
	 * @return
     */
	public Position below() {
		return new Position(x, y + 1);
	}

	/**
	 * The position to the right of this one.
	 * @return
     */
	public Position right() {
		return new Position(x + 1, y);
	}

	/**
	 * The position to the left of this one.
	 * @return
     */
	public Position left() {
		return new Position(x - 1, y);
	}

	/**
	 * A new hash function that relies only on the x and y coordinates of the position.
	 * This was necessary so that the positions could be used as keys for a hashmap.
	 * @return and integer that is specific for this x and y.
     */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + this.x;
		hash = 71 * hash + this.y;
		return hash;
	}

	/**
	 * Makes the equals method only check if the X and Y coordinates are equal.
	 * @param obj
	 * @return
     */
	@Override
	public boolean equals(Object obj) {
		Position p = (Position) obj;
		return (p.getX() == getX()) && (p.getY() == getY());
	}

	/**
	 * @return the visual representation of this position.
     */
	@Override
	public String toString() {
		return "(" + toUsableString() + ")";
	}

	/**
	 * @return the encoding of this position according to the protocol.
     */
	public String toUsableString() {
		return getX() + "," + getY();
	}
}
