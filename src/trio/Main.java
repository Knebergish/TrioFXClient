package trio;


import javafx.application.Application;
import javafx.stage.Stage;
import trio.core.TrioFacade;
import trio.core.TrioFacadeFactory;
import trio.view.ConnectionFormController;
import trio.view.fxml.FXMLManager;


public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
//		TrioFacade trioFacade = TrioFacadeFactory.getHttpTrioFacade();
		TrioFacade trioFacade = TrioFacadeFactory.getRMITrioFacade();
		
		var connectionForm = FXMLManager.load("ConnectionForm", new ConnectionFormController(trioFacade));
		connectionForm.getController().init();
		Stage stage = FXMLManager.createStage(connectionForm.getRoot(), "Подключение");
		stage.show();
	}
}
