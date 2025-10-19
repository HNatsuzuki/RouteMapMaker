package RouteMapMaker.commands;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.Station;

/**
 * undo, redo が可能な、路線に駅を追加するコマンドです。
 */
public class LineStationAddCommand implements Command {
    private final Line line;
    private final int index;
    private final Station station;

    /**
     * コンストラクタ
     *
     * @param line 対象路線
     * @param index 追加する場所
     * @param station 追加する駅
     */
    public LineStationAddCommand(Line line, int index, Station station) {
        this.line = line;
        this.index = index;
        this.station = station;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        line.insertStation(index, station);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        line.removeStation(index);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
