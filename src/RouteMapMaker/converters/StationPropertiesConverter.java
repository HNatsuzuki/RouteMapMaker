package RouteMapMaker.converters;

import java.util.Properties;

import RouteMapMaker.models.Station;

/**
 * Station と Properties の相互変換を行うクラスです。
 */
public class StationPropertiesConverter {
    /**
     * Station を Properties に変換します。
     *
     * @param station 駅データ
     * @param curve 曲線接続データ (保存上 Station の子にいる構造)
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(Station station, boolean curve, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "name", station.getName());
        properties.setProperty(prefix + "pointSet", String.valueOf(station.isSet()));
        if (station.isSet()) {
            properties.setProperty(prefix + "x", String.valueOf(station.getPoint().getX()));
            properties.setProperty(prefix + "y", String.valueOf(station.getPoint().getY()));
        } else {
            properties.setProperty(prefix + "x", String.valueOf(station.getInterPoint().getX()));
            properties.setProperty(prefix + "y", String.valueOf(station.getInterPoint().getY()));
        }
        properties.setProperty(prefix + "stationConnection", String.valueOf(station.getConnection()));
        properties.setProperty(prefix + "textLocation", String.valueOf(station.getTextLocation()));
        properties.setProperty(prefix + "tategaki", String.valueOf(station.isTategaki()));
        properties.setProperty(prefix + "size", String.valueOf(station.getNameSize()));
        properties.setProperty(prefix + "style", String.valueOf(station.getNameStyle()));
        properties.setProperty(prefix + "nameX", String.valueOf((int)station.getNameOffset().getX()));
        properties.setProperty(prefix + "nameY", String.valueOf((int)station.getNameOffset().getY()));
        properties.setProperty(prefix + "shiftOnStation", String.valueOf(station.shiftBasedOnStation()));
        properties.setProperty(prefix + "curve", String.valueOf(curve));

        return properties;
    }

    /**
     * Properties から Station に変換します。
     *
     * @param properties Properties
     * @param version セーブデータのバージョン
     * @param prefix 接頭辞
     * @return Station
     */
    public static Station fromProperties(Properties properties, double version, String prefix) {
        Station station = new Station(properties.getProperty(prefix + "name"));
        double rx = Double.parseDouble(properties.getProperty(prefix + "x"));
        double ry = Double.parseDouble(properties.getProperty(prefix + "y"));

        if (Boolean.valueOf(properties.getProperty(prefix + "pointSet"))) {
            //pointSetがtrueのとき
            station.setPoint(rx, ry);
        } else {
            //falseのとき
            station.setInterPoint(rx, ry);
        }

        station.setConnection(Integer.parseInt(properties.getProperty(prefix + "stationConnection")));

        if (version < 9) {
            int loc = Integer.parseInt(properties.getProperty(prefix + "textMuki"));
            station.setTextLocation(loc);
            station.setTategaki(loc==Station.TEXT_BOTTOM || loc==Station.TEXT_TOP);
        } else {
            station.setTextLocation(Integer.parseInt(properties.getProperty(prefix + "textLocation")));
            station.setTategaki(Boolean.valueOf(properties.getProperty(prefix + "tategaki")));
        }

        station.setNameSize(Integer.parseInt(properties.getProperty(prefix + "size")));
        station.setNameStyle(Integer.parseInt(properties.getProperty(prefix + "style")));
        station.setNameX(Integer.parseInt(properties.getProperty(prefix + "nameX")));
        station.setNameY(Integer.parseInt(properties.getProperty(prefix + "nameY")));
        station.setShiftBase(Boolean.valueOf(properties.getProperty(prefix + "shiftOnStation")));

        return station;
    }
}
