package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shared.*;

public class BadStrategy implements Strategy {

	public BadStrategy() {

	}

	@Override
	public void determineMove(ClientGame game, List<Stone> stones) {
		Board b = game.getBoard();
		List<Stone> stonesPlace = new ArrayList<Stone>();
		Map<Position, PossibleMove> moves = b.getPossibleMoves();
		for (int i = 0; i < moves.size(); i++) {
			for (int j = 0; j < stones.size(); j++) {
				if (game.getBoard().isValidMove(moves.get(i), stones.get(j))) {
					Stone stone = stones.get(j);
					stone.setPosition(moves.get(i).getPosition());
					stonesPlace.add(stone);
					game.getClient().place(stonesPlace);
				} else
					break;
			}

		}

		game.getClient().trade(stones);
	}

}
