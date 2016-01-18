package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import shared.Stone;

public class View {

	private Client client;

	/**
	 * Creates a view for the specified game
	 * 
	 * @param game
	 */
	public View(Client client) {
		this.client = client;
	}

	/**
	 * Checks of the userinput is a valid number and returns that number if it
	 * is valid
	 * 
	 * @return number of the chosen possiblemove
	 */
	public void determineMove() {
		String prompt = client.getGame().getPossibleMoves().toString() + "\n-1 : Swap stones\n-> What is your choice? ";
		int choice = intOutPrompt(prompt);
		if (choice == -1) {
			swapStones();
		} else
			placeStones(choice);
	}

	private int intOutPrompt(String prompt) {
		int choice = readInt(prompt);
		boolean valid = client.getGame().isValidInt(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = client.getGame().isValidInt(choice);
		}
		return choice;
	}

	private void swapStones() {
		List<Stone> stones = new ArrayList<Stone>();
		String swapPrompt = "These are your stones, which stone do you want to swap?\n"
				+ client.getGame().getCurrentPlayer().getStones().toString()
				+ "\nChoose 1 stone now and then you will get the chance to pick more stones or end the swap.";
		int choice = intOutPrompt(swapPrompt);
		Stone chosen1 = client.getGame().getCurrentPlayer().getStones().get(choice);
		client.getGame().getCurrentPlayer().removeStone(chosen1);
		stones.add(chosen1);
		boolean send = false;
		for (int i = 1; i < 6 && !send; i++) {
			String swapPromptSecond = "These are your stones, which stone do you want to swap?\n"
					+ client.getGame().getCurrentPlayer().getStones().toString()
					+ "\nOr choose:\n-1 to end the swap and your turn.";
			int choiceSecond = intOutPrompt(swapPromptSecond);
			if (choiceSecond == -1) {
				client.trade(stones);
				return;
			}
		}
	}

	private void placeStones(int firstChoice) {

	}

	/**
	 * Asks the humanplayer for input of a number by showing the prompt.
	 * 
	 * @param prompt
	 * @return
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

	public void print(String message) {
		System.out.println(message);
	}
}
