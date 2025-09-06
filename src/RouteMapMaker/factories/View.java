package RouteMapMaker.factories;

import javafx.stage.Stage;

/**
 * ファクトリで作成した view の情報を表すクラスです。
 */
public class View<T> {
    private final Stage stage;
    private final T controller;

    public Stage getStage() { return this.stage; }
    public T getController() { return this.controller; }

    public View(Stage stage, T controller) {
        this.stage = stage;
        this.controller = controller;
    }
}
