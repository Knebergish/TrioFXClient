package trio.game;


import trio.model.field.Field;


public interface Representation {
	void setGamerScore(String gamerName, int score);
	
	boolean askSkipError(String text);
	
	void endGame(String winnerName);
	
	void setField(Field field);
	
	void setEnabledMakeStep(boolean enabled);
}
