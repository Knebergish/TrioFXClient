package trio.view;


import trio.model.field.Coordinates;


public class Step {
	private final Coordinates source;
	private final Coordinates dest;
	
	public Step(Coordinates source, Coordinates dest) {
		this.source = source;
		this.dest = dest;
	}
	
	@Override
	public String toString() {
		return "Step{" +
		       "source=" + source +
		       ", dest=" + dest +
		       '}';
	}
	
	public Coordinates getSource() {
		return source;
	}
	
	public Coordinates getDest() {
		return dest;
	}
}
