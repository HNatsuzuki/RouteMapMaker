package RouteMapMaker.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.Line;
import RouteMapMaker.StopMark;

/**
 * Line のリストと Properties の相互変換を行うクラスです。
 */
public class LineListPropertiesConverter extends PropertiesConverterBase {
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

    /**
     * Properties から Line のリストに変換します。
     *
     * @param properties Properties
     * @param version セーブデータのバージョン
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return Line のリスト
     */
    public List<Line> fromProperties(Properties properties, double version, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, String prefix) {
        errorMessages.clear();

        List<Line> lines = new ArrayList<>();
        LinePropertiesConverter linePropertiesConverter = new LinePropertiesConverter();

        int numOfLines = Integer.parseInt(properties.getProperty("NumOfLines"));

        for (int i = 0; i < numOfLines; ++i) {
            //lineの読み込み
            System.out.println("Reading:line" + i);
            String indexedPrefix = "line" + String.valueOf(i) + ".";

            Line line = linePropertiesConverter.fromProperties(properties, version, lineDashes, customMarks, indexedPrefix);
            lines.add(line);

            if (linePropertiesConverter.hasError()) {
                String messagePrefix = "line" + i;
                List<String> messages = linePropertiesConverter
                    .getErrorMessages()
                    .stream()
                    .map(s -> messagePrefix + s)
                    .collect(Collectors.toList());
                errorMessages.addAll(messages);
            }
        }

        return lines;
    }
}
