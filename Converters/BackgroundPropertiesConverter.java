package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.Background;
import javafx.scene.image.Image;

/**
 * Background と Properties の相互変換を行うクラスです。
 */
public class BackgroundPropertiesConverter {
    /**
     * Background を Properties に変換します。
     *
     * @param background 背景情報
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(Background background, List<Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "bgColorR", String.valueOf(background.getColor().getRed()));
        properties.setProperty(prefix + "bgColorG", String.valueOf(background.getColor().getGreen()));
        properties.setProperty(prefix + "bgColorB", String.valueOf(background.getColor().getBlue()));
        properties.setProperty(prefix + "bgColorO", String.valueOf(background.getColor().getOpacity()));
        properties.setProperty(prefix + "bgImageX", String.valueOf(background.getX()));
        properties.setProperty(prefix + "bgImageY", String.valueOf(background.getY()));
        properties.setProperty(prefix + "bgImageZoomRatio", String.valueOf(background.getZoomRatio()));
        properties.setProperty(prefix + "bgImageOpacity", String.valueOf(background.getOpacity()));

        if (background.getImage() != null) {
            properties.setProperty(prefix + "bgImage", String.valueOf(images.size()));
            images.add(background.getImage());
        }

        return properties;
    }
}
