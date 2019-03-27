package trio.view;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trio.game.Representation;
import trio.game.step.Step;
import trio.model.field.CellType;
import trio.model.field.Coordinates;
import trio.model.field.Field;
import trio.game.step.StepPerformer;

import java.util.function.Consumer;


public class GameFormController implements Representation, StepPerformer {
	@FXML
	private Circle   primoCircle;
	@FXML
	private Circle   secundoCircle;
	@FXML
	private Label    nameUserLabel;
	@FXML
	private Label    scoreLabel;
	@FXML
	private Label    nameUser2Label;
	@FXML
	private Label    score2Label;
	@FXML
	private GridPane area;
	
	private Button[][]  buttons;
	private int         width;
	private int         height;
	private Coordinates firstButton;
	
	private Consumer<Step> stepSupplier;
	private boolean        enabled;
	
	@Override
	public void setGamerScore(String gamerName, int score) {
		if (gamerName.equals(nameUserLabel.getText())) {
			scoreLabel.setText(String.valueOf(score));
		} else if (gamerName.equals(nameUser2Label.getText())) {
			score2Label.setText(String.valueOf(score));
		} else {
			throw new RuntimeException("Игрок с таким именем не зарегистрирован!");
		}
	}
	
	@SuppressWarnings("CssUnknownTarget")
	@Override
	public void setField(Field field) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				CellType cellType = field.get(i, j);
				
				if (cellType == null) {
					buttons[i][j].setStyle("");
					continue;
				}
				
				switch (cellType) {
					case TYPE1:
						buttons[i][j].setStyle("-fx-background-image: url('trio/view/images/image1.png')");
						break;
					case TYPE2:
						buttons[i][j].setStyle("-fx-background-image: url('trio/view/images/image2.png')");
						break;
					case TYPE3:
						buttons[i][j].setStyle("-fx-background-image: url('trio/view/images/image3.png')");
						break;
					case TYPE4:
						buttons[i][j].setStyle("-fx-background-image: url('trio/view/images/image4.png')");
						break;
					case TYPE5:
						buttons[i][j].setStyle("-fx-background-image: url('trio/view/images/image5.png')");
						break;
				}
			}
		}
	}
	
	@Override
	public void setEnabledMakeStep(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			primoCircle.setFill(Color.GREEN);
			secundoCircle.setFill(Color.RED);
		} else {
			primoCircle.setFill(Color.RED);
			secundoCircle.setFill(Color.GREEN);
		}
	}
	
	@Override
	public void subscribeToMakeStep(Consumer<Step> stepSupplier) {
		this.stepSupplier = stepSupplier;
	}
	
	void init(int columnCount, int rowCount, String nameGamerOne, String nameGamerTwo) {
		width = columnCount;
		height = rowCount;
		nameUserLabel.setText(nameGamerOne);
		nameUser2Label.setText(nameGamerTwo);
		
		buttons = new Button[rowCount][];
		for (int i = 0; i < rowCount; i++) {
			buttons[i] = new Button[columnCount];
		}
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				Button button = new Button();
				buttons[i][j] = button;
				
				GridPane.setMargin(button, new Insets(1));
				button.setPrefSize(70, 70);
				int finalI = i;
				int finalJ = j;
				button.setOnAction(event -> {
					if (!enabled) return;
					
					Coordinates coordinates = new Coordinates(finalJ, finalI);
					if (firstButton == null) {
						firstButton = coordinates;
					} else {
						stepSupplier.accept(new Step(firstButton, coordinates));
						firstButton = null;
					}
				});
				
				area.add(button, j, i);
			}
		}
	}
}