package client;

import java.util.*;

import shared.*;

public class Player extends Observable {

	private String name;
	private int points;
	private List<Stone> stones;
	private ClientGame game;
	private Position position;
	private Stone secondLastStone;
	private List<Stone> backupStones;

	/**
	 * Create a player with specified name and game.
	 * 
	 * @param name
	 */
	public Player(String name, ClientGame game) {
		stones = new ArrayList<Stone>();
		backupStones = new ArrayList<Stone>();
		points = 0;
		this.name = name;
		this.game = game;

	}

	/**
	 * The move the player makes.
	 */
	public void makeMove() {

	}

	/**
	 * Get the position of the last stone that was placed by the humanplayer.
	 * 
	 * @return the position of the last stone.
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the stones from the backup back in the stones from the player.
	 * Deletes all the stones from the backup so its empty again.
	 */
	public void setStonesFromBackup() {
		for (int i = 0; i < backupStones.size(); i++) {
			if (!stones.contains(backupStones.get(i))) {
				stones.add(backupStones.get(i));
			}
			backupStones.remove(i);
		}
	}

	public void removeBackup() {
		backupStones.clear();
	}

	/**
	 * Checks if the player has a stone that can be placed on the specified
	 * possiblemove.
	 * 
	 * @param p
	 * @return true if the player has such a stone, otherwise false.
	 */
	public boolean isValidPossiblemove(PossibleMove p) {
		for (int i = 0; i < stones.size(); i++) {
			if (getGame().getBoard().isValidMove(p, stones.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the game the player plays.
	 * 
	 * @return game
	 */
	public ClientGame getGame() {
		return game;
	}

	/**
	 * Get the name of the player.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the stones the player has.
	 * 
	 * @return stones
	 */
	public List<Stone> getStones() {
		return stones;
	}

	/**
	 * Makes a string representation of the stones the player has. Before every
	 * stone is a number added so choosing is easy for the view.
	 * 
	 * @return String representation of the stones with a number for every
	 *         stone.
	 */
	public String stonesToString(List<Stone> stonelist) {
		String stoneString = "";
        for (int i = 0; i < stonelist.size(); i++) {
            stoneString += i + " " + stonelist.get(i).toString() + "\n";
        }
		return stoneString;
	}

	public String thisStonesToString() {
		return stonesToString(stones);
	}

	/**
	 * Take stones out of the bag after the player has made his move.
	 * 
	 * @param
	 */
	public void takeStones(List<Stone> stonesToTake) {
		for (Stone s : stonesToTake) {
			stones.add(s);
		}
	}
	
	public void removeStones(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			stones.remove(s);
		}
	}

	/**
	 * Removes the specified stone from the players stones.
	 * 
	 * @param stone
	 */
	public void removeStone(Stone stone) {
		this.stones.remove(stone);
	}

	/**
	 * Add the points the player earned by his last move to his points.
	 * 
	 * @param
	 */
	public void addPoints(int pointsToAdd) {
		this.points = this.points + pointsToAdd;
	}

	/**
	 * Get the points of the player.
	 * 
	 * @return points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Checks if the possiblemove chosen by the humanplayer can be placed on the
	 * specified board by all the rules.
	 * 
	 * @param choice
	 * @param b
	 * @param lastStone
	 * @return the stone that the player will place or null if the player
	 *         doesn't have a stone for that place.
	 */
	public Stone possibleMoveToStone(int choice, Board b, Stone lastStone) {
		Map<Position, PossibleMove> moves = b.getPossibleMoves();
		PossibleMove p = (PossibleMove) moves.values().toArray()[choice];
		List<Position> pList = new ArrayList<Position>();
		if (lastStone == null) {
			for (int i = 0; i < stones.size(); i++) {
				if (b.isValidMove(p.getPosition(), stones.get(i))) {
					Stone stone = stones.get(i);
					this.position = p.getPosition();
					stones.remove(i);
					backupStones.add(stone);
					this.secondLastStone = stone;
					return stone;
				}
			}
		} else if (secondLastStone.equals(lastStone)) {
			Position pos = lastStone.getPosition();
			pList.add(pos);
			pList.add(p.getPosition());
			pList.add(secondLastStone.getPosition());
			if (b.allStonesOneRow(pList)) {
				for (int i = 0; i < stones.size(); i++) {
					if (b.isValidMove(p.getPosition(), stones.get(i))) {
						Stone stone = stones.get(i);
						this.position = p.getPosition();
						stones.remove(i);
						backupStones.add(stone);
						this.secondLastStone = lastStone;
						return stone;
					}
				}
			}
		} else {
			Position pos = lastStone.getPosition();
			pList.add(pos);
			pList.add(p.getPosition());
			pList.add(secondLastStone.getPosition());
			if (b.allStonesOneRow(pList)) {
				for (int i = 0; i < stones.size(); i++) {
					if (b.isValidMove(p.getPosition(), stones.get(i))) {
						Stone stone = stones.get(i);
						this.position = p.getPosition();
						stones.remove(i);
						backupStones.add(stone);
						this.secondLastStone = lastStone;
						return stone;
					}
				}
			}
		}
		return null;
	}
	public List<PossibleMove> adaptPossibleMoves(List<PossibleMove> pmlist, 
					List<Stone> stonesOfPlayer, List<Stone> stonesplaced) {
		List<PossibleMove> newpmlist = new ArrayList<>();
		for (PossibleMove p : pmlist) {
			boolean oneRow = true;
			if (!stonesplaced.isEmpty()) {
				List<Space> spacelist = new ArrayList<>();
				spacelist.addAll(stonesplaced);
				spacelist.add(p);
				oneRow = game.getBoard().allOneRow(spacelist);
			}
			if (oneRow && hasStones(p, stonesOfPlayer)) {
				newpmlist.add(p);
			}
		}
		return newpmlist;
	}
	public boolean canTrade(int stonesplaced) {
		return game.getMoveCount() != 1 && stonesplaced == 0;
	}

	public boolean hasStones(PossibleMove p, List<Stone> stonesOfPlayer) {
        for (Stone s : stonesOfPlayer) {
            if (p.acceptable(s)) {
				return true;
            }
        }
		return false;
	}

	public List<Stone> adaptStones(List<Stone> stonelist, PossibleMove place) {
		List<Stone> acceptableStones = new ArrayList<>();
		for (Stone s : stonelist) {
			if (place.acceptable(s)) {
				acceptableStones.add(s);
			}
		}
		return acceptableStones;
	}

}
