package trio.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StepResult implements Serializable {
	@JsonProperty("states")
	private final List<Field> states;
	@JsonProperty("score")
	private final int         score;
	
	@JsonCreator
	public StepResult(@JsonProperty("states") List<Field> states,
	                  @JsonProperty("score") int score) {
		this.states = new ArrayList<>(states);
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "StepResult{" +
		       "states=" + states +
		       ", score=" + score +
		       '}';
	}
	
	public List<Field> getStates() {
		return Collections.unmodifiableList(states);
	}
	
	public boolean isMoved() {
		return !states.isEmpty();
	}
	
	public int getScore() {
		return score;
	}
}
