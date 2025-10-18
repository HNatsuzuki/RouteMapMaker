package RouteMapMaker.commands;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.LineList;
import RouteMapMaker.models.Point2D;
import RouteMapMaker.models.Station;

/**
 * undo, redo が可能な、路線全体を拡大・縮小するコマンドです。
 */
public class ScaleLineStationsCommand implements Command {
    private final LineList lineList;
    private final double scaleX;
    private final double scaleY;
    private final double pivotX;
    private final double pivotY;
    private final Map<Station, Point2D> oldPoints;

    /**
     * コンストラクタ
     *
     * @param lineList 路線リスト
     * @param scaleX X方向の拡大率
     * @param scaleY Y方向の拡大率
     * @param pivotX 拡大中心X座標
     * @param pivotY 拡大中心Y座標
     */
    public ScaleLineStationsCommand(LineList lineList, double scaleX, double scaleY, double pivotX, double pivotY) {
        this.lineList = lineList;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        oldPoints = lineList.stream().flatMap(l -> l.getStations().stream()).collect(Collectors.toMap(s -> s, s -> s.getPointUSAsPoint2D(), (e, r) -> e));
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        Set<Station> scaledStations = new HashSet<>();

        for (Line line : lineList) {
            for (Station station: line.getStations()) {
                if (scaledStations.contains(station)) {
                    // 移動済みは処理しない
                    continue;
                }

                Point2D oldPoint = station.getPointProperty().get();

                // X
                double oldX = oldPoint.getX();
                double newX = (oldX - pivotX) * scaleX + pivotX;
                // Y
                double oldY = oldPoint.getY();
                double newY = (oldY - pivotY) * scaleY + pivotY;

                station.getPointProperty().set(new Point2D(newX, newY));
                scaledStations.add(station);
            }
        }
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        for (Entry<Station, Point2D> oldPoint : oldPoints.entrySet()) {
            Station station = oldPoint.getKey();
            Point2D point = oldPoint.getValue();
            station.getPointProperty().set(point);
        }
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
