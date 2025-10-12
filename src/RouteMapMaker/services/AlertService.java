package RouteMapMaker.services;

import java.util.Optional;

import RouteMapMaker.factories.AlertFactory;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * アラート表示用のクラスです。
 */
public class AlertService {
    private final AlertFactory alertFactory;

    public AlertService(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    /**
     * エラーのアラートを表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showError(String message) {
        showAlert(AlertType.ERROR, message);
    }

    /**
     * エラーのアラートを非同期で表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showErrorAsync(String message) {
        showAlertAsync(AlertType.ERROR, message);
    }

    /**
     * 警告のアラートを表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showWarning(String message) {
        showAlert(AlertType.WARNING, message);
    }

    /**
     * 警告のアラートを非同期で表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showWarningAsync(String message) {
        showAlertAsync(AlertType.WARNING, message);
    }

    /**
     * 情報のアラートを表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showInformation(String message) {
        showAlert(AlertType.INFORMATION, message);
    }

    /**
     * 情報のアラートを非同期で表示します。
     *
     * @param message 表示するメッセージ
     */
    public void showInformationAsync(String message) {
        showAlertAsync(AlertType.INFORMATION, message);
    }

    /**
     * 確認のアラートを表示します。
     * 
     * @param message 表示するメッセージ
     * @return 選択結果
     */
    public Optional<ButtonType> showConfirmation(String message) {
        var alert = alertFactory.createAlert(AlertType.CONFIRMATION);
        alert.setContentText(message);

        return alert.showAndWait();

    }

    /**
     * 確認のアラートを表示します。
     *
     * @param message 表示するメッセージ
     * @param title アラートのタイトル
     * @return 選択結果
     */
    public Optional<ButtonType> showConfirmation(String message, String title) {
        var alert = alertFactory.createAlert(AlertType.CONFIRMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);

        return alert.showAndWait();
    }

    /**
     * 確認のアラートを表示します。
     *
     * @param message 表示するメッセージ
     * @param buttons アラートに使用するボタン
     * @return 選択結果
     */
    public Optional<ButtonType> showConfirmation(String message, ButtonType ...buttons) {
        var alert = alertFactory.createAlert(AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(buttons);

        return alert.showAndWait();
    }

    /**
     * アラートを表示します。
     *
     * @param alertType アラートの種類
     * @param message 表示するメッセージ
     */
    private void showAlert(AlertType alertType, String message) {
        var alert = alertFactory.createAlert(alertType, message, ButtonType.CLOSE);
        alert.showAndWait();
    }

    /**
     * アラートを非同期で表示します。
     *
     * @param alertType アラートの種類
     * @param message 表示するメッセージ
     */
    private void showAlertAsync(AlertType alertType, String message) {
        var alert = alertFactory.createAlert(alertType, message, ButtonType.CLOSE);
        alert.show();
    }
}
