package server;

/**
 * Created by jjk on 1/12/16.
 */
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position above() {
        return new Position(x, y-1);
    }
    public Position below() {
        return new Position(x, y+1);
    }
    public Position right() {
        return new Position(x+1, y);
    }
    public Position left() {
        return new Position(x-1, y);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        Position p = (Position) obj;
        return (p.getX() == getX()) && (p.getY() == getY());
    }

    @Override
    public String toString() {
        return "(" + toUsableString() +")";
    }
    
    public String toUsableString() {
    	return getX() + "," + getY();
    }
}
