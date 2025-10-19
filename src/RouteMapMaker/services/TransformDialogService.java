package RouteMapMaker.services;

import java.io.IOException;
import java.util.Optional;

import RouteMapMaker.controllers.TransformController;
import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.models.ScaleParameters;
import RouteMapMaker.models.TransformParameters;
import RouteMapMaker.models.TranslateParameters;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 変形ダイアログ表示用クラスです。
 */
public class TransformDialogService implements DialogService<Void, TransformParameters> {
    private final SceneFactory sceneFactory;
    private final AlertService alert;
    private final Dimension2D canvasSize;

    public TransformDialogService(SceneFactory sceneFactory,  AlertService alert, Dimension2D canvasSize) {
        this.sceneFactory = sceneFactory;
        this.alert = alert;
        this.canvasSize = canvasSize;
    }

    @Override
    public Optional<TransformParameters> showDialog() {
        FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/TransformController.fxml"));
        editLoader.setControllerFactory(param -> {
            if (param == TransformController.class) {
                return new TransformController(alert, canvasSize);
            } else {
                throw new RuntimeException();
            }
        });

        Parent parent;

        try {
            parent = editLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("load FXML failure.", e);
        }

        Scene sc = sceneFactory.createScene(parent);
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setScene(sc);
        editStage.setTitle("座標変換");
        editStage.showAndWait();
        TransformController controller = editLoader.getController();

        switch (controller.getTransformType()) {
            case TRANSLATE:
                // 平行移動
                return Optional.of(new TranslateParameters(controller.getTranslateX(), controller.getTranslateY(), controller.isTransformWithFreeItem()));
            case SCALE:
                // 拡大縮小
                return Optional.of(new ScaleParameters(controller.getScaleX(), controller.getScaleY(), controller.getPivotX(),controller.getPivotY(), controller.isTransformWithFreeItem()));
            default:
                // キャンセル
                return Optional.empty();
        }
    }
}
