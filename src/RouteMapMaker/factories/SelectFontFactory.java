package RouteMapMaker.factories;

import RouteMapMaker.controllers.SelectFontController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * フォント選択画面のファクトリクラス
 */
public class SelectFontFactory {
    private final SceneFactory sceneFactory;

    /**
     * コンストラクタ
     *
     * @param sceneFactory SceneFactory
     */
    public SelectFontFactory(SceneFactory sceneFactory) {
        this.sceneFactory = sceneFactory;
    }

    /**
     * フォント選択画面を作成します。
     *
     * @param  current 現在のフォント
     * @return 画面情報
     */
    public View<SelectFontController> createSelectFontView(String current) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader;
        Parent parent;

        try {
            loader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/selectFontController.fxml"));
            loader.setControllerFactory(param -> {
                if (param == SelectFontController.class) {
                    return new SelectFontController(current);
                } else {
                    throw new RuntimeException();
                }
            });
            parent = loader.load();
        } catch (Exception ex) {
            throw new RuntimeException();
        }

        SelectFontController controller = loader.getController();
        Scene scene = sceneFactory.createScene(parent, 400, 300);
        stage.setScene(scene);
        stage.setTitle("フォントの選択");

        return new View<SelectFontController>(stage, controller);
    }
}
