package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.StopMark;
import RouteMapMaker.TrainStop;

/**
 * TrainStop のリストと Properties の相互変換を行うクラスです。
 */
public class TrainStopListPropertiesConverter {
    public static Properties toProperties(List<TrainStop> trainStops, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfStations", String.valueOf(trainStops.size()));

        for (int i = 0; i < trainStops.size(); ++i) {//stationsは駅名のみ記録する。停車駅ごとのループ
            TrainStop trainStop = trainStops.get(i);
            properties.setProperty(prefix + "sta" + String.valueOf(i), trainStop.getSta().getName());
            properties.setProperty(prefix + "sta" + String.valueOf(i) + ".shiftX", String.valueOf(trainStop.getShift()[0]));
            properties.setProperty(prefix + "sta" + String.valueOf(i) + ".shiftY", String.valueOf(trainStop.getShift()[1]));

            //停車駅マークタイプに関する記述。カスタムマーク対応したらここも追記必要あり。
            StopMark sm = trainStop.getMark();
            if (sm == StopMark.OBEY_LINE) {
                properties.setProperty(prefix + "sta" + String.valueOf(i) + ".mark", "OBEY_LINE");
            } else if (sm == StopMark.NO_DRAW) {
                properties.setProperty(prefix + "sta" + String.valueOf(i) + ".mark", "NO_DRAW");
            } else if (sm == StopMark.CIRCLE) {
                properties.setProperty(prefix + "sta" + String.valueOf(i) + ".mark", "CIRCLE");
            } else {//カスタムマークの場合はcustomMarksの番号を記述する。
                properties.setProperty(prefix + "sta" + String.valueOf(i) + ".mark", String.valueOf(customMarks.indexOf(sm)));
            }
        }

        return properties;
    }
}
