package RouteMapMaker.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.LineSegment;
import RouteMapMaker.models.PathSegment;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.Train;
import RouteMapMaker.models.TrainStop;
import javafx.geometry.Point2D;

/**
 * 路線に含まれる系統の線を描画するパスを計算するクラスです。
 */
public class TrainPathCalculator {
    /**
     * 路線に含まれる系統の線を描画するパスを計算します。
     * また、運転系統の駅の座標と角度が再計算されます。
     *
     * @param line 路線
     * @param train 運転系統 (駅の座標と角度が再計算され、設定されます)
     * @return 描画パス
     */
    public List<PathSegment> calculate(Line line, Train train) {
        if (!line.hasTrain(train)) {
            throw new IllegalArgumentException("指定した路線に指定した運転系統が含まれていません。");
        }

        List<TrainStop> stops = train.getStops();
        int offset = train.getLineDistance();
        List<String> lineStationNames = line.getStations().stream()
                .map(s -> s.getName()).collect(Collectors.toList()); // 路線の駅名リスト
        int lastIndex = stops.size() - 1;
        //路線における系統の始点の番号
        int startPoint = lineStationNames.indexOf(stops.get(0).getSta().getName());
        //路線における系統の終点の番号
        int endPoint = lineStationNames.lastIndexOf(stops.get(lastIndex).getSta().getName());
        //駅ごとのライン補正は情報がTrainStopにあるので何番目のTrainStopなのかカウント
        int stopCount = 0;
        List<PathSegment> stationPoints = new ArrayList<>();
        
        //ライン位置補正，駅位置補正，edgeを考慮して各駅の座標を決定していく
        for (int i = startPoint; i <= endPoint; ++i) {
            //Point2D end = null;
            Station prevPrevStation = i > 1 ? line.getStation(i - 2) : null;
            Station prevStation = i > 0 ? line.getStation(i - 1) : null;
            Station currentStation = line.getStation(i);
            Station nextStation = i < line.getStations().size() - 1 ? line.getStation(i + 1) : null;
            boolean isCurvedConnection = line.isConnectedByCurve(i);
            boolean isNextCurvedConnection = i < line.getStations().size() - 1 ? line.isConnectedByCurve(i + 1) : false;
            boolean isStart = i == startPoint;
            boolean isTerminal = i == endPoint;

            // まず路線全体からのオフセットを基に計算をする
            CalculationResult result = calculateShiftedPointAndControlPoint(
                currentStation, prevPrevStation, prevStation, nextStation, offset, isStart, isTerminal, isCurvedConnection, isNextCurvedConnection);
            
            Point2D point = result.point;
            Point2D controlPoint = result.controlPoint;
            LineSegment currentSegment = result.lineSegment;

            if (currentStation == stops.get(stopCount).getSta()) {
                //駅毎位置補正を加え、運転系統の駅座標・角度を更新する
                TrainStop trainStop = stops.get(stopCount);
                Point2D stationOffset = trainStop.getOffset();
                point = point.add(stationOffset);
                trainStop.setPosition(point);

                // 駅の角度設定
                trainStop.setAngle(result.angle);

                stopCount++;//最後にstopcountを一つ上げる。
            }

            if (isStart) {
                // 開始駅の場合の補正
                double edgeALength = train.getEdgeA();

                if (edgeALength != 0) {
                    //edgeAを考慮する。
                    Point2D startPosition = point.subtract(currentSegment.getUnitVector().multiply(edgeALength));
                    // 最初は制御点なし (指定しても参照されない)
                    stationPoints.add(new PathSegment(startPosition, null));
                }

                if (edgeALength >= 0) {
                    // 補正値ゼロの場合は駅の座標をそのまま追加し、始点とする
                    // この場合は制御点があっても参照されない
                    // 補正値が正の場合は補正点と始発駅間は直線接続のため、制御点指定なし
                    stationPoints.add(new PathSegment(point, null));
                }
            } else if (isTerminal) {
                // 終結点の場合の補正
                double edgeBLength = train.getEdgeB();

                if (edgeBLength >= 0) {
                    // 補正値が正あるいはゼロの場合は描画点に加える
                    stationPoints.add(new PathSegment(point, controlPoint));
                }

                if (edgeBLength != 0) {
                    // 終結点補正
                    Point2D endPosition = point.add(currentSegment.getUnitVector().multiply(edgeBLength));
                    // 補正値が正の場合は終着駅と補正点間は直線接続
                    // 負の場合は補正点が実質終着駅となるため曲線接続の可能性あり
                    stationPoints.add(new PathSegment(endPosition, edgeBLength < 0 ? controlPoint : null));
                }
            } else {
                stationPoints.add(new PathSegment(point, controlPoint));
            }
        }

        return stationPoints;
    }

