package server;

import java.util.Map;

public class ComputerPlayer extends Player {

	public ComputerPlayer(String name, Game game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		Map<Position, PossibleMove> possibleMoves = getGame().getPossibleMoves();
		
	}
}
