package shared;

public class Stone extends Space {
	public enum Color {
		R, O, B, Y, G, P
	};

	public enum Shape {
        o, d, v, c, x, s
	};

	private Shape shape;
	private Color color;
	private boolean isOnBoard;

	/**
	 * Creates a stone with the specified shape and color
	 * 
	 * @param shape
	 * @param color
	 */
	public Stone(Shape shape, Color color) {
        super();
		this.shape = shape;
		this.color = color;
		isOnBoard = false;
	}

	/**
	 * Checks if the stone is on the board
	 * 
	 * @return true if the stone is on the board, otherwise false
	 */
	public boolean isOnBoard() {
		return isOnBoard;
	}

	/**
	 * Gets the shape of the stone
	 * 
	 * @return shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Gets the color of the stone
	 * 
	 * @return color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * If the stone gets placed the boolean isOnBoard is set to true
	 */
	public void place() {
		isOnBoard = true;
	}

	@Override
	public String toString() {
		return "[" + shape.ordinal() + "," + color.ordinal() + "]";
	}
	
	public String toUsableString() {
		return shape.ordinal() + "," + color.ordinal();
	}

	@Override
	public boolean equals(Object obj) {
		return (((Stone) obj).getColor() == this.getColor()) && (((Stone) obj).getShape() == this.getShape());
	}
}
