package server;

import java.util.List;
import java.util.Map;

public class ComputerPlayer extends Player {

	private Strategy strategy;

	public ComputerPlayer(String name, Game game, Strategy strategy) {
		super(name, game);
		this.strategy = strategy;

	}

	@Override
	public void makeMove() {
		int choice = strategy.determineMove(getGame(), getStones());
		if (choice < 0) {
			//stenen ruilen ?? beter morgen even overleggen hoe dit designen, misschien met een list van possiblemoves die gedaan worden en als ruilen dan de stenen die hij wil ruilene ofzo
		}
		Map<Position, PossibleMove> possibleMoves = getGame().getPossibleMoves();
		PossibleMove place = possibleMoves.get(choice);
		List<Stone> stones = getStones();
		for (Stone s : stones) {
			if (place.acceptable(s)) {
				getGame().getBoard().makeMove(s, place);
				break;
			}
		}
	}
}
