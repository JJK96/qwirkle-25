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

	public String getHint(Board board) {
		return new LittleBetterStrategy(1).getHint(this, board, getStones());
	}

	/**
	 * ????
	 */
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void makeMove() {
		setChanged();
		notifyObservers();
	}

}
