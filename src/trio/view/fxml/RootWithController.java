package trio.view.fxml;


import javafx.scene.Parent;


/**
 * Агрегирует разметку и её контроллер.
 *
 * @param <T> тип контроллера
 */
public class RootWithController<T> {
	private final Parent root;
	private final T      controller;
	
	RootWithController(Parent root, T controller) {
		this.root = root;
		this.controller = controller;
	}
	
	public Parent getRoot() {
		return root;
	}
	
	public T getController() {
		return controller;
	}
}
