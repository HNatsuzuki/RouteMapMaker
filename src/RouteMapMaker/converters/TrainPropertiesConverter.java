package RouteMapMaker.converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.models.LineDash;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.Train;
import RouteMapMaker.models.TrainStop;

/**
 * Train と Properties の相互変換を行うクラスです。
 */
public class TrainPropertiesConverter extends PropertiesConverterBase {
    /**
     * Train を Properties に変換します。
     *
     * @param train Train
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return properties
     */
    public static Properties toProperties(Train train, List<LineDash> lineDashes, List<StopMark> customMarks, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "name", train.getName());

        Properties trainStopsProperties = TrainStopListPropertiesConverter.toProperties(train.getStops(), customMarks, prefix);
        properties.putAll(trainStopsProperties);

        double[][] cc = train.getColorsInDouble();//色の記録を行う
        for (int i = 0; i <= 3; ++i) {
            //[手動][i]
            properties.setProperty(prefix + "Color0" + String.valueOf(i), String.valueOf(cc[0][i]));
            properties.setProperty(prefix + "Color1" + String.valueOf(i), String.valueOf(cc[1][i]));
            properties.setProperty(prefix + "Color2" + String.valueOf(i), String.valueOf(cc[2][i]));
        }

        properties.setProperty(prefix + "lineWidth", String.valueOf(train.getLineWidth()));
        properties.setProperty(prefix + "lineDistance", String.valueOf(train.getLineDistance()));

        //マークの保存
        if (train.getMark() == StopMark.CIRCLE) {
            properties.setProperty(prefix + "mark", "CIRCLE");
        } else if (train.getMark() == StopMark.NO_DRAW) {
            properties.setProperty(prefix + "mark", "NO_DRAW");
        } else {//カスタムマークの場合は番号を記録
            properties.setProperty(prefix + "mark", String.valueOf(customMarks.indexOf(train.getMark())));
        }

        properties.setProperty(prefix + "markSize", String.valueOf(train.getMarkSize()));
        properties.setProperty(prefix + "staSize", String.valueOf(train.getStaSize()));
        properties.setProperty(prefix + "tategaki", String.valueOf(train.isTategaki()));//この属性本当に使ってるのか謎
        properties.setProperty(prefix + "edgeFixA", String.valueOf(train.getEdgeA()));
        properties.setProperty(prefix + "edgeFixB", String.valueOf(train.getEdgeB()));
        properties.setProperty(prefix + "lineDash", String.valueOf(lineDashes.indexOf(train.getLineDash())));//lineDashは番号で。

        return properties;
    }

    /**
     * Properties から Train に変換します。
     *
     * @param properties Properties
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param stations 路線の駅リスト
     * @param prefix 接頭辞
     * @return Train
     */
    public Train fromProperties(Properties properties, List<LineDash> lineDashes, List<StopMark> customMarks, List<Station> stations, String prefix) {
        errorMessages.clear();

        Train train = new Train(properties.getProperty(prefix + "name"));

        TrainStopListPropertiesConverter trainStopListPropertiesConverter = new TrainStopListPropertiesConverter();
        List<TrainStop> trainStops = trainStopListPropertiesConverter.fromProperties(properties, customMarks, stations, prefix);
        train.getStops().addAll(trainStops);

        if (trainStopListPropertiesConverter.hasError()) {
            errorMessages.addAll(trainStopListPropertiesConverter.getErrorMessages());
        }

        double[][] dd = new double[3][4];
        for (int i = 0; i <= 3; ++i){
            dd[0][i] = Double.parseDouble(properties.getProperty(prefix + "Color0" + String.valueOf(i)));
            dd[1][i] = Double.parseDouble(properties.getProperty(prefix + "Color1" + String.valueOf(i)));
            dd[2][i] = Double.parseDouble(properties.getProperty(prefix + "Color2" + String.valueOf(i)));
        }

        train.setColorsInDouble(dd);
        train.setLineWidth(Integer.parseInt(properties.getProperty(prefix + "lineWidth")));
        train.setLineDistance(Integer.parseInt(properties.getProperty(prefix + "lineDistance")));

        //マークの読み込み
        String markString = properties.getProperty(prefix + "mark");

        if (markString == null || markString.equals("CIRCLE")) {
            train.setMark(StopMark.CIRCLE);
        } else if(markString.equals("NO_DRAW")) {
            train.setMark(StopMark.NO_DRAW);
        } else {
            //カスタムマークの時は番号から読み込む
            train.setMark(customMarks.get(Integer.valueOf(markString)));
        }

        train.setMarkSize(Integer.parseInt(properties.getProperty(prefix + "markSize")));
        train.setStaSize(Integer.parseInt(properties.getProperty(prefix + "staSize")));
        //コレ使ってるのか謎
        train.setTategaki(Boolean.valueOf(properties.getProperty(prefix + "tategaki")));
        train.setEdgeA(Integer.parseInt(properties.getProperty(prefix + "edgeFixA")));
        train.setEdgeB(Integer.parseInt(properties.getProperty(prefix + "edgeFixB")));
        train.setLineDash(lineDashes.get(Integer.parseInt(properties.getProperty(prefix + "lineDash","0"))));

        return train;
    }
}
