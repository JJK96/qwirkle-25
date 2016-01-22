package client;

import java.util.ArrayList;
import java.util.List;

import shared.*;

public class BadStrategy implements Strategy {

	public BadStrategy() {

	}

	@Override
	public void determineMove(ClientGame game, List<Stone> stones) {
		Board b = game.getBoard().deepCopy();
		game.getClient().getView().print(b.toString());
		int size = stones.size();
		List<PossibleMove> adaptedPossibleMoves = new ArrayList<PossibleMove>();
		List<Stone> stonesPlaced = new ArrayList<Stone>();
		for (int i = 0; i < size; i++) {
			List<PossibleMove> allPossibleMoves = new ArrayList<PossibleMove>(b.getPossibleMoves().values());
			adaptedPossibleMoves = game.getCurrentPlayer().adaptPossibleMoves(allPossibleMoves, stones, stonesPlaced);
			end: for (PossibleMove p : adaptedPossibleMoves) {
				for (Stone s : stones) {
					if (b.isValidMove(p, s)) {
						b.makeMove(s, p);
						stonesPlaced.add(s);
						game.getCurrentPlayer().removeStone(s);
						break end;
					}
				}
			}
		}
		if (stonesPlaced.size() > 0) {
			game.getClient().place(stonesPlaced);
		} else {
			game.getClient().trade(stones);
			List<Stone> toRemove = new ArrayList<>();
			toRemove.addAll(stones);
			game.getCurrentPlayer().removeStones(toRemove);
		}
	}
}
