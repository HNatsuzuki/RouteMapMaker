package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.Line;
import RouteMapMaker.Station;
import RouteMapMaker.TrainStop;

/**
 * undo, redo が可能な、駅統合コマンドです。
 */
public class IntegrateStationCommand implements Command {
    private final List<Line.Connection> connections; //置き換える駅のConnectionの配列
    private final List<TrainStop> stops; //置き換える駅を含んだTrainStopの配列
    private final Station oldStation;//置き換え前
    private final Station newStation;//置き換え後

    /**
     * コンストラクタ
     *
     * @param connections 接続
     * @param stops 駅
     * @param oldStation 統合前の駅
     * @param newStation 統合後の駅
     */
    public IntegrateStationCommand(List<Line.Connection> connections, List<TrainStop> stops,
            Station oldStation, Station newStation) {
        this.connections = connections;
        this.stops = stops;
        this.oldStation = oldStation;
        this.newStation = newStation;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        connections.forEach(c -> c.setStation(newStation));
        stops.forEach(s -> s.setSta(newStation));
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        connections.forEach(c -> c.setStation(oldStation));
        stops.forEach(s -> s.setSta(oldStation));
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
