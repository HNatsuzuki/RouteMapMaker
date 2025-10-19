package RouteMapMaker.services;

import java.util.Optional;

import RouteMapMaker.controllers.SelectFontController;
import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.factories.View;
import javafx.stage.Stage;

/**
 * フォント選択ダイアログ表示用クラスです。
 */
public class FontSelectDialogService implements DialogService<String, String> {
    private final SelectFontFactory selectFontFactory;

    public FontSelectDialogService(SelectFontFactory selectFontFactory) {
        this.selectFontFactory = selectFontFactory;
    }

    @Override
    public Optional<String> showDialog() {
        return showDialog("System");
    }

    @Override
    public Optional<String> showDialog(String value) {
        View<SelectFontController> view = selectFontFactory.createSelectFontView(value);
        Stage stage = view.getStage();
        SelectFontController controller = view.getController();
        stage.showAndWait();

        if (controller.isAccepted()) {
            return Optional.of(controller.getSelectedFontName());
        } else {
            return Optional.empty();
        }
    }
}
