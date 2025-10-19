package RouteMapMaker.models;

import java.util.stream.Stream;

/**
 * フォントスタイルを表す enum です。
 */
public enum FontStyle {
    REGULAR(0x00, "Regular"),
    BOLD(0x01, "Bold"),
    ITALIC(0x02, "Italic"),
    BOLD_ITALIC(BOLD.bit | ITALIC.bit, "BoldItalic"),
    INHERIT(0x80, "路線準拠");

    private final int bit;
    private final String displayName;

    private FontStyle(int bit, String displayName) {
        this.bit = bit;
        this.displayName = displayName;
    }

    public boolean isBold() {
        return (this.bit & BOLD.bit) == BOLD.bit;
    }

    public boolean isItalic() {
        return (this.bit & ITALIC.bit) == ITALIC.bit;
    }

    /**
     * 路線のフォントスタイルで有効な値を取得します。
     *
     * @return 有効なフォントスタイルのリスト
     */
    public static FontStyle[] availableLineFontStyleValues() {
        return Stream.of(values()).filter(f -> f != INHERIT).toArray(FontStyle[]::new);
    }

    /**
     * 路線のテキストスタイルから変換します。
     *
     * @param style 路線のテキストスタイル
     * @return フォントスタイル
     */
    public static FontStyle fromLineTextStyle(int style) {
        switch (style) {
            case Line.REGULAR:
                return REGULAR;
            case Line.BOLD:
                return BOLD;
            case Line.ITALIC:
                return ITALIC;
            case Line.ITALIC_BOLD:
                return BOLD_ITALIC;
            default:
                throw new IllegalArgumentException("不正なフォントスタイルです: " + style);
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}
