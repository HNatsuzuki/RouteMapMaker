package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.Line;
import RouteMapMaker.StopMark;

/**
 * Line と Properties の相互変換を行うクラスです。
 */
public class LinePropertiesConverter {
    /**
     * Line を Properties に変換します。
     *
     * @param line 路線
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(Line line, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "lineName", line.getName());
        properties.setProperty(prefix + "nameLocation", String.valueOf(line.getNameLocation()));
        properties.setProperty(prefix + "tategaki",String.valueOf(line.isTategaki()));
        properties.setProperty(prefix + "nameStyle", String.valueOf(line.getNameStyle()));
        properties.setProperty(prefix + "nameSize", String.valueOf(line.getNameSize()));
        properties.setProperty(prefix + "nameColorR", String.valueOf(line.getNameColor().getRed()));
        properties.setProperty(prefix + "nameColorG", String.valueOf(line.getNameColor().getGreen()));
        properties.setProperty(prefix + "nameColorB", String.valueOf(line.getNameColor().getBlue()));
        properties.setProperty(prefix + "nameColorO", String.valueOf(line.getNameColor().getOpacity()));
        properties.setProperty(prefix + "nameX", String.valueOf(line.getNameZure()[0]));
        properties.setProperty(prefix + "nameY", String.valueOf(line.getNameZure()[1]));

        Properties stationsProperties = StationListPropertiesConverter.toProperties(line.getStations(), line, prefix);
        properties.putAll(stationsProperties);

        Properties trainsProperties = TrainListPropertiesConverter.toProperties(line.getTrains(), lineDashes, customMarks, prefix);
        properties.putAll(trainsProperties);

        return properties;
    }
}
