package RouteMapMaker.factories;

import java.util.Optional;
import java.util.stream.Collectors;

import RouteMapMaker.models.FontStyle;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.TextLabel;
import RouteMapMaker.models.TextLocation;
import RouteMapMaker.models.TextStyle;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;

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

        TextLocation location;
        if (station.getTextLocation() == Station.TEXT_UNSET) {
            location = TextLocation.fromLineLocation(line.getNameLocation());
        } else {
            location = TextLocation.fromStationLocation(station.getTextLocation());
        }

        //駅名シフト
        int[] offset;

        if (isEditMode) {
            //路線編集モードならオフセットなし
            offset = new int[] {0, 0};
        } else if (station.shiftBasedOnStation()){
            //駅の設定準拠
            offset = station.getNameZure();
        } else {
            //路線の設定準拠
            offset = line.getNameZure();
        }

        boolean isVertical = station.getTextLocation() == Station.TEXT_UNSET ? line.isTategaki() : station.isTategaki();
        String text;

        if (isVertical) {
            // 縦書きは1文字ずつの改行で対応する
            text = station.getName().chars().mapToObj(c -> String.valueOf((char)c)).collect(Collectors.joining("\n"));
        } else {
            text = station.getName();
        }

        int style = station.getNameStyle() == Station.STYLE_UNSET ? line.getNameStyle() : station.getNameStyle();
        TextStyle textStyle = new TextStyle(size, fontFamily.get(), FontStyle.fromLineTextStyle(style), location, isVertical, line.getNameColor());
        double[] stationPoint = station.getPointUS();
        Point2D position = new Point2D(stationPoint[0] + offset[0], stationPoint[1] + offset[1]);

        return Optional.of(new TextLabel(text, position, textStyle));
    }
}
