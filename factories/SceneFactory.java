package RouteMapMaker.factories;

import RouteMapMaker.Configuration;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Scene のファクトリクラス
 */
public class SceneFactory {
    private final Configuration config;

    /**
     * コンストラクタ
     *
     * @param config アプリケーション設定
     */
    public SceneFactory(Configuration config) {
        this.config = config;
    }

    /**
     * Scene を作成します。
     * @param root Scene のルートノード
     * @return 作成した Scene
     */
    public Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        setStyle(scene);

        return scene;
    }

    /**
     * Scene を作成します。
     * @param root Scene のルートノード
     * @param width Scene の幅
     * @param height Scene の高さ
     * @return 作成した Scene
     */
    public Scene createScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        setStyle(scene);

        return scene;
    }

    /**
     * Scene のスタイル設定を行います。
     *
     * @param scene スタイル設定を行う Scene
     */
    private void setStyle(Scene scene) {
        // フォントを設定する
        scene.getRoot().setStyle(generateFontStyle(config.getUiFont()));

        // それ以外のスタイル設定適用 (未使用？)
        scene.getStylesheets().add(getClass().getResource("/RouteMapMaker/application.css").toExternalForm());

        // フォント設定変更時に即座に適用するためのイベント設定
        config.getUiFontProperty().addListener((obs, oldFont, newFont) -> {
            scene.getRoot().setStyle(generateFontStyle(newFont));
        });
    }

    /**
     * フォント設定のスタイル生成を行います。
     *
     * @param fontName フォント名
     * @return スタイル
     */
    private String generateFontStyle(String fontName) {
        return String.format("-fx-font-family:'%1$s';-fx-tick-label-font-family:'%1$s';", fontName);
    }
}
