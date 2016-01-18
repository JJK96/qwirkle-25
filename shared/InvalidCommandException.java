package shared;

/**
 * Created by jjk on 1/18/16.
 */
public class InvalidCommandException extends Exception{

    public InvalidCommandException(String msg) {
        super("invalid command given: " + msg);
    }
}
