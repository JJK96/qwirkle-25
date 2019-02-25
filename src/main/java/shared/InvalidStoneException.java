package shared;

public class InvalidStoneException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidStoneException(String message) {
		super("Invalid stone: " + message);
	}
}
