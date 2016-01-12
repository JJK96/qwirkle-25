package server;

import java.util.Map;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, Game game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		int i = getGame().getView().determineMove();
		Map<Position, PossibleMove> possibleMoves = getGame().getBoard().getPossibleMoves();
		PossibleMove place = possibleMoves.get(i);
	//	getGame().getBoard().makeMove(, place);
	}
}
