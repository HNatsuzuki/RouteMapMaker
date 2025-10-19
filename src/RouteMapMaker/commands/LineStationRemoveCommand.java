package RouteMapMaker.commands;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.Station;

/**
 * undo, redo が可能な、路線から駅を削除するコマンドです。
 */
public class LineStationRemoveCommand implements Command {
    private final Line line;
    private final int index;
    private final Station station;

    /**
     * コンストラクタ
     *
     * @param line 対象路線
     * @param index 削除する場所
     */
    public LineStationRemoveCommand(Line line, int index) {
        this.line = line;
        this.index = index;
        this.station = line.getStation(index);
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        line.removeStation(index);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        line.insertStation(index, station);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
