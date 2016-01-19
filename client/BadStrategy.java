package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import shared.*;

public class BadStrategy implements Strategy {

	public BadStrategy() {

	}

	@Override
	public void determineMove(ClientGame game, List<Stone> stones) {
		Map<Position, PossibleMove> moves = game.getBoard().getPossibleMoves();
		Set<Position> pos = moves.keySet();
		List<PossibleMove> pm = new ArrayList<PossibleMove>();
		for (Position p : pos) {
			pm.add(moves.get(p));
		}
		List<Stone> stoness = new ArrayList<Stone>();
		for (int i = 0; i < moves.size(); i++) {
			for (int j = 0; j < stones.size(); j++) {
				if (game.getBoard().isValidMove(pm.get(i), stones.get(j))) {
					Stone stone = stones.get(j);
					stone.setPosition(pm.get(i).getPosition());
					stoness.add(stone);
					game.getClient().place(stoness);
					return;
				}
			}
		}
		game.getClient().trade(stones);
	}

}
