package client;

import java.util.ArrayList;
import java.util.List;
import shared.*;
import java.util.Map;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, ClientGame game) {
		super(name, game);

	}

	@Override
	public List<Stone> makeMove() {
		boolean sendStones = false;
		List<Stone> moveStones = new ArrayList<Stone>();
		for (int k = 0; k < 7 && !sendStones; k++) {
			int i = getGame().getClient().getView().determineMove();
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
		return moveStones;
	}
}
