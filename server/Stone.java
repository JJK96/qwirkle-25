package server;

public class Stone {

	private int shape;
	private int color;
	private int positionY;
	private int positionX;

	public Stone(int shape, int color, int Y, int X) {
		this.shape = shape;
		this.color = color;
		this.positionX = X;
		this.positionY = Y;
	}

	public int getShape() {
		return shape;
	}

	public int getColor() {
		return color;
	}

}
