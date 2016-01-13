package server;

import java.util.Scanner;

public class View {

	private Game game;

	/**
	 * Creates a view for the specified game
	 * 
	 * @param game
	 */
	public View(Game game) {
		this.game = game;
	}

	/**
	 * Shows the error occurred
	 */
	public void showError() {

	}

	/**
	 * Checks of the userinput is a valid number and returns that number if it
	 * is valid
	 * 
	 * @return number of the chosen possiblemove
	 */
	public int determineMove() {
		String prompt = game.getPossibleMoves().toString() + "\n-> What is your choice? ";
		int choice = readInt(prompt);
		boolean valid = game.isValidInt(choice);
		while (!valid) {
			System.out.println("ERROR: number " + choice + " is no valid choice.");
			choice = readInt(prompt);
			valid = game.isValidInt(choice);
		}
		return choice;
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
}
