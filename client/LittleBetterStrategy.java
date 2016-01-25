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
	
	public LittleBetterStrategy(int time) {
		this.time = time;
	}
	
    @Override
    public void determineMove(ClientGame game, List<Stone> stones) {
        List<Stone> stonesplaced = getMove(game, stones);
        if (stonesplaced.isEmpty()) {
            game.getClient().trade(stones);
            List<Stone> toRemove = new ArrayList<>();
            toRemove.addAll(stones);
            game.getCurrentPlayer().removeStones(toRemove);
        } else {
            game.getClient().place(stonesplaced);
            List<Stone> toRemove = new ArrayList<>();
            toRemove.addAll(stonesplaced);
            game.getCurrentPlayer().removeStones(toRemove);
        }
    }

    public List<Stone> getMove(ClientGame game, List<Stone> stones) {
        List<Stone> move = new ArrayList<>();
        int movePoints = 0;
        long start = System.currentTimeMillis();
        long end = start + time * 1000;
        while (System.currentTimeMillis() < end) {
            List<Stone> stonesBackup = new ArrayList<>();
            for (Stone s : stones) {
                Stone newStone = new Stone(s.getShape(), s.getColor());
                stonesBackup.add(newStone);
            }
            Player player = game.getCurrentPlayer();
            Board b = game.getBoard().deepCopy();
            List<PossibleMove> possibleMoves = new ArrayList<>(b.getPossibleMoves().values());
            List<Stone> stonesplaced = new ArrayList<>();
            possibleMoves = game.getCurrentPlayer().adaptPossibleMoves(
            		possibleMoves, stonesBackup, stonesplaced, b);
            while (!possibleMoves.isEmpty()) {
                int choice = (int) Math.floor(Math.random() * possibleMoves.size());
                Stone placed = placeStone(b, possibleMoves.get(choice), player, stonesBackup);
                stonesplaced.add(placed);
                stonesBackup.remove(placed);
                possibleMoves = new ArrayList<>(b.getPossibleMoves().values());
                possibleMoves = player.adaptPossibleMoves(
                		possibleMoves, stonesBackup, stonesplaced,b);
            }
            List<Position> positions = new ArrayList<>();
            for (int i=0; i<stonesplaced.size(); i++) {
                positions.add(stonesplaced.get(i).getPosition());
            }
            if (!stonesplaced.isEmpty()) {
                int points = b.calculatePoints(stonesplaced, positions);
                if (points > movePoints) {
                    move = stonesplaced;
                    movePoints = points;
                }
            } else return stonesplaced;
        }
        return move;
    }

    public Stone placeStone(Board b, PossibleMove place, Player p, List<Stone> stones) {
        List<Stone> acceptableStones = p.adaptStones(stones, place);
		int choice = (int) Math.floor(Math.random() * acceptableStones.size());
		Stone s = acceptableStones.get(choice);
		b.makeMove(s, place);
        return s;
    }

    public String getHint(ClientGame game, List<Stone> stones) {
        String res = "I suggest you ";
        List<Stone> stonesplaced = getMove(game, stones);
        if (stonesplaced.isEmpty()) {
            res += "trade";
        } else {
            Stone s = stonesplaced.get(0);
            res += "place " + s + " at " + s.getPosition();
        }
        return res;
    }
}
