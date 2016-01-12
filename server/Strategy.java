package server;

public interface Strategy {
	public String getName();

	public int determineMove();
}
