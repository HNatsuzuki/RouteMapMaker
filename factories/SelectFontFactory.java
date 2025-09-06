package RouteMapMaker.factories;

import RouteMapMaker.selectFontController;
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
    public View<selectFontController> createSelectFontView(String current) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader;
        Parent parent;

        try {
            loader = new FXMLLoader(getClass().getResource("/RouteMapMaker/selectFontController.fxml"));
            parent = loader.load();
        } catch (Exception ex) {
            throw new RuntimeException();
        }

        selectFontController controller = loader.getController();
        controller.setObject(current);
        Scene scene = sceneFactory.createScene(parent, 400, 300);
        stage.setScene(scene);
        stage.setTitle("フォントの選択");

        return new View<selectFontController>(stage, controller);
    }
}
