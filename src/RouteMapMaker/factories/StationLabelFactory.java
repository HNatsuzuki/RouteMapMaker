package RouteMapMaker.factories;

import java.util.Optional;
import java.util.stream.Collectors;

import RouteMapMaker.models.FontStyle;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.Point2D;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.TextLabel;
import RouteMapMaker.models.TextLocation;
import RouteMapMaker.models.TextStyle;
import javafx.beans.property.StringProperty;

/**
 * 駅名描画に必要なパラメータを作成するクラスです。
 */
public class StationLabelFactory {
    private final StringProperty fontFamily;

    public StationLabelFactory(StringProperty fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * 駅名描画に必要なパラメータを作成します。
     *
     * @param station 駅
     * @param line 路線
     * @param isEditMode 編集モードかどうか
     * @return 駅名描画に必要なパラメータ。描画しない場合は empty。
     */
    public Optional<TextLabel> createLabel(Station station, Line line, boolean isEditMode) {
        int size = station.getNameSize() == 0 ? line.getNameSize() : station.getNameSize();

        if (size == -1) {
            // size が -1 の場合は描画しないため空を返す
            return Optional.empty();
        }

        TextLocation location = station.isTextLocationInherited() ? line.getNameLocation() : station.getTextLocation();

        //駅名シフト
        Point2D offset;

        if (isEditMode) {
            //路線編集モードならオフセットなし
            offset = new Point2D(0, 0);
        } else if (station.shiftBasedOnStation()){
            //駅の設定準拠
            offset = station.getNameOffset();
        } else {
            //路線の設定準拠
            offset = line.getNameOffset();
        }

        boolean isVertical = station.isTextLocationInherited() ? line.isVertical() : station.isTategaki();
        String text;

        if (isVertical) {
            // 縦書きは1文字ずつの改行で対応する
            text = station.getName().chars().mapToObj(c -> String.valueOf((char)c)).collect(Collectors.joining("\n"));
        } else {
            text = station.getName();
        }

        FontStyle style = station.getNameStyle() == Station.STYLE_UNSET ? line.getFontStyle() : FontStyle.fromLineTextStyle(station.getNameStyle());
        TextStyle textStyle = new TextStyle(size, fontFamily.get(), style, location, isVertical, line.getNameColor());
        Point2D stationPoint = station.getPointUS();
        Point2D position = stationPoint.add(offset);

        return Optional.of(new TextLabel(text, position, textStyle));
    }
}
