package server;

/**
 * Created by jjk on 1/14/16.
 */
public class Protocol {
	public static final String REGISTER = "hello";
	public static final String SPLIT = " ";
	public static final String ACKNOWLEDGE = "hello_from_the_other_side";
	public static final String ERROR = "error";
	public static final String JOINLOBBY = "joinlobby";
	public static final String PLAYERS = "players";
	public static final String JOINAANTAL = "join";
	public static final String START = "start";
	public static final String CHAT = "chat";
	public static final String MSG = "msg"; // van server naar game/lobby "msgspeler bericht..."
	public static final String CHATPM = "chatpm";
	public static final String MSGPM = "msgpm";
	public static final String PLACE = "place";
	public static final String PLACED = "placed";
	public static final String TRADE = "trade";
	public static final String NEWSTONES = "newstones";
	public static final String TRADED = "traded";
	public static final String TURN = "turn";
	public static final String ENDGAME = "endgame";
	public static final String CHALLENGE = "challenge";
	public static final String NEWCHALLENGE = "newchallenge";
	public static final String ACCEPT = "accept";
	public static final String DECLINE = "decline";

}
