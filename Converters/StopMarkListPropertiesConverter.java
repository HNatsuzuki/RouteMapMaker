package RouteMapMaker.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import RouteMapMaker.StopMark;
import javafx.scene.image.Image;

/**
 * StopMark のリストと Properties の相互変換を行うクラスです。
 */
public class StopMarkListPropertiesConverter extends PropertiesConverterBase {
    /**
     * StopMark のリストから Properties に変換します。
     *
     * @param stopMarks StopMarkのリスト
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<StopMark> stopMarks, List<Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfMarks", String.valueOf(stopMarks.size()));
        for(int i = 0; i < stopMarks.size(); i++){
            StopMark mark = stopMarks.get(i);
            String indexedPrefix = prefix + "Mark" + i + ".";
            Properties markProperties = StopMarkPropertiesConverter.toProperties(mark, images, indexedPrefix);
            properties.putAll(markProperties);
        }

        return properties;
    }

    /**
     * Properties から StopMark のリストに変換します。
     *
     * @param properties Properties
     * @param images 入力用画像リスト
     * @param prefix 接頭辞
     * @return StopMark のリスト
     */
    public List<StopMark> fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        StopMarkPropertiesConverter stopMarkPropertiesConverter = new StopMarkPropertiesConverter();
        int numOfMarks = Integer.valueOf(properties.getProperty("NumOfMarks"));
        List<StopMark> stopMarks = new ArrayList<>(numOfMarks);

        for (int i = 0; i < numOfMarks; ++i) {
            String indexedMarkPrefix = "Mark" + i + ".";
            StopMark mark = stopMarkPropertiesConverter.fromProperties(properties, images, indexedMarkPrefix);
            stopMarks.add(mark);

            if (stopMarkPropertiesConverter.hasError()) {
                String messagePrefix = "カスタムマーク" + i;
                List<String> messages = stopMarkPropertiesConverter
                    .getErrorMessages()
                    .stream()
                    .map(s -> messagePrefix + s)
                    .collect(Collectors.toList());
                errorMessages.addAll(messages);
            }
        }

        return stopMarks;
    }
}
