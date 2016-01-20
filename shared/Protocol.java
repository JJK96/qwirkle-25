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
	public static final String WHICHPLAYERS = "players?";
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
	public static final String BORDER = "\n\n=================================================================\n\n";

	public enum errorcode {
		WRONGCOMMAND, WRONGTURN, INVALIDNAME, PLAYERDISCONNECTED, MISSINGOPTION
	}

	/**
	 * converts the received newstones command to a list of stones
	 * 
	 * @param inputArray
	 * @return
	 */
	//@ requires inputArray.length >= 2;
	public static List<Stone> StringToStonelist(String[] inputArray) throws InvalidStoneException {
		List<Stone> stones = new ArrayList<Stone>();
		for (int i = 1; i < inputArray.length; i++) {
            Stone stone = intsToStone(inputArray[i]);
            stones.add(stone);
		}
		return stones;
	}

	/**
	 * converts stones from the string format to the stone object.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static Stone intsToStone(String input) throws InvalidStoneException {
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

	/**
	 * gets the received place command converted to array as input returns an
	 * list of all stones placed.
	 * 
	 * @param inputArray
	 * @return
	 */
	/*
	 * @ requires inputArray.length >= 3 && inputArray.length % 2 == 1; ensures
	 * \result.size() == (inputArray.length -1) / 2;
	 */
	public static List<Stone> StringToPlacedStonelist(String[] inputArray) throws InvalidCommandException {
		List<Stone> stones = new ArrayList<Stone>();
		for (int i = 1; i < inputArray.length - 1; i += 2) {
			try {
				Stone stone = intsToStone(inputArray[i]);
				stones.add(stone);
			} catch (InvalidStoneException e) {
				throw new InvalidCommandException();
			}
		}
		return stones;
	}

	/**
	 * gets the received place command converted to array as input returns a
	 * list of all positions where the stones are to be placed.
	 */
	/*
	 * @ requires inputArray.length >= 3 && inputArray.length % 2 == 1; ensures
	 * \result.size() == (inputArray.length -1) / 2;
	 */

	public static List<Position> StringToPlacedPositionlist(String[] inputArray) throws InvalidCommandException {
		List<Position> positions = new ArrayList<Position>();
		for (int i = 2; i < inputArray.length; i += 2) {
			String[] xy = inputArray[i].split(",");
			try {
				int x = Integer.parseInt(xy[0]);
				int y = Integer.parseInt(xy[1]);
				positions.add(new Position(x, y));
			} catch (NumberFormatException e) {
				throw new InvalidCommandException();
			}
		}
		return positions;
	}

	/**
	 * Converts the coordinates from inputArray to an array x's of the stones to
	 * be placed
	 * 
	 * @param inputArray
	 * @return array of Y coordinates
	 */
	public static int[] convertPlacedX(String[] inputArray) {
		int[] x = new int[inputArray.length - 1];
		for (int i = 0; i < inputArray.length; i += 2) {
			try {
				x[i] = intsToY(inputArray[i]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return x;
	}

	/**
	 * Converts the coordinates from inputArray to an array y's of the stones to
	 * be placed
	 * 
	 * @param inputArray
	 * @return array of X coordinates
	 */
	public static int[] convertPlacedY(String[] inputArray) {
		int[] y = new int[inputArray.length - 1];
		for (int i = 0; i < inputArray.length; i += 2) {
			try {
				y[i] = intsToY(inputArray[i]);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return y;
	}

	/**
	 * converts the first half of the input int,int to a usable int as X
	 * coordinate
	 * 
	 * @param input
	 * @return
	 */
	public static int intsToX(String input) {
		String[] array = input.split(",");
		return Integer.parseInt(array[0]);
	}

	/**
	 * converts the second half of the input int,int to a usable int as Y
	 * coordinate
	 * 
	 * @param input
	 * @return
	 */
	public static int intsToY(String input) {
		String[] array = input.split(",");
		return Integer.parseInt(array[1]);

	}
}
