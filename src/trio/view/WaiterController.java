package trio.view;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class WaiterController {
	@FXML
	private TextField idTextField;
	
	public void init(String gameId) {
		idTextField.setText(gameId);
	}
}
