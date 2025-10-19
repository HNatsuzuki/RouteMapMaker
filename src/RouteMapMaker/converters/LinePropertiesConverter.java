package RouteMapMaker.converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.models.LineDash;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.TextLocation;
import RouteMapMaker.models.Train;
import javafx.scene.paint.Color;

/**
 * Line と Properties の相互変換を行うクラスです。
 */
public class LinePropertiesConverter extends PropertiesConverterBase {
    /**
     * Line を Properties に変換します。
     *
     * @param line 路線
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(Line line, List<LineDash> lineDashes, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "lineName", line.getName());
        properties.setProperty(prefix + "nameLocation", String.valueOf(line.getNameLocation()));
        properties.setProperty(prefix + "tategaki",String.valueOf(line.isVertical()));
        properties.setProperty(prefix + "nameStyle", String.valueOf(line.getNameStyle()));
        properties.setProperty(prefix + "nameSize", String.valueOf(line.getNameSize()));
        properties.setProperty(prefix + "nameColorR", String.valueOf(line.getNameColor().getRed()));
        properties.setProperty(prefix + "nameColorG", String.valueOf(line.getNameColor().getGreen()));
        properties.setProperty(prefix + "nameColorB", String.valueOf(line.getNameColor().getBlue()));
        properties.setProperty(prefix + "nameColorO", String.valueOf(line.getNameColor().getOpacity()));
        properties.setProperty(prefix + "nameX", String.valueOf(line.getNameOffset().getX()));
        properties.setProperty(prefix + "nameY", String.valueOf(line.getNameOffset().getY()));

        Properties stationsProperties = StationListPropertiesConverter.toProperties(line.getStations(), line, prefix);
        properties.putAll(stationsProperties);

        Properties trainsProperties = TrainListPropertiesConverter.toProperties(line.getTrains(), lineDashes, customMarks, prefix);
        properties.putAll(trainsProperties);

        return properties;
    }

    /**
     * Properties から Line に変換します。
     *
     * @param properties Properties
     * @param version セーブデータのバージョン
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return Line
     */
    public Line fromProperties(Properties properties, double version, List<LineDash> lineDashes, List<StopMark> customMarks, String prefix) {
        errorMessages.clear();

        Line line = new Line(properties.getProperty(prefix + "lineName"));

        double r = Double.parseDouble(properties.getProperty(prefix + "nameColorR"));
        double g = Double.parseDouble(properties.getProperty(prefix + "nameColorG"));
        double b = Double.parseDouble(properties.getProperty(prefix + "nameColorB"));
        double a = Double.parseDouble(properties.getProperty(prefix + "nameColorO"));
        line.setNameColor(new Color(r, g, b, a));
        line.setNameX(Integer.parseInt(properties.getProperty(prefix + "nameX")));
        line.setNameY(Integer.parseInt(properties.getProperty(prefix + "nameY")));
        line.setNameStyle(Integer.valueOf(properties.getProperty(prefix + "nameStyle")));
        line.setNameSize(Integer.valueOf(properties.getProperty(prefix + "nameSize")));

        if (version < 7) {
            //上付き、下付きなど未対応のデータ
            boolean vertical = Boolean.valueOf(properties.getProperty(prefix + "tategaki"));
            line.setNameLocation(vertical ? TextLocation.BOTTOM : TextLocation.RIGHT);
            line.setVertical(vertical);
        } else if (version < 9) {
            //縦・横とtopやbottomが分離されていないデータ
            line.setNameLocation(TextLocation.fromLineLocation(Integer.valueOf(properties.getProperty(prefix + "nameLocation"))));
            line.setVertical(line.getNameLocation() == TextLocation.TOP || line.getNameLocation() == TextLocation.BOTTOM);
        } else {
            line.setNameLocation(TextLocation.fromLineLocation(Integer.valueOf(properties.getProperty(prefix + "nameLocation"))));
            line.setVertical(Boolean.valueOf(properties.getProperty(prefix + "tategaki")));
        }

        int numOfSta = Integer.parseInt(properties.getProperty(prefix + "NumOfStations"));
        for (int i = 0; i < numOfSta; ++i) {
            //Stationの読み込み
            String indexedStationPrefix = prefix + "sta" + String.valueOf(i) + ".";
            Station station = StationPropertiesConverter.fromProperties(properties, version, indexedStationPrefix);
            line.addStation(station, Boolean.valueOf(properties.getProperty(indexedStationPrefix + "curve")));
        }

        List<Station> stations = line.getStations();
        TrainListPropertiesConverter trainListPropertiesConverter = new TrainListPropertiesConverter();
        List<Train> trains = trainListPropertiesConverter.fromProperties(properties, lineDashes, customMarks, stations, prefix);
        line.getTrains().addAll(trains);

        if (trainListPropertiesConverter.hasError()) {
            errorMessages.addAll(trainListPropertiesConverter.getErrorMessages());
        }

        return line;
    }
}
