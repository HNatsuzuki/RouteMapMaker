package RouteMapMaker.models;

/**
 * テキストの配置を表す enum です。
 */
public enum TextLocation {
    CENTER,
    TOP,
    BOTTOM,
    LEFT,
    RIGHT,
    INHERIT;

    /**
     * 路線のテキスト配置から変換します。
     *
     * @param location 路線のテキスト配置
     * @return テキスト配置
     */
    public static TextLocation fromLineLocation(int location) {
        switch (location) {
            case Line.CENTER:
                return CENTER;
            case Line.TOP:
                return TOP;
            case Line.BOTTOM:
                return BOTTOM;
            case Line.LEFT:
                return LEFT;
            case Line.RIGHT:
                return RIGHT;
            default:
                throw new IllegalArgumentException("対応する値がありません。");
        }
    }

    /**
     * 駅のテキスト配置から変換します。
     *
     * @param location 駅のテキスト配置
     * @return テキスト配置
     */
    public static TextLocation fromStationLocation(int location) {
        switch (location) {
            case Station.TEXT_CENTER:
                return CENTER;
            case Station.TEXT_TOP:
                return TOP;
            case Station.TEXT_BOTTOM:
                return BOTTOM;
            case Station.TEXT_LEFT:
                return LEFT;
            case Station.TEXT_RIGHT:
                return RIGHT;
            case Station.TEXT_UNSET:
                return INHERIT;
            default:
                throw new IllegalArgumentException("対応する値がありません。");
        }
    }
}
