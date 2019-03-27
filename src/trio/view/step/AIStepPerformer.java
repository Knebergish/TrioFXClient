package trio.view.step;


import javafx.application.Platform;
import trio.core.FieldManipulator;
import trio.core.PossibleStep;
import trio.model.field.Field;
import trio.view.Step;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;


public class AIStepPerformer implements StepPerformer {
	private Consumer<Step> stepSupplier;
	private Field          field;
	private boolean        enabled;
	
	@Override
	public void subscribeToMakeStep(Consumer<Step> stepSupplier) {
		this.stepSupplier = stepSupplier;
	}
	
	@Override
	public void setField(Field field) {
		this.field = field;
	}
	
	@Override
	public void setEnabledMakeStep(boolean enabled) {
		if (this.enabled && enabled) return;
		
		this.enabled = enabled;
		if (enabled) {
			FutureTask<Void> task = new FutureTask<>(() -> {
				Thread.sleep(500);
				List<PossibleStep> possibleSteps = FieldManipulator.getPossibleSteps(field);
				stepSupplier.accept(new Step(possibleSteps.get(0).getSource(),
				                             possibleSteps.get(0).getDest()));
				return null;
			});
			Platform.runLater(task);
		}
	}
}