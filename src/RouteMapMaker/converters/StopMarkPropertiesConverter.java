package RouteMapMaker.converters;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.StopMark;
import javafx.scene.image.Image;

/**
 * StopMark と Properties の相互変換を行うクラスです。
 */
public class StopMarkPropertiesConverter extends PropertiesConverterBase {
    /**
     * StopMark から Properties に変換します。
     *
     * @param stopMark StopMark
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(StopMark stopMark, Map<Integer, Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "isRotated", String.valueOf(stopMark.isRotated()));
        Properties markLayersProperties = MarkLayerListPropertiesConverter.toProperties(stopMark.getLayers(), images, prefix);
        properties.putAll(markLayersProperties);

        return properties;
    }

    /**
     * Properties から StopMark に変換します。
     *
     * @param properties Properties
     * @param images 入力用画像リスト
     * @param prefix 接頭辞
     * @return StopMark
     */
    public StopMark fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        StopMark stopMark = new StopMark();
        stopMark.setRotate(Boolean.valueOf(properties.getProperty(prefix + "isRotated")));

        MarkLayerListPropertiesConverter markLayerListPropertiesConverter = new MarkLayerListPropertiesConverter();
        List<MarkLayer> markLayers = markLayerListPropertiesConverter.fromProperties(properties, images, prefix);
        stopMark.getLayers().addAll(markLayers);

        if (markLayerListPropertiesConverter.hasError()) {
            errorMessages.addAll(markLayerListPropertiesConverter.getErrorMessages());
        }

        return stopMark;
    }
}
