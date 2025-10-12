package RouteMapMaker.services;

import java.io.File;
import java.util.Optional;

import RouteMapMaker.converters.FileTypeConverter;
import RouteMapMaker.factories.FileChooserFactory;
import RouteMapMaker.models.FileSelectionResult;
import RouteMapMaker.models.enums.FileType;
import javafx.stage.FileChooser;

/**
 * 開く用のファイル選択ダイアログ表示用クラスです。
 */
public class FileOpenDialogService implements FileSelectDialogService {
    /**
     * 開く用のファイル選択ダイアログを表示します。
     *
     * @param title ダイアログのタイトル
     * @param initialDirectory 最初に表示するディレクトリ
     * @param fileTypes 選択可能にする拡張子
     * @return 選択結果
     */
    @Override
    public Optional<FileSelectionResult> showDialog(String title, String initialDirectory, FileType... fileTypes) {
        FileChooser chooser = FileChooserFactory.create(title, initialDirectory, fileTypes);
        File selectedFile = chooser.showOpenDialog(null);

        if (selectedFile == null) {
            return Optional.empty();
        } else {
            return Optional.of(new FileSelectionResult(selectedFile, FileTypeConverter.fromExtensionFilter(chooser.getSelectedExtensionFilter())));
        }
    }
}
