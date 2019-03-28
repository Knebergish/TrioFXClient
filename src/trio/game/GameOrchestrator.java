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


public class GameOrchestrator {
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
		
		System.out.println("Получены учётные данные: " + creds);
	}
	
	public void start() throws RemoteException {
		int stepNumber = -1;
		
		System.out.println("Игра запущена.");
		while (true) {
			if (localStep != null) {
				representation.setEnabledMakeStep(false);
				stepPerformer.setEnabledMakeStep(false);
				System.out.println("Пользователь сходил: " + localStep);
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
			
			System.out.println("Обновляем состояние игры...");
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
				System.out.println("Игра завершена.");
				endGame(game.getWinnerGamerName());
				break;
			}
			
			System.out.println("Проверяем возможность ходить...");
			System.out.println(game.getCurrentGamerName() + " vs " + creds.getGamerName());
			boolean canMakeStep = game.getCurrentGamerName().equals(creds.getGamerName());
			if (canMakeStep) {
				stepPerformer.setField(game.getField());
				representation.setEnabledMakeStep(true);
				stepPerformer.setEnabledMakeStep(true);
				System.out.println("Ожидаем хода пользователя.");
			} else {
				System.out.println("Ожидаем ход противника.");
			}
			
			wait(200);
		}
		
		System.exit(0);
	}
	
	private <T> T checkAndGetData(Response<T> response) {
		if (response.hasError()) {
			boolean result = representation.askSkipError(response.getErrorText());
			if (result) {
				return null;
			} else {
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
			e.printStackTrace();
		}
	}
	
	private void performStep(Step step) {
		localStep = step;
	}
}
