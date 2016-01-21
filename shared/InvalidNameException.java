package shared;

/**
 * Created by jjk on 1/18/16.
 */
public class InvalidNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidNameException(String name) {
		super("Name invalid or not unique: " + name);
	}
}
