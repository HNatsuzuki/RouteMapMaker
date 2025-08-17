package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.StopMark;
import javafx.scene.image.Image;

/**
 * StopMark のリストと Properties の相互変換を行うクラスです。
 */
public class StopMarkListPropertiesConverter {
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
}
