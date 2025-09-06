package RouteMapMaker.converters;

import java.util.Map;
import java.util.Properties;

import RouteMapMaker.MarkLayer;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * MarkLayer と Properties の相互変換を行うクラスです。
 */
public class MarkLayerPropertiesConverter extends PropertiesConverterBase {
    /**
     * MarkLayer を Properties に変換します。
     *
     * @param markLayer MarkLayer
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(MarkLayer markLayer, Map<Integer, Image> images, String prefix) {
        Properties properties = new Properties();
        properties.setProperty(prefix + "type", String.valueOf(markLayer.getType()));
        properties.setProperty(prefix + "paint", String.valueOf(markLayer.getPaint()));
        properties.setProperty(prefix + "numOfParams", String.valueOf(markLayer.getParamProperty().size()));

        for (int i = 0; i < markLayer.getParamProperty().size(); ++i) {
            properties.setProperty(prefix + "param" + i, String.valueOf(markLayer.getParam(i)));
        }

        properties.setProperty(prefix + "text", String.valueOf(markLayer.getText()));
        properties.setProperty(prefix + "fontName", String.valueOf(markLayer.getFontName()));
        properties.setProperty(prefix + "colorR", String.valueOf(markLayer.getColor().getRed()));
        properties.setProperty(prefix + "colorG", String.valueOf(markLayer.getColor().getGreen()));
        properties.setProperty(prefix + "colorB", String.valueOf(markLayer.getColor().getBlue()));
        properties.setProperty(prefix + "colorO", String.valueOf(markLayer.getColor().getOpacity()));

        if (markLayer.getType() == MarkLayer.IMAGE && markLayer.getImage() != null) {
            properties.setProperty(prefix + "image", String.valueOf(images.size()));
            images.put(images.size(), markLayer.getImage());
        }

        return properties;
    }

    /**
     * Properties から MarkLayer に変換します。
     *
     * @param properties Properties
     * @param images 入力画像リスト
     * @param prefix 接頭辞
     * @return MarkLayer
     */
    public MarkLayer fromProperties(Properties properties, Map<Integer, Image> images, String prefix) {
        errorMessages.clear();
        MarkLayer layer = new MarkLayer(Integer.valueOf(properties.getProperty(prefix + "type")));
        layer.setPaint(Integer.valueOf(properties.getProperty(prefix + "paint")));
        int numOfParams = Integer.valueOf(properties.getProperty(prefix + "numOfParams"));

        for (int i = 0; i < numOfParams; ++i) {
            layer.addParam(Double.valueOf(properties.getProperty(prefix + "param" + i)));
        }

        layer.setText(properties.getProperty(prefix + "text"));
        layer.setFontName(properties.getProperty(prefix + "fontName",null));

        double r = Double.valueOf(properties.getProperty(prefix + "colorR"));
        double g = Double.valueOf(properties.getProperty(prefix + "colorG"));
        double b = Double.valueOf(properties.getProperty(prefix + "colorB"));
        double a = Double.valueOf(properties.getProperty(prefix + "colorO"));
        layer.setColor(new Color(r, g, b, a));

        if (layer.getType() == MarkLayer.IMAGE){
            Image im = images.get(Integer.valueOf(properties.getProperty(prefix + "image")));

            if (im == null) {
                errorMessages.add("画像属性ですが画像が取得できませんでした。");
            } else {
                layer.setImage(im);
            }
        }

        return layer;
    }
}
