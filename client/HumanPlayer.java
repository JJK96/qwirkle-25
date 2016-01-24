package client;

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
	 * ????
	 */
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void makeMove() {
		setChanged();
		notifyObservers();
	}
}
