package trio.view.fxml;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public final class FXMLManager {
	private FXMLManager() {
	}
	
	public static <T> RootWithController<T> load(String filePath) {
		return load(filePath, null);
	}
	
	/**
	 * Загружает разметку из файла.
	 *
	 * @param fileName   имя файла с разметкой
	 * @param <T>        тип контроллера к этой разметке
	 * @param controller контроллер, если он не задан в разметке
	 * @return разметка и контроллер к ней
	 */
	public static <T> RootWithController<T> load(String fileName, T controller) {
		final String filePath = "trio/view/forms/" + fileName + ".fxml";
		final Parent root;
		
		FXMLLoader loader = new FXMLLoader(FXMLManager.class.getClassLoader().getResource(filePath));
		if (controller != null) {
			loader.setController(controller);
		}
		try {
			root = loader.load();
		} catch (Exception e) {
			throw new RuntimeException("Ошибка загрузки формы: " + filePath, e);
		}
		
		return new RootWithController<>(root, loader.getController());
	}
	
	public static Stage createStage(Parent root, String title) {
		return createStage(root, title, 0, 0);
	}
	
	/**
	 * Оборачивает разметку в Stage для открытия в новом окне.
	 *
	 * @param root   разметка
	 * @param title  заголовок окна
	 * @param width  ширина окна
	 * @param height высота окна
	 * @return Stage с разметкой
	 */
	public static Stage createStage(Parent root, String title, int width, int height) {
		Stage stage = new Stage();
		stage.setTitle(title);
		stage.getIcons().add(new Image("file:res/icon.jpg"));
		stage.setResizable(false);
		
		if (width > 0) {
			stage.setWidth(width);
		}
		if (height > 0) {
			stage.setHeight(height);
		}
		Scene scene = new Scene(root);
		stage.setScene(scene);
		return stage;
	}
}
