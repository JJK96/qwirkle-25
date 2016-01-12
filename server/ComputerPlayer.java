package server;

import java.util.List;
import java.util.Map;

public class ComputerPlayer extends Player {

	public ComputerPlayer(String name, Game game) {
		super(name, game);

	}

	@Override
	public void makeMove() {
		Map<Position, PossibleMove> possibleMoves = getGame().getPossibleMoves();
		List<Stone> stones = getStones();
		PossibleMove place = possibleMoves.get((int) Math.random() * (possibleMoves.size() + 1));
		for (Stone s : stones) {
			if (place.acceptable(s)) {
				getGame().getBoard().makeMove(s, place);
				break;
			}
		}
	}
}
