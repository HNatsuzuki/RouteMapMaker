package RouteMapMaker.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import RouteMapMaker.models.Station;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.TrainStop;

/**
 * TrainStop のリストと Properties の相互変換を行うクラスです。
 */
public class TrainStopListPropertiesConverter extends PropertiesConverterBase {
    public static Properties toProperties(List<TrainStop> trainStops, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfStations", String.valueOf(trainStops.size()));

        for (int i = 0; i < trainStops.size(); ++i) {//stationsは駅名のみ記録する。停車駅ごとのループ
            TrainStop trainStop = trainStops.get(i);
            properties.setProperty(prefix + "sta" + String.valueOf(i), trainStop.getSta().getName());
            properties.setProperty(prefix + "sta" + String.valueOf(i) + ".shiftX", String.valueOf(trainStop.getShift().getX()));
            properties.setProperty(prefix + "sta" + String.valueOf(i) + ".shiftY", String.valueOf(trainStop.getShift().getY()));

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

    /**
     * Properties から TrainStop のリストに変換します。
     *
     * @param properties Properties
     * @param customMarks カスタムマークリスト
     * @param stations 駅リスト
     * @param prefix 接頭辞
     * @return TrainStop
     */
    public List<TrainStop> fromProperties(Properties properties, List<StopMark> customMarks, List<Station> stations, String prefix) {
        errorMessages.clear();
        int count = 0;
        List<TrainStop> trainStops = new ArrayList<>();

        for (int k = 0; k < stations.size(); k++){//路線の駅から停車駅を追加していく。
            String a = stations.get(k).getName();
            String b = properties.getProperty(prefix + "sta" + String.valueOf(count));

            if (a.equals(b)) {
                TrainStop trainStop = new TrainStop(stations.get(k));
                //記載がなかったら0を代入。
                trainStop.setShiftX(Integer.valueOf(properties.getProperty(prefix + "sta" + String.valueOf(count) + ".shiftX", "0")));
                trainStop.setShiftY(Integer.valueOf(properties.getProperty(prefix + "sta" + String.valueOf(count) + ".shiftY", "0")));

                //停車駅マークについて
                String ms = properties.getProperty (prefix + "sta" + String.valueOf(count) + ".mark");

                if (ms == null) {//デフォは路線準拠
                    trainStop.setMark(StopMark.OBEY_LINE);
                } else if (ms.equals("OBEY_LINE")){
                    trainStop.setMark(StopMark.OBEY_LINE);
                } else if (ms.equals("NO_DRAW")){
                    trainStop.setMark(StopMark.NO_DRAW);
                } else if (ms.equals("CIRCLE")){
                    trainStop.setMark(StopMark.CIRCLE);
                } else {//カスタムマークの場合は番号で。
                    trainStop.setMark(customMarks.get(Integer.valueOf(ms)));
                }

                trainStops.add(trainStop);
                count++;
            }
        }

        String numOfStations = properties.getProperty(prefix + "NumOfStations");

        if (count != Integer.parseInt(numOfStations)) {
            errorMessages.add("指定された駅数 (" + numOfStations + ") が停車駅として追加されていません。");
        }

        return trainStops;
    }
}
