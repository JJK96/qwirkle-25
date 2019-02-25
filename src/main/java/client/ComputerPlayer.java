package client;

public class ComputerPlayer extends Player {

	private Strategy strategy;

	/**
	 * Creates a computerplayer with specified name and strategy.
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
	 * Lets the computerPlayer make a move.
	 */
	@Override
	public void makeMove() {
		strategy.determineMove(getGame(), getStones());
	}
}
