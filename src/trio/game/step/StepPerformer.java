package trio.game.step;


import trio.model.field.Field;

import java.util.function.Consumer;


public interface StepPerformer {
	void subscribeToMakeStep(Consumer<Step> stepSupplier);
	
	void setField(Field field);
	
	void setEnabledMakeStep(boolean enabled);
}
