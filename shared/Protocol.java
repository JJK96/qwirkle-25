package shared;

import java.util.ArrayList;
import java.util.List;
import shared.Stone.Color;
import shared.Stone.Shape;



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
	public static final String MSG = "msg";
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

	public static List<Stone> convertStones(String[] inputArray) {
		List<Stone> stones = new ArrayList<Stone>();
		for (int i = 1; i < inputArray.length; i++) {
			try {
				Stone stone = intsToStone(inputArray[i]);
				stones.add(stone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stones;
	}

	public static Stone intsToStone(String input) throws Exception {
		String[] array = input.split(",");
		int shape = Integer.parseInt(array[0]);
		int color = Integer.parseInt(array[1]);
		if (shape >= 0 && shape <= Stone.Shape.values().length && color >= 0 && color <= Stone.Color.values().length) {
			Shape[] shapes = Shape.values();
			Color[] colors = Color.values();
			Stone stone = new Stone(shapes[shape], colors[color]);
			return stone;

		} else
			throw new InvalidStoneException(input);
	}

	public static Position intsToPosition(String input) {
		String[] array = input.split(",");
		int x = Integer.parseInt(array[0]);
		int y = Integer.parseInt(array[1]);
		Position position = new Position(x, y);
		return position;
	}

	public static List<Stone> convertPlacedStones(String[] inputArray) {
		List<Stone> stones = new ArrayList<Stone>();
		for (int i = 1; i < inputArray.length; i += 2) {
			try {
				Stone stone = intsToStone(inputArray[i]);
				stones.add(stone);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stones;
	}

	public static List<Position> convertPlacedPositions(String[] inputArray) {
		List<Position> positions = new ArrayList<Position>();
		for (int i = 0; i < inputArray.length; i += 2) {
			try {
				Position position = intsToPosition(inputArray[i]);
				positions.add(position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return positions;
	}
}
