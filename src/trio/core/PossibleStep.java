package trio.core;


import trio.model.field.Coordinates;


public class PossibleStep {
	private final Coordinates source;
	private final Coordinates dest;
	private final int         score;
	
	public PossibleStep(Coordinates source, Coordinates dest, int score) {
		this.source = source;
		this.dest = dest;
		this.score = score;
	}
	
	public Coordinates getSource() {
		return source;
	}
	
	public Coordinates getDest() {
		return dest;
	}
	
	public int getScore() {
		return score;
	}
}
