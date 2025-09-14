package RouteMapMaker.factories;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.Station;

/**
 * 路線のファクトリクラスです。
 */
public final class LineFactory {
    /** 新規作成時の終点駅のX方向距離 */
    private static final double INITIAL_STATION_DISTANCE_X = 200;
    /** 新規作成時の終点駅のY方向距離 */
    private static final double INITIAL_STATION_DISTANCE_Y = 0;

    /**
     * 新たな路線を作成します。
     *
     * @param name 路線名
     * @param baseX 基準X座標
     * @param baseY 基準Y座標
     * @return 作成した路線
     */
    public static Line create(String name, double baseX, double baseY) {
        Line line = new Line(name);
        Station startingStation = new Station(name + "始点");
        startingStation.setPoint(baseX, baseY);
        line.addStation(startingStation);
        Station terminalStation = new Station(name + "終点");
        terminalStation.setPoint(baseX + INITIAL_STATION_DISTANCE_X, baseY + INITIAL_STATION_DISTANCE_Y);
        line.addStation(terminalStation);

        return line;
    }
}
