package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import shared.*;

public class View implements Observer {

	private Client client;

	/**
	 * Creates a view for the specified client.
	 * 
	 * @param
	 */
	public View(Client client) {
		this.client = client;
	}

	public InetAddress getHostName() throws UnknownHostException {
		String hostname = readString("Enter hostname of the server:");
		InetAddress result = InetAddress.getByName(hostname);
		return result;
	}

	public int getPort() {
		int port = readInt("Enter port number:");
		return port;
	}

	/**
	 * Asks the person who sets up the client if he wants to play himself or
	 * want a computerplayer to play.
	 * 
	 * @return true if he wants a computerplayer, false if he wants to play as
	 *         humanplayer himself.
	 */
	public boolean askHumanOrComputerPlayer() {
		String prompt = Protocol.BORDER + "Do you want to play as human or shall "
						+ "a computerplayer play?\n0 : HumanPlayer.\n1 : ComputerPlayer.";
		while (true) {
			int bool = readInt(prompt);

			if (bool != 1 && bool != 0) {
				System.out.println("invalid choice");
			} else if (bool == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * Asks the person which strategy the computerplayer shall have.
	 * 
	 * @return the strategy chosen.
	 */
	public Strategy getStrategyFromInput() {
		String prompt = Protocol.BORDER + "Which strategy shall the ComputerPlayer have?"
						+ "\n0 : BadStrategy.\n\nMore options will follow.";
		int strat = 0;
		while (true) {
			strat = readInt(prompt);
			if (strat != 0) {
				System.out.println("invalid choice");
			} else if (strat == 0) {
				return new BadStrategy();
			} // else als er meer strats zijn enzoooo
		}
	}

	/**
	 * Asks the person with how many players he wants to start a game.
	 * 
	 * @return the amount of players he wants to have in his game: 2, 3 or 4
	 */
	public int startGame() {
		String prompt = Protocol.BORDER + "With how many players do you want to start a game?"
						+ "\nYou can choose: 2, 3 or 4.";
		int aantal = 0;
		while (true) {
			aantal = readInt(prompt);
			if (aantal == 2 || aantal == 3 || aantal == 4) {
				return aantal;
			} else {
				System.out.println("invalid number.");
			}
		}
	}

	/**
	 * Asks the person who sets up the client how the client should be named.
	 * 
	 * @return the name the person types.
	 */
	public String getClientName() {
		String prompt = "Specify your name:";
		return readString(prompt);
	}

	/**
	 * Prints the prompt and waits for the user to give input, then decides if
	 * it is a swap or a place and calls those methods respectively.
	 */
	public void determineMove(HumanPlayer player) {
		Board b = player.getGame().getBoard().deepCopy();
		List<Stone> stones = player.getStones();
		List<PossibleMove> possibleMoves = new ArrayList<>(b.getPossibleMoves().values());
		List<Stone> stonesplaced  = new ArrayList<>();
		possibleMoves = player.adaptPossibleMoves(possibleMoves, stones, stonesplaced);
		while (!possibleMoves.isEmpty() || player.canTrade(stonesplaced.size())) {
			String message = b.toString(possibleMoves) + "\n"
					+ "Stones: \n" + player.stonesToString(stones) + "\n"
					+ "Choose a place to play ";
			print(message);
			if (player.canTrade(stonesplaced.size())) {
				print("-1: swap");
				int choice = getChoice(-1, possibleMoves.size());
				if (choice == -1) {
					swapStones();
					break;
				} else {
					stonesplaced.add(placeStone(b, possibleMoves.get(choice), player));
				}
			} else {
				int choice = getChoice(0, possibleMoves.size());
				stonesplaced.add(placeStone(b, possibleMoves.get(choice), player));
			}
			stones = player.getStones();
			possibleMoves = new ArrayList<>(b.getPossibleMoves().values());
			possibleMoves = player.adaptPossibleMoves(possibleMoves, stones, stonesplaced);
		}
		if (!stonesplaced.isEmpty()) {
			player.removeStones(stonesplaced);
			client.place(stonesplaced);
		}
	}



	public String possibleMovesToString(List<PossibleMove> pmlist) {
		String result = "Possible Places to play: ";
		for (int i = 0; i < pmlist.size(); i++) {
			result += i + " " + pmlist.get(i) + "\n";
		}
		return result;
	}

	/**
	 * returns int in range between low (inclusive) and high (exclusive).
	 *
	 * @return A valid integer
	 */
	private int getChoice(int low, int high) {
		boolean valid = false;
		int choice;
		do {
			print("-2: hint");
			choice = readInt("Enter you choice (" + low + " - " + (high -1) + "): ");
			if (choice == -2) {
				int hintchoice = (int) Math.floor(Math.random() * (high -low) + low);
				print("I suggest you take: " + hintchoice);
				choice = readInt("Enter you choice: ");
			}
			valid = choice >= low && choice < high;
			if (!valid) System.out.println("ERROR: number " + choice + " is not a valid choice.");
		} while (!valid);
		return choice;
	}

	/**
	 * Checks if the given input is in the range of -1 till the amount of stones
	 * the player has.
	 * 
	 * @param prompt
	 * @return A valid integer
	 */
	private int intOutPromptMinus1TillStonesRange(String prompt) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidIntStonesRange(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidIntStonesRange(choice);
		}
		return choice;
	}

	/**
	 * Checks if the given input is in the range of 0 till the amount of stones
	 * the player has.
	 * 
	 * @param prompt
	 * @return A valid integer
	 */
	private int intOutPromptFrom0ToStonesRange(String prompt) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidIntStonesRangeFrom0(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidIntStonesRangeFrom0(choice);
		}
		return choice;
	}

	/**
	 * Asks the humanplayer how many stones he wants to swap, the humanplayer
	 * has to repeatedly give input and if he gives -1 the turn ends and the
	 * stones selected will be send to the server to be swapped. The first input
	 * cant be -1 since otherwise it was possible to swap no stones ;)
	 */
	private void swapStones() {
		List<Stone> stones = new ArrayList<Stone>();
		List<Stone> playerStones = client.getGame().getCurrentPlayer().getStones();
		int sizeMin1 = playerStones.size()-1;
		String swapPrompt = Protocol.BORDER + "These are your stones, "
						+ "which stone do you want to swap?\n"
						+ client.getGame().getCurrentPlayer().thisStonesToString()
						+ "\nChoose 1 stone now and then you will get the chance "
						+ "to pick more stones or end the swap.";
		print(swapPrompt);
		int choice = getChoice(0, playerStones.size());
		Stone chosen1 = playerStones.get(choice);
		client.getGame().getCurrentPlayer().removeStone(chosen1);
		stones.add(chosen1);
		for (int i = 1; i <= sizeMin1 && i<= client.getGame().getBag(); i++) {
			String swapPromptSecond = Protocol.BORDER + "These are your stones, "
							+ "which stone do you want to swap?\n"
							+ client.getGame().getCurrentPlayer().thisStonesToString()
							+ "\nOr choose:\n-1 : to end the swap and your turn.";
			int choiceSecond = intOutPromptMinus1TillStonesRange(swapPromptSecond);
			if (choiceSecond == -1) {
				break;
			} else {
				Stone chosen2 = playerStones.get(choice);
				client.getGame().getCurrentPlayer().removeStone(chosen2);
				stones.add(chosen2);
			}
		}
		client.trade(stones);
	}

	/**
	 * Asks the humanplayer how many stones he wants to place, the humanplayer
	 * has to repeatedly give input and if he gives -1 the turn ends and the
	 * stones selected will be send to the server to be placed. The int
	 * firstChoice is the first stone to be placed. The first input cant be -1
	 * since otherwise it was possible to place no stones ;)
	 *
	 */
	private Stone placeStone(Board b , PossibleMove place, Player p) {
		List<Stone> acceptableStones = p.adaptStones(p.getStones(), place);
		String message = "You can place these stones here: " + p.stonesToString(acceptableStones) + "\n"
				+ "which stone do you want to place?: ";
		print(message);
		int choice = getChoice(0,acceptableStones.size());
		Stone s = acceptableStones.get(choice);
		b.makeMove(s, place);
		return s;
	}

	/**
	 * Checks if the given input is in the range of 0 till the amount of
	 * possiblemoves.
	 * 
	 * @param prompt
	 * @return A valid integer
	 */
	private int intOutPromptPossibleMovesRange(String prompt) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidIntNotMinusOne(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidIntNotMinusOne(choice);
		}
		return choice;
	}

