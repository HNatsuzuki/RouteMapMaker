package RouteMapMaker.converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.Line;
import RouteMapMaker.Station;

/**
 * Station のリストと Properties の相互変換を行うクラスです。
 */
public class StationListPropertiesConverter {
    /**
     * Station のリストを Properties に変換します。
     *
     * @param stations 駅リスト
     * @param line 路線 (曲線接続の保存に使用)
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<Station> stations, Line line, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfStations", String.valueOf(stations.size()));

        for (int i = 0; i < stations.size(); ++i) {
            String indexedPrefix = prefix + "sta" + i + ".";
            Properties p = StationPropertiesConverter.toProperties(stations.get(i), line.getCurveConnection(i), indexedPrefix);
            properties.putAll(p);
        }

        return properties;
    }
}
