package RouteMapMaker.services;

import java.util.Optional;

import RouteMapMaker.controllers.SelectFontController;
import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.factories.View;
import javafx.stage.Stage;

/**
 * フォント選択ダイアログ表示用クラスです。
 */
public class FontSelectDialogService implements DialogService<String> {
    private final String currentFont;
    private final SelectFontFactory selectFontFactory;

    public FontSelectDialogService(String currentFont, SelectFontFactory selectFontFactory) {
        this.currentFont = currentFont;
        this.selectFontFactory = selectFontFactory;
    }

    @Override
    public Optional<String> showDialog() {
        View<SelectFontController> view = selectFontFactory.createSelectFontView(currentFont);
        Stage stage = view.getStage();
        SelectFontController controller = view.getController();
        stage.showAndWait();

        if (controller.shouldSave()) {
            return Optional.of(controller.getFontName());
        } else {
            return Optional.empty();
        }
    }
}
