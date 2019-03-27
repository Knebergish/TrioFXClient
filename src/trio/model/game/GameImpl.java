package trio.model.game;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import trio.ObjectMapperFactory;
import trio.model.field.Field;
import trio.model.field.FieldImpl;
import trio.model.field.StepResult;
import trio.model.gamer.Gamer;
import trio.model.gamer.GamerImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameImpl implements Game, Serializable {
	@JsonProperty("field")
	private final String          field;
	@JsonProperty("current_gamer_name")
	private final String          currentGamerName;
	@JsonProperty("gamers")
	private final List<GamerImpl> gamers;
	@JsonProperty("step_number")
	private final int             stepNumber;
	@JsonProperty("last_step_result")
	private final String          lastStepResult;
	@JsonProperty("status")
	private final int             status;
	@JsonProperty("winner_gamer_name")
	private final String          winnerGamerName;
	
	@JsonCreator
	public GameImpl(@JsonProperty("field") String field,
	                @JsonProperty("current_gamer_name") String currentGamerName,
	                @JsonProperty("gamers") List<GamerImpl> gamers,
	                @JsonProperty("step_number") int stepNumber,
	                @JsonProperty("last_step_result") String lastStepResult,
	                @JsonProperty("status") int status,
	                @JsonProperty("winner_gamer_name") String winnerGamerName) {
		this.field = field;
		this.currentGamerName = currentGamerName;
		this.gamers = new ArrayList<>(gamers);
		this.stepNumber = stepNumber;
		this.lastStepResult = lastStepResult;
		this.status = status;
		this.winnerGamerName = winnerGamerName;
	}
	
	@Override
	public String toString() {
		return "GameImpl{" +
		       "field=" + field +
		       ", currentGamerName='" + currentGamerName + '\'' +
		       ", gamers=" + gamers +
		       ", stepNumber=" + stepNumber +
		       ", lastStepResult=" + lastStepResult +
		       ", status=" + status +
		       ", winnerGamerName='" + winnerGamerName + '\'' +
		       '}';
	}
	
	@Override
	public List<Gamer> getGamers() {
		return Collections.unmodifiableList(gamers);
	}
	
	@Override
	public Field getField() {
		ObjectMapper mapper = ObjectMapperFactory.createMapper();
		Field        f;
		try {
			f = mapper.readerFor(FieldImpl.class).readValue(field);
		} catch (IOException e) {
			f = null;
			e.printStackTrace();
		}
		return f;
	}
	
	@Override
	public String getCurrentGamerName() {
		return currentGamerName;
	}
	
	@Override
	public int getStepNumber() {
		return stepNumber;
	}
	
	@Override
	public StepResult getLastStepResult() {
		ObjectMapper mapper = ObjectMapperFactory.createMapper();
		StepResult   s;
		try {
			s = mapper.readerFor(StepResult.class).readValue(lastStepResult);
		} catch (IOException e) {
			s = null;
			e.printStackTrace();
		}
		return s;
	}
	
	@Override
	public int getStatus() {
		return status;
	}
	
	@Override
	public String getWinnerGamerName() {
		return winnerGamerName;
	}
}
