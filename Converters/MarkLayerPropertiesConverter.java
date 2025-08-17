package RouteMapMaker.Converters;

import java.util.List;
import java.util.Properties;

import RouteMapMaker.MarkLayer;
import javafx.scene.image.Image;

/**
 * MarkLayer と Properties の相互変換を行うクラスです。
 */
public class MarkLayerPropertiesConverter {
    /**
     * MarkLayer を Properties に変換します。
     *
     * @param markLayer MarkLayer
     * @param images 出力用画像リスト
     * @param prefix 接頭辞
     * @return Properties
     */
    public static Properties toProperties(MarkLayer markLayer, List<Image> images, String prefix) {
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
            images.add(markLayer.getImage());
        }

        return properties;
    }
}
