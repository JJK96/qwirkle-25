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
		Board b;
		if (!(game.getBoard().getPossibleMoves().size() > 1)) {
			b = game.getBoard();
		} else {
			b = game.getBoard().deepCopy();
		}
		List<Stone> stonesPlace = new ArrayList<Stone>();
		end: for (int k = 0; k < 7; k++) {
			Map<Position, PossibleMove> moves = b.getPossibleMoves();
			for (int i = 0; i < moves.size(); i++) {
				for (int j = 0; j < stones.size(); j++) {
					if (game.getBoard().isValidMove(moves.get(i), stones.get(j))) {
						Stone stone = stones.get(j);
						stone.setPosition(moves.get(i).getPosition());
						stonesPlace.add(stone);
						b.makeMove(moves.get(i).getPosition(), stone);
					} else
						break end;
				}
			}
		}
		if (stonesPlace.size() > 0) {
			game.getClient().place(stonesPlace);
		} else
			game.getClient().trade(stones);
	}

}
