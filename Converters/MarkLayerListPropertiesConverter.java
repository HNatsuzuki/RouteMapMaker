package RouteMapMaker.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import RouteMapMaker.MarkLayer;
import javafx.scene.image.Image;

/**
 * MarkLayer のリストと Properties の相互変換を行うクラスです。
 */
public class MarkLayerListPropertiesConverter extends PropertiesConverterBase {
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
    /**
     * Properties から MarkLayer のリストに変換します。
     *
     * @param properties Properties
     * @param images 入力画像リスト
     * @param prefix 接頭辞
     * @return MarkLayer のリスト
     */
    public List<MarkLayer> fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        MarkLayerPropertiesConverter markLayerPropertiesConverter = new MarkLayerPropertiesConverter();
        int numOfLayers = Integer.valueOf(properties.getProperty(prefix + "NumOfLayers"));
        List<MarkLayer> markLayers = new ArrayList<>(numOfLayers);

        for (int i = 0; i < numOfLayers; ++i) {
            String indexedLayerPrefix = prefix + "layer" + i + ".";
            MarkLayer markLayer = markLayerPropertiesConverter.fromProperties(properties, images, indexedLayerPrefix);

            if (markLayerPropertiesConverter.hasError()) {
                String messagePrefix = "レイヤー" + i + "は";
                List<String> messages = markLayerPropertiesConverter
                    .getErrorMessages()
                    .stream()
                    .map(s -> messagePrefix + s)
                    .collect(Collectors.toList());
                errorMessages.addAll(messages);
            }

            markLayers.add(markLayer);
        }

        return markLayers;
    }
}
