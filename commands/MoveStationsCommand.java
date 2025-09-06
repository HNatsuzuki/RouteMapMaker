package RouteMapMaker.commands;

import java.util.ArrayList;
import java.util.List;

import RouteMapMaker.MvSta;
import RouteMapMaker.Station;

/**
 * undo, redo が可能な、駅を移動するコマンドです。
 */
public class MoveStationsCommand implements Command {
    //MvStaオブジェクトをそのまま使うと外部から変更された時にヤバイので全て値をコピーして使います。
    private final List<Station> stations = new ArrayList<>();
    private final List<Boolean> isSet = new ArrayList<>(); //変更前の状態（変更後は必ずtrueなので）
    private final List<Double> startX = new ArrayList<>();
    private final List<Double> startY = new ArrayList<>();
    private final List<Double> afterX = new ArrayList<>();
    private final List<Double> afterY = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param movingStations 移動する駅
     */
    public MoveStationsCommand(List<MvSta> movingStations){
        for (MvSta ms: movingStations) {
            stations.add(ms.getStation());
            isSet.add(ms.getIsSet());
            startX.add(ms.getStart()[0]);
            startY.add(ms.getStart()[1]);
            afterX.add(ms.getStation().getPoint()[0]);
            afterY.add(ms.getStation().getPoint()[1]);
        }
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        for (int i = 0; i < stations.size(); ++i) {
            stations.get(i).setPoint(afterX.get(i), afterY.get(i));
        }
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        for (int i = 0; i < stations.size(); i++) {
            if (isSet.get(i)) {
                stations.get(i).setPoint(startX.get(i), startY.get(i));
            } else {
                stations.get(i).setInterPoint(startX.get(i), startY.get(i));
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
