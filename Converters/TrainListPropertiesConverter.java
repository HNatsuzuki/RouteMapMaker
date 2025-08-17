package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.StopMark;
import RouteMapMaker.Train;

/**
 * Train のリストと Properties の相互変換を行うクラスです。
 */
public class TrainListPropertiesConverter {
    /**
     * Train のリストを Properties に変換します。
     *
     * @param trains Train のリスト
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return properties
     */
    public static Properties toProperties(List<Train> trains, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfTrains", String.valueOf(trains.size()));

        for (int i = 0; i < trains.size(); ++i) {
            Train train = trains.get(i);
            String indexedPrefix = prefix + "train" + String.valueOf(i) + ".";
            Properties trainProperties = TrainPropertiesConverter.toProperties(train, lineDashes, customMarks, indexedPrefix);
            properties.putAll(trainProperties);
        }

        return properties;
    }
}
