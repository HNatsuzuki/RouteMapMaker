package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.MarkLayer;
import javafx.scene.image.Image;

/**
 * MarkLayer のリストと Properties の相互変換を行うクラスです。
 */
public class MarkLayerListPropertiesConverter {
    /**
     * MarkLayer のリストを Properties に変換します。
     *
     * @param markLayers MarkLayer のリスト
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<MarkLayer> markLayers, List<Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfLayers", String.valueOf(markLayers.size()));

        for (int i = 0; i < markLayers.size(); ++i) {
            MarkLayer l = markLayers.get(i);
            String indexedPrefix = prefix + "layer" + i + ".";
            Properties markLayerProperties = MarkLayerPropertiesConverter.toProperties(l, images, indexedPrefix);
            properties.putAll(markLayerProperties);
        }

            return properties;
    }
}
