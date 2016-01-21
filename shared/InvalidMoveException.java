package shared;

/**
 * Created by jjk on 1/19/16.
 */
public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMoveException() {
		super("invalid move");
	}
}
