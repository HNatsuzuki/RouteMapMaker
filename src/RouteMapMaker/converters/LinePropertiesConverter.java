package RouteMapMaker.converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.models.LineDash;
import RouteMapMaker.models.FontStyle;
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
        properties.setProperty(prefix + "nameLocation", String.valueOf(textLocationToInt(line.getNameLocation())));
        properties.setProperty(prefix + "tategaki",String.valueOf(line.isVertical()));
        properties.setProperty(prefix + "nameStyle", String.valueOf(fontStyleToInt(line.getFontStyle())));
        properties.setProperty(prefix + "nameSize", String.valueOf(line.getNameSize()));
        properties.setProperty(prefix + "nameColorR", String.valueOf(line.getNameColor().getRed()));
        properties.setProperty(prefix + "nameColorG", String.valueOf(line.getNameColor().getGreen()));
        properties.setProperty(prefix + "nameColorB", String.valueOf(line.getNameColor().getBlue()));
        properties.setProperty(prefix + "nameColorO", String.valueOf(line.getNameColor().getOpacity()));
        properties.setProperty(prefix + "nameX", String.valueOf((int)line.getNameOffset().getX()));
        properties.setProperty(prefix + "nameY", String.valueOf((int)line.getNameOffset().getY()));

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
        line.setFontStyle(intToFontStyle(Integer.valueOf(properties.getProperty(prefix + "nameStyle"))));
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

    /**
     * {@link TextLocation} から保存用の値に変換します。
     *
     * @param textLocation テキスト位置
     * @return 対応する値
     */
    private static int textLocationToInt(TextLocation textLocation) {
        switch (textLocation) {
            case CENTER:
                return Line.CENTER;
            case TOP:
                return Line.TOP;
            case RIGHT:
                return Line.RIGHT;
            case BOTTOM:
                return Line.BOTTOM;
            case LEFT:
                return Line.LEFT;
            default:
                throw new IllegalArgumentException("不正な引数です:" + textLocation);
        }
    }

    /**
     * {@link FontStyle} から保存用の値に変換します。
     *
     * @param fontStyle フォントスタイル
     * @return 対応する値
     */
    private static int fontStyleToInt(FontStyle fontStyle) {
        switch (fontStyle) {
            case REGULAR:
                return Line.REGULAR;
            case BOLD:
                return Line.BOLD;
            case ITALIC:
                return Line.ITALIC;
            case BOLD_ITALIC:
                return Line.ITALIC_BOLD;
            default:
                throw new IllegalArgumentException("不正な引数です:" + fontStyle);
        }
    }

    /**
     * 保存用の値から {@link FontStyle} に変換します。
     *
     * @param style 保存用の値
     * @return 対応する値
     */
    private static FontStyle intToFontStyle(int style) {
        switch (style) {
            case Line.REGULAR:
                return FontStyle.REGULAR;
            case Line.BOLD:
                return FontStyle.BOLD;
            case Line.ITALIC:
                return FontStyle.ITALIC;
            case Line.ITALIC_BOLD:
                return FontStyle.BOLD_ITALIC;
            default:
                throw new IllegalArgumentException("不正なフォントスタイルです: " + style);
        }
    }
}
