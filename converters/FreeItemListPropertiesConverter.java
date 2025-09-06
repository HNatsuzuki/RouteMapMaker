package RouteMapMaker.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import RouteMapMaker.FreeItem;
import javafx.scene.image.Image;

/**
 * FreeItem のリストと Properties の相互変換を行うクラスです。
 */
public class FreeItemListPropertiesConverter extends PropertiesConverterBase {
    /**
     * FreeItem のリストを properties に変換します。
     *
     * @param freeItems FreeItem のリスト
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(List<FreeItem> freeItems, Map<Integer, Image> images, String prefix) {
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

    /**
     * Properties から FreeItem のリストに変換します。
     *
     * @param properties Properties
     * @param images 入力画像リスト
     * @param prefix 接頭辞
     * @return FreeItem のリスト
     */
    public List<FreeItem> fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        List<FreeItem> freeItems = new ArrayList<>();
        FreeItemPropertiesConverter freeItemPropertiesConverter = new FreeItemPropertiesConverter();
        int numOfItems = Integer.valueOf(properties.getProperty("NumOfFreeItems"));

        for (int i = 0; i < numOfItems; ++i) {
            String indexedPrefix = prefix + "FreeItem" + i + ".";
            FreeItem freeItem = freeItemPropertiesConverter.fromProperties(properties, images, indexedPrefix);

            if (freeItemPropertiesConverter.hasError()) {
                errorMessages.addAll(freeItemPropertiesConverter.getErrorMessages());
            } else {
                freeItems.add(freeItem);
            }
        }

        return freeItems;
    }
}
