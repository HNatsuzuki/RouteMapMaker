package RouteMapMaker.factories;

import java.io.File;

import RouteMapMaker.converters.FileTypeConverter;
import RouteMapMaker.models.enums.FileType;
import javafx.stage.FileChooser;

/**
 * FileChooser のファクトリクラスです。
 */
public class FileChooserFactory {
    public static FileChooser create(String title, String initialDirectory, FileType... fileTypes) {
        FileChooser chooser = new FileChooser();

        if (title != null) {
            chooser.setTitle(title);
        }

        if (initialDirectory != null) {
            File dir = new File(initialDirectory);
    
            if (dir.exists()) {
                chooser.setInitialDirectory(dir);
            }
        }

        if (fileTypes == null || fileTypes.length == 0) {
            chooser.getExtensionFilters().add(FileTypeConverter.toExtensionFilter(FileType.ALL));
        } else {
            for (FileType filetype : fileTypes) {
                chooser.getExtensionFilters().add(FileTypeConverter.toExtensionFilter(filetype));
            }
        }

        return chooser;
    }
}
