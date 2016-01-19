package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import shared.Stone;

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

	public boolean askHumanOrComputerPlayer() {
		while (true) {
			int bool = readInt(
					"Do you want to play as human or shall a computerplayer play?\n0 : HumanPlayer.\n1 : ComputerPlayer.");
			if (bool != 1 && bool != 0) {
				System.out.println("invalid choice");
			} else if (bool == 0) {
				return false;
			} else
				return true;
		}
	}

	public Strategy getStrategyFromInput() {
		String prompt = "Which strategy shall the ComputerPlayer have?\n0 : BadStrategy.\n\nMore options follow.";
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

	public int startGame() {
		String prompt = "With how many players do you want to start a game?\nYou can choose: 2, 3 or 4.";
		int aantal = 0;
		while (true) {
			aantal = readInt(prompt);
			if (aantal == 2 || aantal == 3 || aantal == 4) {
				client.join(aantal);
				return aantal;
			} else
				System.out.println("invalid number.");
		}
	}

	public String getClientName() {
		String prompt = "Specify your name:";
		return readString(prompt);
	}

	/**
	 * Prints the prompt and waits for the user to give input, then decides if
	 * it is a swap or a place and calls those methods respectively.
	 */
	public void determineMove() {
		String prompt = client.getGame().getPossibleMoves().toString()
				+ "\n-1 : Swap stones\n-> What is your choice?\n\nThese are your stones:\n"
				+ client.getGame().getCurrentPlayer().stonesToString();
		int choice = intOutPromptMinus1TillPossibleMovesRange(prompt);
		if (choice == -1) {
			swapStones();
		} else
			placeStones(choice);
	}

	/**
	 * Checks if the given input is in the range of -1 till the amount of
	 * possiblemoves.
	 * 
	 * @param prompt
	 * @return A valid integer
	 */
	private int intOutPromptMinus1TillPossibleMovesRange(String prompt) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidInt(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidInt(choice);
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
			valid = client.getGame().isValidInt(choice);
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
			valid = client.getGame().isValidInt(choice);
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
		String swapPrompt = "These are your stones, which stone do you want to swap?\n"
				+ client.getGame().getCurrentPlayer().stonesToString()
				+ "\nChoose 1 stone now and then you will get the chance to pick more stones or end the swap.";
		int choice = intOutPromptFrom0ToStonesRange(swapPrompt);
		Stone chosen1 = client.getGame().getCurrentPlayer().getStones().get(choice);
		client.getGame().getCurrentPlayer().removeStone(chosen1);
		stones.add(chosen1);
		for (int i = 1; i < 7; i++) {
			String swapPromptSecond = "These are your stones, which stone do you want to swap?\n"
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
	 * @param firstChoice
	 */
	private void placeStones(int firstChoice) {

		// String prompt =
	}

	/**
	 * Asks the humanplayer for input of a number by showing the prompt.
	 * 
	 * @param prompt
	 * @return
	 */
	public int readInt(String prompt) {
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

	public void print(String message) {
		System.out.println(message);
	}
}
