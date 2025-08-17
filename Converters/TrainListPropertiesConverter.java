package RouteMapMaker.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.Station;
import RouteMapMaker.StopMark;
import RouteMapMaker.Train;

/**
 * Train のリストと Properties の相互変換を行うクラスです。
 */
public class TrainListPropertiesConverter extends PropertiesConverterBase {
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

    /**
     * Properties から Train のリストに変換します。
     *
     * @param properties Properties
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param stations 路線の駅リスト
     * @param prefix 接頭辞
     * @return Train のリスト
     */
    public List<Train> fromProperties(Properties properties, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, List<Station> stations, String prefix) {
        errorMessages.clear();

        TrainPropertiesConverter trainPropertiesConverter = new TrainPropertiesConverter();
        int numOfTrains = Integer.parseInt(properties.getProperty(prefix + "NumOfTrains"));
        List<Train> trains = new ArrayList<>(numOfTrains);

        for (int i = 0; i < numOfTrains; ++i) {
            System.out.println("\ttrain" + i);

            String indexedTrainPrefix = prefix + "train" + String.valueOf(i) + ".";
            Train train = trainPropertiesConverter.fromProperties(properties, lineDashes, customMarks, stations, indexedTrainPrefix);
            trains.add(train);

            if (trainPropertiesConverter.hasError()) {
                String messagePrefix = "train" + i + "で";
                List<String> messages = trainPropertiesConverter
                    .getErrorMessages()
                    .stream()
                    .map(s -> messagePrefix + s)
                    .collect(Collectors.toList());
                errorMessages.addAll(messages);
            }
        }

        return trains;
    }
}
