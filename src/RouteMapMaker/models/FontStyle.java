package RouteMapMaker.models;

/**
 * フォントスタイルを表す enum です。
 */
public enum FontStyle {
    NORMAL(0x00),
    BOLD(0x01),
    ITALIC(0x02),
    BOLD_ITALIC(BOLD.bit | ITALIC.bit);

    private final int bit;

    private FontStyle(int bit) {
        this.bit = bit;
    }

    public boolean isBold() {
        return (this.bit & BOLD.bit) == BOLD.bit;
    }

    public boolean isItalic() {
        return (this.bit & ITALIC.bit) == ITALIC.bit;
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
                return NORMAL;
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
}
