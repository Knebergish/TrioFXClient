package trio.view;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trio.core.Response;
import trio.core.TrioFacade;
import trio.game.GameCredentials;
import trio.game.GameOrchestrator;
import trio.game.step.AIStepPerformer;
import trio.game.step.StepPerformer;
import trio.model.game.Game;
import trio.model.gamer.Gamer;
import trio.view.fxml.FXMLManager;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;


public class ConnectionFormController {
	private static final Logger log = Logger.getLogger("TrioLogging");
	
	private final TrioFacade trioFacade;
	
	@FXML
	private TextField nameTextField;
	@FXML
	private Button    createButton;
	@FXML
	private Button    connectButton;
	@FXML
	private CheckBox  aiMode;
	
	
	public ConnectionFormController(TrioFacade trioFacade) {
		this.trioFacade = trioFacade;
	}
	
	public void init() {
		createButton.setOnAction(event -> connectToGame(this::createGame));
		connectButton.setOnAction(event -> connectToGame(this::askGameId));
	}
	
	private void connectToGame(final GameIdSupplier supplier) {
		wrapTry(() -> {
			validateGamerName(nameTextField.getText());
			final String gameId    = supplier.getGameId();
			String       gamerName = new String(nameTextField.getText().getBytes(), StandardCharsets.UTF_8);
			log.warning("Gamer name: " + gamerName);
			connectToGame(gameId, gamerName);
		});
	}
	
	private void wrapTry(Action action) {
		try {
			action.start();
		} catch (Exception e) {
			log.severe("Error: " + e.getMessage());
			showError(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void validateGamerName(String gamerName) throws Exception {
		if (gamerName == null || gamerName.isEmpty()) {
			throw new Exception("Имя игрока не может быть пустым.");
		}
	}
	
	private void connectToGame(String gameId, String gamerName) throws Exception {
		final String gamerId = getDataFromResponse(trioFacade.connectToGame(gameId, gamerName));
		
		final Stage stage = showWaiter(gameId);
		
		new Thread(() -> {
			wrapTry(() -> {
				while (true) {
					Game game = getDataFromResponse(trioFacade.getGameState(gameId, gamerId));
					if (game.getGamers().size() == 2) {
						Platform.runLater(() -> startGame(new GameCredentials(gameId, gamerId, gamerName)));
						return;
					}
					
					Thread.sleep(1000);
				}
			});
			Platform.runLater(stage::close);
		}).start();
	}
	
	private void showError(String text) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("Произошла ошибка!");
			alert.setContentText(text);
			alert.show();
		});
	}
	
	private <T> T getDataFromResponse(Response<T> response) throws Exception {
		if (response.hasError()) {
			throw new Exception(response.getErrorText());
		}
		return response.getData();
	}
	
	private Stage showWaiter(String gameId) {
		final var waiterForm = FXMLManager.<WaiterController>load("WaiterForm");
		waiterForm.getController().init(gameId);
		final Stage stage = FXMLManager.createStage(waiterForm.getRoot(), "Ожидайте");
		stage.show();
		return stage;
	}
	
	private void startGame(GameCredentials creds) {
		AtomicReference<Game> gameRef = new AtomicReference<>();
		wrapTry(() -> gameRef.set(getDataFromResponse(trioFacade.getGameState(creds.getGameId(), creds.getGamerId()))));
		Game game = gameRef.get();
		
		final String opponentGamerName = game.getGamers().parallelStream()
		                                     .map(Gamer::getName)
		                                     .filter(s -> !s.equals(creds.getGamerName()))
		                                     .findAny().orElseThrow();
		
		final var gameForm = FXMLManager.<GameFormController>load("GameForm");
		gameForm.getController().init(game.getField().getWidth(),
		                              game.getField().getHeight(),
		                              creds.getGamerName(),
		                              opponentGamerName);
		Stage stage = FXMLManager.createStage(gameForm.getRoot(), "Trio");
		stage.setOnCloseRequest(event -> System.exit(1));
		stage.show();
		
		StepPerformer stepPerformer;
		if (aiMode.isSelected()) {
			stepPerformer = new AIStepPerformer();
		} else {
			stepPerformer = gameForm.getController();
		}
		
		GameOrchestrator gameOrchestrator = new GameOrchestrator(trioFacade,
		                                                         creds,
		                                                         gameForm.getController(),
		                                                         stepPerformer);
		Thread thread = new Thread(() -> {
			wrapTry(gameOrchestrator::start);
			System.exit(13);
		});
		thread.start();
		
		nameTextField.getScene().getWindow().hide();
	}
	
	private String createGame() throws Exception {
		return getDataFromResponse(trioFacade.createGame());
	}
	
	private String askGameId() throws Exception {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Введите ID игры для подключения");
		dialog.showAndWait();
		
		String gameId = dialog.getResult();
		if (gameId == null || gameId.isEmpty()) {
			throw new Exception("Пустой ID. Подключение невозможно.");
		}
		return gameId;
	}
	
	@FunctionalInterface
	private interface GameIdSupplier {
		String getGameId() throws Exception;
	}
	
	
	@FunctionalInterface
	private interface Action {
		void start() throws Exception;
	}
}
