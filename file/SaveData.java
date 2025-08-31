package RouteMapMaker.file;

import java.util.Map;
import java.util.Properties;

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
}
