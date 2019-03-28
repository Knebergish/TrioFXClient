package trio;


import javafx.application.Application;
import javafx.stage.Stage;
import trio.core.TrioFacade;
import trio.core.TrioFacadeFactory;
import trio.view.ConnectionFormController;
import trio.view.fxml.FXMLManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Main extends Application {
	public static void main(String[] args) throws IOException {
		LogManager logManager = LogManager.getLogManager();
		logManager.readConfiguration(new FileInputStream("./config/logging.properties"));
		Logger logger = Logger.getLogger("TrioLogging");
		logManager.addLogger(logger);
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		TrioFacade trioFacade = new TrioFacadeFactory().getTrioFacade();
		
		var connectionForm = FXMLManager.load("ConnectionForm", new ConnectionFormController(trioFacade));
		connectionForm.getController().init();
		Stage stage = FXMLManager.createStage(connectionForm.getRoot(), "Подключение");
		stage.show();
	}
}
