package RouteMapMaker.models;

/**
 * canvas に描画するテキストを表すクラスです。
 */
public class TextLabel {
    private final String text;
    private final Point2D position;
    private final TextStyle style;

    /**
     * コンストラクタ
     *
     * @param text テキスト
     * @param position 位置
     * @param style スタイル
     */
    public TextLabel(String text, Point2D position, TextStyle style) {
        this.text = text;
        this.position = position;
        this.style = style;
    }

    public String getText() {
        return this.text;
    }

    public Point2D getPosition() {
        return this.position;
    }

    public TextStyle getStyle() {
        return this.style;
    }
}
