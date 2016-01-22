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
		Board b = game.getBoard().deepCopy();
		System.out.println(b.toString());
		List<Stone> stonesPlace = new ArrayList<Stone>();
		Map<Position, PossibleMove> moves = b.getPossibleMoves();
		for (int i = 0; i < moves.size(); i++) {
			for (int j = 0; j < stones.size(); j++) {
				if (b.isValidMove(moves.get(i), stones.get(j))) {
					Stone stone = stones.get(j);
					b.makeMove(stone, moves.get(i));
					stonesPlace.add(stone);
					game.getClient().place(stonesPlace);
					List<Stone> toRemove = new ArrayList<>();
					toRemove.addAll(stonesPlace);
					game.getCurrentPlayer().removeStones(toRemove);
					return;
				}
			}
		}
		game.getClient().trade(stones);
		List<Stone> toRemove = new ArrayList<>();
		toRemove.addAll(stones);
		game.getCurrentPlayer().removeStones(toRemove);
	}
}
