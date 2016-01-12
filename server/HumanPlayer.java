package server;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, Game game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		int i = getGame().getView().determineMove();
	}

}
