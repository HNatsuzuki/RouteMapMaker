package RouteMapMaker.Factories;

import RouteMapMaker.Configuration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Alert のファクトリクラス
 */
public class AlertFactory {
    private final Configuration config;

    /**
     * コンストラクタ
     *
     * @param config アプリケーション設定
     */
    public AlertFactory(Configuration config) {
        this.config = config;
    }

    /**
     * Alert を作成します。
     *
     * @param alertType アラートタイプ
     * @return Alert
     */
    public Alert createAlert(AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.getDialogPane().setStyle("-fx-font-family: '" + config.getUiFont() + "';");

        return alert;
    }

    /**
     * Alert を作成します。
     *
     * @param alertType アラートタイプ
     * @param contentText タイトル
     * @param buttonType ボタンタイプ
     * @return Alert
     */
    public Alert createAlert(AlertType alertType, String contentText, ButtonType buttonType) {
        Alert alert = new Alert(alertType, contentText, buttonType);
        alert.getDialogPane().setStyle("-fx-font-family: '" + config.getUiFont() + "';");

        return alert;
    }
}
