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
		String pointsPlayers = "-------------------------------------------------------"
						+ "\nPLAYERPOINTS:\n\n";
		for (Player p : game.getPlayers()) {
			pointsPlayers += p.getName() + ": " + p.getPoints() + "\n";
		}
		pointsPlayers += "-------------------------------------------------------";
		game.getClient().getView().print(pointsPlayers);
		game.getClient().getView().print(b.toString() 
						+ "\n-------------------------------------------------------");
		int size = stones.size();
		List<PossibleMove> adaptedPossibleMoves = new ArrayList<PossibleMove>();
		List<Stone> stonesPlaced = new ArrayList<Stone>();
		for (int i = 0; i < size; i++) {
			List<PossibleMove> allPossibleMoves = 
							new ArrayList<PossibleMove>(b.getPossibleMoves().values());
			adaptedPossibleMoves = game.getCurrentPlayer()
					.adaptPossibleMoves(allPossibleMoves, stones, stonesPlaced);
			end: for (PossibleMove p : adaptedPossibleMoves) {
				for (Stone s : stones) {
					if (p.acceptable(s)) {
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
			if (game.getBag() < size) {
				for (int j = 0; j < game.getBag(); j++) {
					stonesPlaced.add(stones.get(j));
				}
				System.out.println("adapted possible moves: " + adaptedPossibleMoves);
				System.out.println("stones: " + stones);
				game.getClient().trade(stonesPlaced);
				List<Stone> toRemove = new ArrayList<>();
				toRemove.addAll(stonesPlaced);
				game.getCurrentPlayer().removeStones(toRemove);
			} else {
				game.getClient().trade(stones);
				List<Stone> toRemove = new ArrayList<>();
				toRemove.addAll(stones);
				game.getCurrentPlayer().removeStones(toRemove);
			}
		}
	}
}
