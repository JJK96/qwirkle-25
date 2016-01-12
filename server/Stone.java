package server;

public class Stone extends Space {
    public enum Shape {ZERO, ONE, TWO, THREE, FOUR, FIVE};
    public enum Color {ZERO, ONE, TWO, THREE, FOUR, FIVE};

    private Shape shape;
    private Color color;
    private boolean isOnBoard;

	public Stone(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
        isOnBoard = false;
	}

    public boolean isOnBoard() {
        return isOnBoard;
    }

    public Shape getShape() {
       return shape;
    }
    public Color getColor() {
        return color;
    }
    public void place() {
        isOnBoard = true;
    }
}
