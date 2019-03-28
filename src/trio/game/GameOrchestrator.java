package trio.game;


import trio.core.Response;
import trio.core.TrioFacade;
import trio.game.step.Step;
import trio.game.step.StepPerformer;
import trio.model.field.Field;
import trio.model.field.StepResult;
import trio.model.game.Game;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;


public class GameOrchestrator {
	private static final Logger log = Logger.getLogger("TrioLogging");
	
	private final TrioFacade      trioFacade;
	private final GameCredentials creds;
	private final Representation  representation;
	private final StepPerformer   stepPerformer;
	
	private Step localStep;
	
	public GameOrchestrator(TrioFacade trioFacade,
	                        GameCredentials creds,
	                        Representation representation,
	                        StepPerformer stepPerformer) {
		this.trioFacade = trioFacade;
		this.creds = creds;
		this.representation = representation;
		this.stepPerformer = stepPerformer;
		
		stepPerformer.setEnabledMakeStep(false);
		stepPerformer.subscribeToMakeStep(this::performStep);
		
		log.warning("Credentials received: " + creds);
	}
	
	public void start() throws RemoteException {
		int stepNumber = -1;
		
		log.warning("Game started.");
		while (true) {
			if (localStep != null) {
				representation.setEnabledMakeStep(false);
				stepPerformer.setEnabledMakeStep(false);
				log.info("User made a step: " + localStep);
				StepResult localStepResult = checkAndGetData(trioFacade.makeStep(creds.getGameId(),
				                                                                 creds.getGamerId(),
				                                                                 localStep.getSource(),
				                                                                 localStep.getDest()));
				localStep = null;
				if (localStepResult == null) continue;
				if (!localStepResult.isMoved()) continue;
				drawAnimation(localStepResult);
				stepNumber++;
			}
			
			log.info("Update game state...");
			Game game = checkAndGetData(trioFacade.getGameState(creds.getGameId(), creds.getGamerId()));
			if (game == null) continue;
			if (stepNumber != game.getStepNumber()) {
				stepNumber = game.getStepNumber();
				if (game.getLastStepResult() != null) {
					drawAnimation(game.getLastStepResult());
				}
			}
			updateState(game);
			
			if (game.getStatus() == 2) {
				log.warning("Game is end.");
				endGame(game.getWinnerGamerName());
				break;
			}
			
			log.info("Check the ability to make step...");
			log.info(game.getCurrentGamerName() + " vs " + creds.getGamerName());
			boolean canMakeStep = game.getCurrentGamerName().equals(creds.getGamerName());
			if (canMakeStep) {
				stepPerformer.setField(game.getField());
				representation.setEnabledMakeStep(true);
				stepPerformer.setEnabledMakeStep(true);
				log.info("Waiting for user step.");
			} else {
				log.info("Waiting for opponent step.");
			}
			
			wait(200);
		}
		
		System.exit(0);
	}
	
	private <T> T checkAndGetData(Response<T> response) {
		if (response.hasError()) {
			log.severe("Game error: " + response.getErrorText());
			boolean result = representation.askSkipError(response.getErrorText());
			if (result) {
				log.severe("Error skipped.");
				return null;
			} else {
				log.severe("Interruption.");
				System.exit(13);
			}
		}
		
		return response.getData();
	}
	
	private void drawAnimation(StepResult stepResult) {
		if (!stepResult.isMoved()) {
			return;
		}
		
		List<Field> states = stepResult.getStates();
		for (Field state : states) {
			representation.setField(state);
			wait(400);
		}
	}
	
	private void updateState(Game game) {
		representation.setField(game.getField());
		representation.setGamerScore(game.getGamers().get(0).getName(), game.getGamers().get(0).getScore());
		representation.setGamerScore(game.getGamers().get(1).getName(), game.getGamers().get(1).getScore());
	}
	
	private void endGame(String gamerName) {
		representation.endGame(gamerName);
	}
	
	private void wait(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			log.severe("Pause error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void performStep(Step step) {
		localStep = step;
	}
}
