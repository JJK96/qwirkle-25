package client;

import shared.Board;

public class HumanPlayer extends Player {
	/**
	 * Creates a humanplayer.
	 * 
	 * @param name
	 * @param game
	 */
	public HumanPlayer(String name, ClientGame game) {
		super(name, game);
	}
	
	/**
	 * If the humanplayer wants a hint this method is called and returns
	 * the first stone from the move the LittleBetterStrategy would make
	 * on the specified board with the specified stones.
	 * 
	 * @param board
	 * @return
	 */
	public String getHint(Board board) {
		return new LittleBetterStrategy(1).getHint(board, getStones());
	}

	/**
	 * Notifies the observers that its the turn of the humanplayer to make a move.
	 */
	public void makeMove() {
		setChanged();
		notifyObservers();
	}

}
