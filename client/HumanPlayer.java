package client;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, ClientGame game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		System.out.println("in humanplayer makemove");
		setChanged();
		notifyObservers();
	}
}
