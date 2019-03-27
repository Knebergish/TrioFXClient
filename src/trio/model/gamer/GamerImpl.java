package trio.model.gamer;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class GamerImpl implements Gamer, Serializable {
	@JsonProperty("name")
	private final String name;
	@JsonProperty("score")
	private final int    score;
	
	@JsonCreator
	public GamerImpl(@JsonProperty("name") String name,
	                 @JsonProperty("score") int score) {
		this.name = name;
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "GamerImpl{" +
		       "name='" + name + '\'' +
		       ", score=" + score +
		       '}';
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getScore() {
		return score;
	}
}



