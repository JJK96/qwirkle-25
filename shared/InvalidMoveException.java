package shared;

/**
 * Created by jjk on 1/19/16.
 */
public class InvalidMoveException extends Exception{
    public InvalidMoveException() {
        super("invalid move");
    }
}
