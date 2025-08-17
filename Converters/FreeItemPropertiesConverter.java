package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.FreeItem;
import javafx.scene.image.Image;

/**
 * FreeItem と Properties の相互変換を行うクラスです。
 */
public class FreeItemPropertiesConverter {
    /**
     * FreeItem を properties に変換します。
     *
     * @param freeItem FreeItem
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(FreeItem freeItem, List<Image> images, String prefix) {
        Properties properties = new Properties();
        int freeItemType = freeItem.getType();
        properties.setProperty(prefix + "type", String.valueOf(freeItemType));
        properties.setProperty(prefix + "text", freeItem.getText());

        for(int i = 0; i < freeItem.getParams().length; i++){
            properties.setProperty(prefix + "params" + i, String.valueOf(freeItem.getParams()[i].getValue()));
        }

        switch (freeItemType) {
            case FreeItem.TEXT:
                properties.setProperty(prefix + "ColorR", String.valueOf(freeItem.getColor().getRed()));
                properties.setProperty(prefix + "ColorG", String.valueOf(freeItem.getColor().getGreen()));
                properties.setProperty(prefix + "ColorB", String.valueOf(freeItem.getColor().getBlue()));
                properties.setProperty(prefix + "ColorO", String.valueOf(freeItem.getColor().getOpacity()));
                properties.setProperty(prefix + "font", freeItem.getFontName());
                break;
            case FreeItem.IMAGE:
                properties.setProperty(prefix + "image", String.valueOf(images.size()));
                images.add(freeItem.getImage());
                break;
        }

        return properties;
    }
}
