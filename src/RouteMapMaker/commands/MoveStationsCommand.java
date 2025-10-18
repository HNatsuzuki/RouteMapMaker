package RouteMapMaker.commands;

import java.util.ArrayList;
import java.util.List;

import RouteMapMaker.models.MvSta;
import RouteMapMaker.models.Point2D;
import RouteMapMaker.models.Station;

/**
 * undo, redo が可能な、駅を移動するコマンドです。
 */
public class MoveStationsCommand implements Command {
    //MvStaオブジェクトをそのまま使うと外部から変更された時にヤバイので全て値をコピーして使います。
    private final List<Station> stations = new ArrayList<>();
    private final List<Boolean> isSet = new ArrayList<>(); //変更前の状態（変更後は必ずtrueなので）
    private final List<Point2D> start = new ArrayList<>();
    private final List<Point2D> after = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param movingStations 移動する駅
     */
    public MoveStationsCommand(List<MvSta> movingStations){
        for (MvSta ms: movingStations) {
            stations.add(ms.getStation());
            isSet.add(ms.getIsSet());
            start.add(ms.getStart());
            after.add(ms.getStation().getPoint());
        }
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        for (int i = 0; i < stations.size(); ++i) {
            stations.get(i).setPoint(after.get(i));
        }
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        for (int i = 0; i < stations.size(); i++) {
            if (isSet.get(i)) {
                stations.get(i).setPoint(start.get(i));
            } else {
                stations.get(i).setInterPoint(start.get(i));
            }
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
