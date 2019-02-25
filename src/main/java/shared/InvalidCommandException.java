package shared;

/**
 * Created by jjk on 1/18/16.
 */
public class InvalidCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCommandException(String msg) {
		super("invalid command given: " + msg);
	}

	public InvalidCommandException() {
		super();
	}
}
