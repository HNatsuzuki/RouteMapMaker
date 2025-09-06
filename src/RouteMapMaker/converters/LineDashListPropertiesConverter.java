package RouteMapMaker.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import RouteMapMaker.models.DoubleArrayWrapper;

/**
 * LineDash (DoubleArrayWrapper) のリストと Properties の相互変換を行うクラスです。
 */
public class LineDashListPropertiesConverter {
    /**
     * LineDash (DoubleArrayWrapper) のリストから Properties に変換します。
     *
     * @param lineDashes LineDash のリスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<DoubleArrayWrapper> lineDashes, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfLineDashes", String.valueOf(lineDashes.size()));

        // index0は直線=nullなので保存しません
        for (int i = 1; i < lineDashes.size(); ++i) {
            String indexedPrefix = prefix + "LineDash" + i;
            double[] lineDash = lineDashes.get(i).get();
            properties.setProperty(indexedPrefix + "length", String.valueOf(lineDash.length));

            for (int j = 0; j < lineDash.length; ++j) {
                properties.setProperty(indexedPrefix + "." + j, String.valueOf(lineDash[j]));
            }
        }

        return properties;
    }

    /**
     * Properties から LineDash のリストに変換します。
     *
     * @param properties Properties
     * @param prefix 接頭辞
     * @return LineDash のリスト
     */
    public static List<DoubleArrayWrapper> fromProperties(Properties properties, String prefix) {
        List<DoubleArrayWrapper> lineDashes = new ArrayList<DoubleArrayWrapper>();
        int numOfLineDashes = Integer.valueOf(properties.getProperty(prefix + "NumOfLineDashes"));

        //iは1から。（0はNORMAL_LINE）
        for (int i = 1; i < numOfLineDashes; ++i) {
            String indexedPrefix = prefix + "LineDash" + i;
            int length = Integer.valueOf(properties.getProperty(indexedPrefix + "length"));
            double[] da = new double[length];

            for (int j = 0; j < length; ++j) {
                da[j] = Double.valueOf(properties.getProperty(indexedPrefix + "." + j));
            }

            lineDashes.add(new DoubleArrayWrapper(da));
        }

        return lineDashes;
    }
}
