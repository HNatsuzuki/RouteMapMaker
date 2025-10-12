package RouteMapMaker.models;

import java.io.File;

import RouteMapMaker.models.enums.FileType;

/**
 * ファイル選択ダイアログの選択結果を表すクラスです。
 */
public class FileSelectionResult {
    private final File file;
    private final FileType fileType;

    public FileSelectionResult(File file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    public File getFile() {
        return file;
    }

    public FileType getFileType() {
        return fileType;
    }
}
