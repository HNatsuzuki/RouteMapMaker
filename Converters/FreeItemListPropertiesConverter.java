package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.FreeItem;
import javafx.scene.image.Image;

/**
 * FreeItem のリストと Properties の相互変換を行うクラスです。
 */
public class FreeItemListPropertiesConverter {
    /**
     * FreeItem のリストを properties に変換します。
     *
     * @param freeItems FreeItem のリスト
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<FreeItem> freeItems, List<Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "NumOfFreeItems", String.valueOf(freeItems.size()));

        for (int i = 0; i < freeItems.size(); ++i) {
            FreeItem freeItem = freeItems.get(i);
            String indexedPrefix = prefix + "FreeItem" + i + ".";
            Properties freeItemProperties = FreeItemPropertiesConverter.toProperties(freeItem, images, indexedPrefix);
            properties.putAll(freeItemProperties);
        }

        return properties;
    }
}
