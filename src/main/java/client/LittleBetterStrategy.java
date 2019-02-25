package client;

import shared.Board;
import shared.Position;
import shared.PossibleMove;
import shared.Stone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjk on 1/25/16.
 *
 */
public class LittleBetterStrategy implements Strategy {

	private int time;

	/**
	 * Constructor.
	 * Sets the time the strategy has to make a move.
	 * @param time
     */
	public LittleBetterStrategy(int time) {
		this.time = time;
	}

	/**
	 * Makes the move that is returned by getMove().
	 * If the stonelist returned by getMove is empty as much stones as possible are traded.
	 * @param game
	 * @param stones
     */
	@Override
	public void determineMove(ClientGame game, List<Stone> stones) {
		List<Stone> stonesplaced = getMove(game.getBoard(), stones);
		if (stonesplaced.isEmpty()) {
			List<Stone> toTrade;
			if (game.getBag() < stones.size()) {
				toTrade = new ArrayList<Stone>();
				for (int i = 0; i < game.getBag(); i++) {
					toTrade.add(stones.get(i));
				}
			} else {
				toTrade = stones;
			}
			game.getClient().trade(toTrade);
			List<Stone> toRemove = new ArrayList<Stone>();
			toRemove.addAll(toTrade);
			game.getCurrentPlayer().removeStones(toRemove);
		} else {
			game.getClient().place(stonesplaced);
			List<Stone> toRemove = new ArrayList<Stone>();
			toRemove.addAll(stonesplaced);
			game.getCurrentPlayer().removeStones(toRemove);
		}
	}

	/**
	 * Gets the move the strategy wants to make.
	 *
	 * It does so by creating a deepcopy of the board and trying a 
	 * lot of random moves until the time is over
	 * then it chooses the best one and returns that.
	 *
	 * @param board
	 * @param stones
     * @return a list of stones that are to be placed on the board.
	 * 			or an empty list if the strategy can not place any 
	 * 			stones (indicating that it wants to swap).
     */
	public List<Stone> getMove(Board board, List<Stone> stones) {
		List<Stone> move = new ArrayList<Stone>();
		int movePoints = 0;
		long start = System.currentTimeMillis();
		long end = start + time * 100;
		while (System.currentTimeMillis() < end) {
			List<Stone> stonesBackup = new ArrayList<Stone>();
			for (Stone s : stones) {
				Stone newStone = new Stone(s.getShape(), s.getColor());
				stonesBackup.add(newStone);
			}
			Board b = board.deepCopy();
			List<PossibleMove> possibleMoves = 
							new ArrayList<PossibleMove>(b.getPossibleMoves().values());
			List<Stone> stonesplaced = new ArrayList<Stone>();
			possibleMoves = Player.adaptPossibleMoves(possibleMoves, stonesBackup, stonesplaced, b);
			while (!possibleMoves.isEmpty()) {
				int choice = (int) Math.floor(Math.random() * possibleMoves.size());
				Stone placed = placeStone(b, possibleMoves.get(choice), stonesBackup);
				stonesplaced.add(placed);
				stonesBackup.remove(placed);
				possibleMoves = new ArrayList<PossibleMove>(b.getPossibleMoves().values());
				possibleMoves = Player.adaptPossibleMoves(
						possibleMoves, stonesBackup, stonesplaced, b);
			}
			List<Position> positions = new ArrayList<Position>();
			for (int i = 0; i < stonesplaced.size(); i++) {
				positions.add(stonesplaced.get(i).getPosition());
			}
			if (!stonesplaced.isEmpty()) {
				int points = b.calculatePoints(stonesplaced, positions);
				if (points > movePoints) {
					move = stonesplaced;
					movePoints = points;
				}
			} else {
				return stonesplaced;
			}
		}
		return move;
	}

	/**
	 * Chooses a stone to play on the specified place.
	 * It places this stone on the given board.
	 * and returns it.
	 *
	 * @param b
	 * @param place
	 * @param stones
     * @return
     */
	private Stone placeStone(Board b, PossibleMove place, List<Stone> stones) {
		List<Stone> acceptableStones = Player.adaptStones(stones, place);
		int choice = (int) Math.floor(Math.random() * acceptableStones.size());
		Stone s = acceptableStones.get(choice);
		b.makeMove(s, place);
		return s;
	}

	/**
	 * Returns a string with the stone this strategy would 
	 * place on the given board with the given stones.
	 * @param board
	 * @param stones
     * @return
     */
	public String getHint(Board board, List<Stone> stones) {
		String res = "I suggest you ";
		List<Stone> stonesplaced = getMove(board, stones);
		if (stonesplaced.isEmpty()) {
			res += "trade";
		} else {
			Stone s = stonesplaced.get(0);
			res += "place " + s + " at " + s.getPosition();
		}
		return res;
	}
}
