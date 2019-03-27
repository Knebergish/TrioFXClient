package trio.model.game;


import trio.model.field.Field;
import trio.model.field.StepResult;
import trio.model.gamer.Gamer;

import java.util.List;


public interface Game {
	List<Gamer> getGamers();
	
	Field getField();
	
	String getCurrentGamerName();
	
	int getStepNumber();
	
	StepResult getLastStepResult();
	
	int getStatus();
	
	String getWinnerGamerName();
}