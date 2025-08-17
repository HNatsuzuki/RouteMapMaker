package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.DoubleArrayWrapper;
import RouteMapMaker.StopMark;
import RouteMapMaker.Train;

/**
 * Train と Properties の相互変換を行うクラスです。
 */
public class TrainPropertiesConverter {
    /**
     * Train を Properties に変換します。
     *
     * @param train Train
     * @param lineDashes 点線定義リスト
     * @param customMarks 停車駅マークリスト
     * @param prefix 接頭辞
     * @return properties
     */
    public static Properties toProperties(Train train, List<DoubleArrayWrapper> lineDashes, List<StopMark> customMarks, String prefix) {
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
}
