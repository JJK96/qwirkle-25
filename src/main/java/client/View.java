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
	 * @param client
	 */
	public View(Client client) {
		this.client = client;
	}

	/**
	 * Get the hostname of the server from the user.
	 * 
	 * @return hostname of server
	 * @throws UnknownHostException
	 */
	public InetAddress getHostName() throws UnknownHostException {
		String hostname = readString("Enter hostname of the server:");
		InetAddress result = InetAddress.getByName(hostname);
		return result;
	}

	/**
	 * Get the port of the server from the user.
	 * 
	 * @return the port typed by the user.
	 */
	public int getPort() {
		int port = readInt("Enter port number:");
		return port;
	}

	/**
	 * Asks the user if he wants to play himself or
	 * want a computerplayer to play.
	 * 
	 * @return 	true if he wants a computerplayer,
	 * 			false if he wants to play as humanplayer himself.
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
	public Strategy getStrategyFromInput(int time) {
		String prompt = Protocol.BORDER + "Which strategy shall the ComputerPlayer have?"
						+ "\n0 : BadStrategy.\n1 : LittleBetterStrategy"
						+ "\n\nMore options will follow.";
		int strat = 0;
		while (true) {
			strat = readInt(prompt);
			if (strat != 0 && strat != 1) {
				System.out.println("invalid choice");
			} else if (strat == 0) {
				return new BadStrategy(time);
			} else {
				return new LittleBetterStrategy(time);
			}
		}
	}

	/**
	 * Asks the user how much time the AI should have to make his move.
	 * @return
     */
	public int getPlayTimeFromInput() {
		String prompt = Protocol.BORDER + "How much time shall the computerplayer have to"
						+ " decide his move?\n1 = a tenth of a second, so 10 is 1 second."
						+ "\nLess than a tenth of a second is not possible.";
		int time = 0;
		while (true) {
			time = readInt(prompt);
			if (!(time >= 1)) {
				System.out.println("invalid choice");
			} else {
				return time;
			} 
		}
	}

	/**
	 * Asks the person with how many players he wants to start a game.
	 *
	 * (You can also play alone but this is not supported by all 
	 * servers and is intended as an easter egg)
	 * 
	 * @return the amount of players he wants to have in his game: 2, 3 or 4
	 */
	public int startGame() {
		String prompt = Protocol.BORDER + "With how many players do you want to start a game?"
						+ "\nYou can choose: 2, 3 or 4.";
		int aantal = 0;
		while (true) {
			aantal = readInt(prompt);
			if (aantal >= 1 && aantal <= 4) {
				return aantal;
			} else {
				System.out.println("invalid number.");
			}
		}
	}

	/**
	 * Asks the user how the client should be named.
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
	 *
	 * It keeps asking the user for input as long as the user 
	 * can make a move and has not ended his turn.
	 * 
	 * @param player
	 */
	public void determineMove(HumanPlayer player) {
		Board b = player.getGame().getBoard().deepCopy();
		List<Stone> stones = player.getStones();
		List<Stone> stonesplaced  = new ArrayList<Stone>();
		boolean moved = false;
		if (player.getGame().getMoveCount() == 1) {
			new LittleBetterStrategy(1).determineMove(player.getGame(), stones);
			return;
		}
		List<PossibleMove> possibleMoves = 
						new ArrayList<PossibleMove>(b.getPossibleMoves().values());
		possibleMoves = Player.adaptPossibleMoves(possibleMoves, stones, stonesplaced, b);
		while (!moved && !possibleMoves.isEmpty() || player.canTrade(stonesplaced.size(), moved)) {
			String message = b.toString(possibleMoves) + "\n"
							+ "Stones: \n" + player.stonesToString(stones) + "\n"
							+ "Choose a place to play ";
			print(message);
			boolean valid = false;
			while (!valid) {
				if (player.canTrade(stonesplaced.size(), moved)) {
					print("-1: swap");
				}
				print("-2: hint");
				if (player.canEnd(stonesplaced.size())) {
					print("-3: end turn");
				}
				int choice = getChoice(-3, possibleMoves.size());
				if (choice == -1) {
					if (player.canTrade(stonesplaced.size(), moved)) {
						swapStones();
						valid = true;
						moved = true;
					}
				} else if (choice == -2) {
					print(player.getHint(b));
					continue;
				} else if (choice == -3) {
					if (player.canEnd(stonesplaced.size())) {
						moved = true;
						break;
					}
				} else {
					stonesplaced.add(placeStone(b, possibleMoves.get(choice), player));
					valid = true;
				}
				if (!valid) {
					System.out.println("ERROR: number " + choice + " is not a valid choice.");
				}
			}
			stones = player.getStones();
			possibleMoves = new ArrayList<PossibleMove>(b.getPossibleMoves().values());
			possibleMoves = Player.adaptPossibleMoves(possibleMoves, stones, stonesplaced, b);
		}
		System.out.println("Stonesplaced" + stonesplaced);
		if (!stonesplaced.isEmpty()) {
			client.place(stonesplaced);
		}
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
			choice = readInt("Enter your choice: ");
			valid = choice >= low && choice < high;
			if (!valid) {
				System.out.println("ERROR: number " + choice + " is not a valid choice.");
			}
		} while (!valid);
		return choice;
	}

	/**
	 * Checks if the given input is in the range of -1 till the amount of stones
	 * the player has.
	 *
	 * (This functionality can be taken over by the getChoice method but we didn't get to that yet.)
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
	 * Asks the user which stones he wants to swap, the user
	 * has to repeatedly give input and if he gives -1 the turn ends and the
	 * stones selected will be send to the server to be traded. The first input
	 * can not be -1.
	 */
	private void swapStones() {
		List<Stone> stones = new ArrayList<Stone>();
		List<Stone> playerStones = client.getGame().getCurrentPlayer().getStones();
		int sizeMin1 = playerStones.size() - 1;
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
		for (int i = 1; i <= sizeMin1 && i <= client.getGame().getBag(); i++) {
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
	 * Asks the user which stone he wants to place on the given position.
	 * The selected stone will then be placed and removed from the hand of the player.
	 *
	 * @return The stone to be placed.
	 */
	private Stone placeStone(Board b, PossibleMove place, Player p) {
		List<Stone> acceptableStones = Player.adaptStones(p.getStones(), place);
		String message = "You can place these stones here: \n" + p.stonesToString(acceptableStones)
						+ "\nwhich stone do you want to place?: ";
		print(message);
		int choice = getChoice(0, acceptableStones.size());
		Stone s = acceptableStones.get(choice);
		b.makeMove(s, place);
		p.removeStone(s);
		return s;
	}

	/**
	 * Asks the humanplayer for input of a number by showing the prompt.
	 * 
	 * @param prompt
	 * @return the number the player typed.
	 */
	private int readInt(String prompt) {
		int value = 0;
		boolean intRead = false;
		@SuppressWarnings("resource")
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
		@SuppressWarnings("resource")
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

	/**
	 * If its the turn of the humanplayer the TUI is updated and the humanplayer
	 * can start with giving input for his move because determinemove is called.
	 */
	@Override
	public void update(Observable observable, Object o) {
		if (observable instanceof HumanPlayer) {
			determineMove((HumanPlayer) observable);
		}
	}
}
