package client;

public class ComputerPlayer extends Player {

	private Strategy strategy;

	public ComputerPlayer(String name, ClientGame game, Strategy strategy) {
		super(name, game);
		this.strategy = strategy;

	}

	@Override
	public void makeMove() {
		strategy.determineMove(getGame(), getStones());
	}
}
