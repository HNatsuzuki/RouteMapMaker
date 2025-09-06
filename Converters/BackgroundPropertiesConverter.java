package RouteMapMaker.Converters;

import java.util.Map;
import java.util.Properties;

import RouteMapMaker.Background;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Background と Properties の相互変換を行うクラスです。
 */
public class BackgroundPropertiesConverter extends PropertiesConverterBase {
    /**
     * Background を Properties に変換します。
     *
     * @param background 背景情報
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(Background background, Map<Integer, Image> images, String prefix) {
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
            images.put(images.size(), background.getImage());
        }

        return properties;
    }

    /**
     * Properties を Background に 変換します。
     *
     * @param properties Properties
     * @param images 入力画像リスト
     * @param prefix 接頭辞
     */
    public Background fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        Background background = new Background();

        double r = Double.parseDouble(properties.getProperty(prefix + "bgColorR"));
        double g = Double.parseDouble(properties.getProperty(prefix + "bgColorG"));
        double b = Double.parseDouble(properties.getProperty(prefix + "bgColorB"));
        double a = Double.parseDouble(properties.getProperty(prefix + "bgColorO"));
        background.setColor(new Color(r, g, b, a));

        // v15より前はbgImageXなどが存在しないので分岐
        if (properties.getProperty(prefix + "bgImageX") != null) {
            background.setX(Integer.parseInt(properties.getProperty(prefix + "bgImageX")));
            background.setY(Integer.parseInt(properties.getProperty(prefix + "bgImageY")));
            background.setZoomRatio(Integer.parseInt(properties.getProperty(prefix + "bgImageZoomRatio")));
            background.setOpacity(Integer.parseInt(properties.getProperty(prefix + "bgImageOpacity")));
        }

        String bgImage = properties.getProperty(prefix + "bgImage");

        if (bgImage != null) {
            Image image = images.get(Integer.parseInt(bgImage));

            if (image == null) {
                errorMessages.add("画像ファイル (" + bgImage + ") が見つかりませんでした。");
            } else {
                background.setImage(image);
            }
        } else {
            background.setImage(null);
        }

        return background;
    }
}
