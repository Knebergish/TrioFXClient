package trio.game;


import trio.model.field.Field;


public interface Representation {
	void setGamerScore(String gamerName, int score);
	
	void setField(Field field);
	
	void setEnabledMakeStep(boolean enabled);
}
