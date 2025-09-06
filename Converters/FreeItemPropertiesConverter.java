package RouteMapMaker.Converters;

import java.util.Map;
import java.util.Properties;

import RouteMapMaker.FreeItem;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * FreeItem と Properties の相互変換を行うクラスです。
 */
public class FreeItemPropertiesConverter extends PropertiesConverterBase {
    /**
     * FreeItem を properties に変換します。
     *
     * @param freeItem FreeItem
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(FreeItem freeItem, Map<Integer, Image> images, String prefix) {
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
                images.put(images.size(), freeItem.getImage());
                break;
        }

        return properties;
    }

    /**
     * Properties から FreeItem に変換します。
     *
     * @param properties Properties
     * @param images 入力画像リスト
     * @param prefix 接頭辞
     * @return FreeItem
     */
    public FreeItem fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        int type = Integer.valueOf(properties.getProperty(prefix + "type"));

        switch (type) {
            case FreeItem.TEXT:
                {
                    FreeItem freeItem = new FreeItem(type);
                    freeItem.setText(properties.getProperty(prefix + "text"));

                    for (int i = 0; i < 8; ++i) {
                        freeItem.getParams()[i].set(Double.parseDouble(properties.getProperty(prefix + "params" + i)));
                    }

                    double r = Double.parseDouble(properties.getProperty(prefix + "ColorR"));
                    double g = Double.parseDouble(properties.getProperty(prefix + "ColorG"));
                    double b = Double.parseDouble(properties.getProperty(prefix + "ColorB"));
                    double a = Double.parseDouble(properties.getProperty(prefix + "ColorO"));
                    freeItem.setColor(new Color(r, g, b, a));
                    freeItem.setFontName(properties.getProperty(prefix + "font"));

                    return freeItem;
                }
            case FreeItem.IMAGE:
                int imageIndex = Integer.valueOf(properties.getProperty(prefix + "image"));
                Image image = images.get(imageIndex);

                if (image == null) {
                    errorMessages.add("画像ファイル " + imageIndex + ".png が見つかりません．");

                    return null;
                } else {
                    FreeItem freeItem = new FreeItem(type);
                    freeItem.setText(properties.getProperty(prefix + "text"));

                    for (int i = 0; i < 5; ++i) {
                        freeItem.getParams()[i].set(Double.parseDouble(properties.getProperty(prefix + "params" + i)));
                    }

                    freeItem.setImage(image);

                    return freeItem;
                }
            default:
                errorMessages.add("不正なタイプです: " + type);
                return null;
        }
    }
}
