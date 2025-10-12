package RouteMapMaker.services;

import java.util.Optional;

import RouteMapMaker.models.FileSelectionResult;
import RouteMapMaker.models.enums.FileType;

/**
 * ファイル選択ダイアログのインターフェイスです。
 */
public interface FileSelectDialogService {
    /**
     * ファイル選択ダイアログを表示します。
     *
     * @param title ダイアログのタイトル
     * @param initialDirectory 最初に表示するディレクトリ
     * @param fileTypes 選択可能にする拡張子
     * @return 選択結果
     */
    public Optional<FileSelectionResult> showDialog(String title, String initialDirectory, FileType ...fileTypes);
}