    /**
     * 路線位置からのオフセットを考慮した駅描画位置などの計算を行います。
     *
     * @param currentStation 対象駅
     * @param prevPrevStation 対象駅の前前駅
     * @param prevStation 対象駅の前駅
     * @param nextStation 対象駅の次駅
     * @param offset 基準からのオフセット
     * @param isStartStation 対象駅が最初の駅かどうか
     * @param isTerminalStation 対象駅が終点かどうか
     * @param isCurvedConnection 対象駅が前駅と曲線接続するかどうか
     * @param isNextCurvedConnection 対象駅が次駅と曲線接続するかどうか
     * @return 計算結果
     */
    private CalculationResult calculateShiftedPointAndControlPoint(
        Station currentStation,
        Station prevPrevStation,
        Station prevStation,
        Station nextStation,
        double offset,
        boolean isStartStation,
        boolean isTerminalStation,
        boolean isCurvedConnection,
        boolean isNextCurvedConnection
    ) {
        Point2D point = null;
        Point2D controlPoint = null;
        LineSegment segment = null;
        LineSegment previousSegment = prevStation != null ? LineSegment.createShifted(
            prevStation.getPointUSAsPoint2D(), currentStation.getPointUSAsPoint2D(), offset) : null;
        LineSegment nextSegment = nextStation != null ? LineSegment.createShifted(
            currentStation.getPointUSAsPoint2D(), nextStation.getPointUSAsPoint2D(), offset) : null;

        if (isCurvedConnection) {
            point = nextSegment.getStart();
            segment = nextSegment;

            if (!isStartStation) {
                LineSegment prevPrevSegment = LineSegment.createShifted(
                    prevPrevStation.getPointUSAsPoint2D(), prevStation.getPointUSAsPoint2D(), offset);
                controlPoint = prevPrevSegment.getIntersection(nextSegment);
            }
        } else if (isNextCurvedConnection) {
            point = previousSegment.getEnd();
            segment = previousSegment;
        } else if (currentStation.isSet()) {
            // 前後非曲線固定点
            // 従来処理では、始点の場合は nextSegment、終点の場合は prevSegment 固定
            // 必要に応じてもとに戻すか選択できるようにする？
            if (previousSegment != null && nextSegment != null) {
                //この場合は歪みを防ぐため特殊な処理が必要。連立方程式を用意してその解を採用する。
                point = previousSegment.getIntersection(nextSegment);
                segment = (isTerminalStation) ? previousSegment : nextSegment;
            } else if (previousSegment != null) {
                point = previousSegment.getEnd();
                segment = previousSegment;
            } else if (nextSegment != null) {
                point = nextSegment.getStart();
                segment = nextSegment;
            }
        } else {
            // 非固定点
            point = previousSegment.getEnd();
            segment = previousSegment;
        }

        // 駅の角度計算
        double angle = previousSegment != null ? previousSegment.getAngle() : nextSegment.getAngle();

        return new CalculationResult(point, controlPoint, segment, angle);
    }

    /** 計算結果用一時クラス */
    private class CalculationResult {
        private final Point2D point;
        private final Point2D controlPoint;
        private final LineSegment lineSegment;
        private final double angle;

        public CalculationResult(Point2D point, Point2D controlPoint, LineSegment lineSegment, double angle) {
            this.point = point;
            this.controlPoint = controlPoint;
            this.lineSegment = lineSegment;
            this.angle = angle;
        }
    }
}
