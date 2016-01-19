package client;

import java.util.List;
import java.util.Map;

import shared.*;

public class BadStrategy implements Strategy {

	public BadStrategy() {

	}

	@Override
	public void determineMove(ClientGame game, List<Stone> stones) {
		Map<Position, PossibleMove> moves = game.getBoard().getPossibleMoves();
		List<Stone> stoness = game.getCurrentPlayer().getStones();
		for (int i = 0; i < moves.size(); i++) {
			for (int j = 0; j < stoness.size(); j++) {
				if (game.getBoard().isValidMove(moves.get(i), stoness.get(j))) {
					Stone stone = stoness.get(j);
					stoness.removeAll(stoness);
					stoness.add(stone);
					game.getClient().place(stoness);
					return;
				}
			}
		}
		game.getClient().trade(stoness);
	}

}
