package server;

import java.util.Scanner;

public class View {

	private Game game;
	
	public View(Game game) {
		this.game = game;
	}

	public void showError() {

	}

	public int determineMove() {
	    String prompt = "-> " + game.getPossibleMoves().toString()+ ", what is your choice? ";
        int choice = readInt(prompt);
        boolean valid = game.isValidInt(choice);
        while (!valid) {
            System.out.println("ERROR: number " + choice
                    + " is no valid choice.");
            choice = readInt(prompt);
            valid = game.isValidInt(choice);
        }
        return choice;
	}

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
