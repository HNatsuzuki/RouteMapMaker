package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.StopMark;
import javafx.scene.image.Image;

/**
 * StopMark と Properties の相互変換を行うクラスです。
 */
public class StopMarkPropertiesConverter {
    /**
     * StopMark から Properties に変換します。
     *
     * @param stopMark StopMark
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(StopMark stopMark, List<Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "isRotated", String.valueOf(stopMark.isRotated()));
        Properties markLayersProperties = MarkLayerListPropertiesConverter.toProperties(stopMark.getLayers(), images, prefix);
        properties.putAll(markLayersProperties);

        return properties;
    }
}
