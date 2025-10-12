package RouteMapMaker.converters;

import java.util.Arrays;

import RouteMapMaker.models.enums.FileType;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * FileType と ExtensionFilter の相互変換を行います。
 */
public class FileTypeConverter {
    /**
     * ExtensionFilter に変換します。
     *
     * @param type FileType
     * @return ExtensionFilter
     */
    public static ExtensionFilter toExtensionFilter(FileType type) {
        return new ExtensionFilter(type.getDescription(), type.getExtensions());
    }

    /**
     * FileType に変換します。
     *
     * @param filter ExtensionFilter
     * @return
     */
    public static FileType fromExtensionFilter(ExtensionFilter filter) {
        return Arrays.stream(FileType.values())
            .filter(ft -> ft.getDescription().equals(filter.getDescription())
                       && ft.getExtensions().equals(filter.getExtensions()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("ExtensionFilter からの変換に失敗しました。"));
    }
}