	/**
	 * Asks the humanplayer for input of a number by showing the prompt.
	 * 
	 * @param prompt
	 * @return the number the player typed.
	 */
	public int readInt(String prompt) {
		int value = 0;
		boolean intRead = false;
		// @SuppressWarnings("resource")
		Scanner line = new Scanner(System.in);
		do {
			System.out.print(prompt);
			try (Scanner scannerLine = new Scanner(line.nextLine());) {
				if (scannerLine.hasNextInt()) {
					intRead = true;
					value = scannerLine.nextInt();
				}
			}
		} while (!intRead);
		return value;
	}

	/**
	 * Shows the specified prompt to the user and receives input back from the
	 * user.
	 * 
	 * @param prompt
	 * @return A string the user types.
	 */
	public String readString(String prompt) {
		String input = "";
		boolean stringRead = false;
		// @SuppressWarnings("resource")
		Scanner line = new Scanner(System.in);
		do {
			System.out.print(prompt);
			try (Scanner scannerLine = new Scanner(line.nextLine());) {
				if (scannerLine.hasNext()) {
					stringRead = true;
					input = scannerLine.next();
				}
			}
		} while (!stringRead);
		return input;

	}

	/**
	 * Prints the message to the output.
	 * 
	 * @param message
	 */
	public void print(String message) {
		System.out.println(message);
	}

	@Override
	public void update(Observable observable, Object o) {
		if (observable instanceof HumanPlayer) {
			determineMove((HumanPlayer) observable);
		} else if (observable instanceof ClientGame) {
			print(((ClientGame) observable).getBoard().toString());
		}
	}
}
