package shared;

public class InvalidStoneException extends Exception {

	public InvalidStoneException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidStoneException(String message) {
		super("Invalid stone: " + message);
		// TODO Auto-generated constructor stub
	}

	public InvalidStoneException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidStoneException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidStoneException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
