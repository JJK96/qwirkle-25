package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import shared.*;

public class View {

	private Client client;

	/**
	 * Creates a view for the specified client
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
		String prompt = Protocol.BORDER + "Do you want to play as human or shall a computerplayer play?"
				+ "\n0 : HumanPlayer.\n1 : ComputerPlayer.";
		while (true) {
			int bool = readInt(prompt);

			if (bool != 1 && bool != 0) {
				System.out.println("invalid choice");
			} else if (bool == 0) {
				return false;
			} else
				return true;
		}
	}

	/**
	 * Asks the person which strategy the computerplayer shall have.
	 * 
	 * @return the strategy chosen.
	 */
	public Strategy getStrategyFromInput() {
		String prompt = Protocol.BORDER
				+ "Which strategy shall the ComputerPlayer have?\n0 : BadStrategy.\n\nMore options will follow.";
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
		String prompt = Protocol.BORDER
				+ "With how many players do you want to start a game?\nYou can choose: 2, 3 or 4.";
		int aantal = 0;
		while (true) {
			aantal = readInt(prompt);
			if (aantal == 2 || aantal == 3 || aantal == 4) {
				return aantal;
			} else
				System.out.println("invalid number.");
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
	public void determineMove() {
		String prompt = Protocol.BORDER + client.getGame().getBoard();
				prompt += "\nIf you choose one of these places you will go to the place view, where you can place stones"
				+ "\nThe number doesn't matter now you can choose your first stone later"
				+ "\n-1 : Swap stones\n-> What is your choice?\n\nThese are your stones:\n"
				+ client.getGame().getCurrentPlayer().stonesToString();
		int choice = intOutPromptMinus1TillPossibleMovesRange(prompt, client.getGame().getBoard());
		if (choice == -1) {
			swapStones();
		} else
			placeStones();
	}

	/**
	 * Checks if the given input is in the range of -1 till the amount of
	 * possiblemoves.
	 * 
	 * @param prompt
	 * @return A valid integer
	 */
	private int intOutPromptMinus1TillPossibleMovesRange(String prompt, Board b) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidInt(choice, b);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidInt(choice, b);
		}
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
		String swapPrompt = Protocol.BORDER + "These are your stones, which stone do you want to swap?\n"
				+ client.getGame().getCurrentPlayer().stonesToString()
				+ "\nChoose 1 stone now and then you will get the chance to pick more stones or end the swap.";
		int choice = intOutPromptFrom0ToStonesRange(swapPrompt);
		Stone chosen1 = client.getGame().getCurrentPlayer().getStones().get(choice);
		client.getGame().getCurrentPlayer().removeStone(chosen1);
		stones.add(chosen1);
		for (int i = 1; i < 7; i++) {
			String swapPromptSecond = Protocol.BORDER + "These are your stones, which stone do you want to swap?\n"
					+ client.getGame().getCurrentPlayer().stonesToString()
					+ "\nOr choose:\n-1 : to end the swap and your turn.";
			int choiceSecond = intOutPromptMinus1TillStonesRange(swapPromptSecond);
			if (choiceSecond == -1) {
				client.trade(stones);
				return;
			} else {
				Stone chosen2 = client.getGame().getCurrentPlayer().getStones().get(choice);
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
	private void placeStones() {
		Board b = client.getGame().getBoard().deepCopy();
		List<Stone> stones = new ArrayList<Stone>();
		Stone lastStone = null;
		int choice;
		for (int i = 0; i < 7; i++) {
			String prompt = Protocol.BORDER + client.getGame().getBoard()
					+ "\nThis is the board with all you can choose." + "\nThese are your stones:\n"
					+ client.getGame().getCurrentPlayer().stonesToString() + "\nCHOOSE CAREFULL:\n"
					+ "If you choose a possiblemove and you can't place 1 of your stones there you "
					+ "have to start over with placing stones!!";
			if (stones.size() > 0) {
				prompt += "\nIf you want to end your turn choose -1.";
				choice = intOutPromptMinus1TillPossibleMovesRange(prompt, b);
			} else {
				choice = intOutPromptPossibleMovesRange(prompt);
			}
			if (choice != -1) {
				Stone stone = client.getGame().getCurrentPlayer().possibleMoveToStone(choice, b, lastStone);
				if (stone == null) {
					client.getGame().getCurrentPlayer().setStonesFromBackup();
					placeStones();
					return;
				}
				Position pos = client.getGame().getCurrentPlayer().getPosition();
				b.makeMove(pos, stone);
				lastStone = b.getStones().get(pos);
				stones.add(stone);
			} else {
				client.getGame().getCurrentPlayer().removeBackup();
				client.place(stones);
			}
		}
		client.getGame().getCurrentPlayer().removeBackup();
		client.place(stones);
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
		//@SuppressWarnings("resource")
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
		//@SuppressWarnings("resource")
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
}
