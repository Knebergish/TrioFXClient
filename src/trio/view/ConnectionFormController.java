package trio.view;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import trio.core.Response;
import trio.core.TrioFacade;
import trio.game.GameCredentials;
import trio.game.GameOrchestrator;
import trio.model.game.Game;
import trio.model.gamer.Gamer;
import trio.view.fxml.FXMLManager;
import trio.view.fxml.RootWithController;
import trio.game.step.AIStepPerformer;
import trio.game.step.StepPerformer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;


@SuppressWarnings("unused")
public class ConnectionFormController {
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
		createButton.setOnAction(event -> {
			String gameId = createGame();
			connectToGame(gameId);
		});
		connectButton.setOnAction(event -> {
			String gameId = askGameId();
			connectToGame(gameId);
		});
	}
	
	private String createGame() {
		try {
			return getDataFromResponse(trioFacade.createGame());
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String askGameId() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Введите ID игры для подключения");
		dialog.showAndWait();
		
		String gameId = dialog.getResult();
		if (gameId != null && gameId.isEmpty()) gameId = null;
		return gameId;
	}
	
	private void connectToGame(final String gameId) {
		try {
			if (gameId == null || gameId.isEmpty()) return;
			if (nameTextField.getText() == null || nameTextField.getText().isEmpty()) return;
			final String gamerName = new String(nameTextField.getText().getBytes(), StandardCharsets.UTF_8);
			final String gamerId   = getDataFromResponse(trioFacade.connectToGame(gameId, gamerName));
			if (gamerId == null) return;
			
			final Stage stage = showWaiter(gameId);
			
			new Thread(() -> {
				try {
					while (true) {
						Game game = getDataFromResponse(trioFacade.getGameState(gameId, gamerId));
						if (game == null) {
							Platform.runLater(() -> end(null, null));
							return;
						}
						if (game.getGamers().size() == 2) {
							Platform.runLater(() -> end(gameId, gamerId));
							return;
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
					showError(e.getMessage());
					Platform.runLater(() -> end(null, null));
				} finally {
					Platform.runLater(stage::close);
				}
			}).start();
		} catch (RemoteException e) {
			e.printStackTrace();
			showError(e.getMessage());
		}
	}
	
	private void showError(String text) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ошибка");
		alert.setHeaderText("Произошла ошибка!");
		alert.setContentText(text);
		alert.show();
	}
	
	private <T> T getDataFromResponse(Response<T> response) {
		T data;
		if (response.hasError()) {
			showError(response.getErrorText());
			data = null;
		} else {
			data = response.getData();
		}
		return data;
	}
	
	private Stage showWaiter(String gameId) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("forms/WaiterForm.fxml"));
		Parent     root   = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			System.err.println("Если это произошло, то всё пропало.");
		}
		WaiterController controller = loader.getController();
		controller.init(gameId);
		Stage stage = new Stage();
		stage.setResizable(false);
//		stage.initStyle(StageStyle.UNDECORATED);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		return stage;
	}
	
	private void end(String gameId, String gamerId) {
		System.out.println(gameId + " " + gamerId);
		
		Game game;
		try {
			game = trioFacade.getGameState(gameId, gamerId).getData();
		} catch (RemoteException e) {
			showError(e.getMessage());
			return;
		}
		
		Gamer primoGamer = game.getGamers().get(0).getName().equals(nameTextField.getText())
		                   ? game.getGamers().get(0)
		                   : game.getGamers().get(1);
		Gamer secundoGamer = !game.getGamers().get(0).getName().equals(nameTextField.getText())
		                     ? game.getGamers().get(0)
		                     : game.getGamers().get(1);
		
		
		RootWithController<GameFormController> gameForm = FXMLManager.load("GameForm");
		gameForm.getController().init(game.getField().getWidth(),
		                              game.getField().getHeight(),
		                              primoGamer.getName(),
		                              secundoGamer.getName());
		Stage stage = FXMLManager.createStage(gameForm.getRoot(), "Игра");
		stage.setOnCloseRequest(event -> System.exit(1));
		stage.show();
		
		StepPerformer stepPerformer;
		if (aiMode.isSelected()) {
			stepPerformer = new AIStepPerformer();
		} else {
			stepPerformer = gameForm.getController();
		}
		
		GameOrchestrator gameOrchestrator = new GameOrchestrator(trioFacade,
		                                                         new GameCredentials(gameId,
		                                                                             gamerId,
		                                                                             nameTextField.getText()),
		                                                         gameForm.getController(),
		                                                         stepPerformer);
		Thread thread = new Thread(() -> {
			try {
				gameOrchestrator.start();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		thread.start();
		
		nameTextField.getScene().getWindow().hide();
	}
}
