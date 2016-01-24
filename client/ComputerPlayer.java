package client;

public class ComputerPlayer extends Player {

	private Strategy strategy;

	/**
	 * Creates a computerplayer.
	 * 
	 * @param name
	 * @param game
	 * @param strategy
	 */
	public ComputerPlayer(String name, ClientGame game, Strategy strategy) {
		super(name, game);
		this.strategy = strategy;

	}

	/**
	 * Makes a move for the computerplayer via the strategy.
	 */
	@Override
	public void makeMove() {
		strategy.determineMove(getGame(), getStones());
	}
}
