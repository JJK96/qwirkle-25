package shared;

public class InvalidStoneException extends Exception {

	public InvalidStoneException(String message) {
		super("Invalid stone: " + message);
	}
}
