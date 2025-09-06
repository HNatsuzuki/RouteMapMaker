package RouteMapMaker.file;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javafx.scene.image.Image;

/**
 * 保存データを表します。
 */
public class SaveData {
    private final Properties properties;
    private final Map<Integer, Image> images;

    /**
     * 設定値を保存する Properties を取得します。
     *
     * @return 設定値を保存する Properties
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * 画像を保存する Map を取得します。
     *
     * @return 画像を保存する Map
     */
    public Map<Integer, Image> getImages() {
        return this.images;
    }

    /**
     * コンストラクタ
     *
     * @param properties Properties
     * @param images images
     */
    public SaveData(Properties properties, Map<Integer, Image> images) {
        this.properties = properties;
        this.images = images;
    }

    /**
     * ソート済みの Property を返します。
     *
     * @return ソート済み Property
     * @throws IOException ソート失敗時にスローされます。
     */
    public String[] getSortedProperties() throws IOException {
        // propertiesファイルの出力を文字列でソートする
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter pw = new PrintWriter(sw)) {
                properties.store(pw, null);
            }

            String[] propertyArray = Stream.of(sw.toString().split("\n")).sorted().toArray(String[]::new);

            return propertyArray;
        }
    }
}
