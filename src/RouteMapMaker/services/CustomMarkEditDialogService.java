package RouteMapMaker.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import RouteMapMaker.controllers.CustomMarkController;
import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.models.StopMark;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * カスタムマーク編集ダイアログ表示クラスです。
 */
public class CustomMarkEditDialogService implements DialogService<Void, List<StopMark>> {
    private final ObservableList<StopMark> customMarks;
    private final SceneFactory sceneFactory;
    private final AlertService alert;
    private final FontSelectDialogService fontSelectDialog;
    private final FileOpenDialogService fileOpenDialog;
    private final Configuration config;

    public CustomMarkEditDialogService(
        ObservableList<StopMark> customMarks, SceneFactory sceneFactory, FontSelectDialogService fontSelectDialog,
        AlertService alert, FileOpenDialogService fileOpenDialog, Configuration config) {
        this.customMarks = customMarks;
        this.sceneFactory = sceneFactory;
        this.fontSelectDialog = fontSelectDialog;
        this.alert = alert;
        this.fileOpenDialog = fileOpenDialog;
        this.config = config;
    }

    @Override
    public Optional<List<StopMark>> showDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/CustomMarkController.fxml"));
        loader.setControllerFactory(param -> {
            if (param == CustomMarkController.class) {
                return new CustomMarkController(customMarks, fontSelectDialog, alert, fileOpenDialog, config);
            } else {
                throw new RuntimeException();
            }
        });

        try {
            Parent parent = loader.load();
            Scene scene = sceneFactory.createScene(parent);
            stage.setScene(scene);
            stage.setTitle("カスタム停車マークの編集");
            stage.showAndWait();

            return Optional.of(customMarks);
        } catch (IOException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }
}
