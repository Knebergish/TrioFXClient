package trio.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class Coordinates implements Serializable {
	@JsonProperty("x")
	private final int x;
	@JsonProperty("y")
	private final int y;
	
	@JsonCreator
	public Coordinates(@JsonProperty("x") int x,
	                   @JsonProperty("y") int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return 31 * getX() + getY();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Coordinates coord = (Coordinates) o;
		
		if (getX() != coord.getX()) return false;
		return getY() == coord.getY();
	}
	
	@Override
	public String toString() {
		return "Coordinates{" +
		       "x=" + getX() +
		       ", y=" + getY() +
		       "}\n";
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
