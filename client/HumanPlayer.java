package client;

import java.util.List;
import shared.*;
import java.util.Map;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, ClientGame game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		int i = getGame().getView().determineMove();
		Map<Position, PossibleMove> possibleMoves = getGame().getBoard().getPossibleMoves();
		List<Stone> stones = getStones();
		PossibleMove place = possibleMoves.get(i);
		for (Stone s : stones) {
			if (place.acceptable(s)) {
				getGame().getBoard().makeMove(s, place);
				break;
			}
		}
		
	}
}
