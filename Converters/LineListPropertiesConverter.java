package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.Line;
import RouteMapMaker.StopMark;

/**
 * Line のリストと Properties の相互変換を行うクラスです。
 */
public class LineListPropertiesConverter {
    /**
     * Line のリストを Properties に変換します。
     *
     * @param lines 路線リスト
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<Line> lines, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty("NumOfLines", String.valueOf(lines.size()));

        for (int i = 0; i < lines.size(); ++i) {
            Line line = lines.get(i);
            String indexedPrefix =  prefix + "line" + i + ".";
            Properties lineProperties = LinePropertiesConverter.toProperties(line, lineDashes, customMarks, indexedPrefix);
            properties.putAll(lineProperties);
        }

        return properties;
    }
}
