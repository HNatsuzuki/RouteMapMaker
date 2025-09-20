package RouteMapMaker.models;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * テキストの描画スタイルを表すクラスです。
 */
public class TextStyle {
    private final int size;
    private final String fontFamily;
    private final FontStyle style;
    private final TextLocation location;
    private final boolean isVertical;
    private final Color color;

    public TextStyle(int size, String fontFamily, FontStyle style, TextLocation location, boolean isVertical, Color color) {
        this.size = size;
        this.fontFamily = fontFamily;
        this.style = style;
        this.location = location;
        this.isVertical = isVertical;
        this.color = color;
    }

    /**
     * 文字色を取得します。
     *
     * @return 文字色
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * フォントを取得します。
     *
     * @return フォント
     */
    public Font getFont() {
        return Font.font(fontFamily, getFontWeight(), getFontPosture(), size);
    }

    /**
     * フォントの太さを取得します。
     *
     * @return フォントの太さ
     */
    public FontWeight getFontWeight() {
        return style.isBold() ? FontWeight.BOLD : FontWeight.NORMAL;
    }

    /**
     * フォントがイタリックかどうかを取得します。
     *
     * @return フォントがイタリックかどうか
     */
    public FontPosture getFontPosture() {
        return style.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
    }

    /**
     * 水平方向のオフセットを取得します。
     *
     * @return 水平方向のオフセット
     */
    public double getHorizontalOffset() {
        if (isVertical) {
            switch (location) {
                case LEFT:
                    return -size / 2;
                case RIGHT:
                    return size / 2;
                default:
                    return 0;
            }
        } else {
            switch (location) {
                case LEFT:
                    return -5;
                case RIGHT:
                    return 5;
                default:
                    return 0;
            }
        }
    }

    /**
     * 水平方向の配置方法を取得します。
     *
     * @return 水平方向の配置方法
     */
    public TextAlignment getTextAlignment() {
        if (isVertical) {
            // 縦書きは中央寄せ固定
            return TextAlignment.CENTER;
        }

        switch (location) {
            case LEFT:
                return TextAlignment.RIGHT;
            case RIGHT:
                return TextAlignment.LEFT;
            default:
                return TextAlignment.CENTER;
        }
    }

    /**
     * 垂直方向の配置方法を取得します。
     *
     * @return 垂直方向の配置方法
     */
    public VPos getVPos() {
        switch (location) {
            case TOP:
                return VPos.BOTTOM;
            case BOTTOM:
                return VPos.TOP;
            default:
                return VPos.CENTER;
        }
    }
}
