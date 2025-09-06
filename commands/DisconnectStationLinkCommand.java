package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.Station;
import RouteMapMaker.Train;
import RouteMapMaker.TrainStop;

/**
 * undo, redo が可能な、駅の接続を解除するコマンドです。
 */
public class DisconnectStationLinkCommand implements Command {
    private final List<Station> staList;
    private final Station oldSta;//置き換え前の駅
    private final Station newSta;//接続を切った後の新駅
    private final int staIndex;//line.stationsの方の置き換えるindex
    private final List<Train> trains;
    private final List<Integer[]> setList;//{系統番号,停車駅番号}
    private final List<TrainStop> stopValue;//偶数はold,奇数はnewにする

    public DisconnectStationLinkCommand(List<Station> staList, Station oldSta, Station newSta, int staIndex,
            List<Train> trains,List<Integer[]> setList, List<TrainStop> stopValue){
        this.staList = staList;
        this.oldSta = oldSta;
        this.newSta = newSta;
        this.staIndex = staIndex;
        this.trains = trains;
        this.setList = setList;
        this.stopValue = stopValue;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        throw new UnsupportedOperationException("Execute is not supported.");
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        staList.set(staIndex, oldSta);

        for (int i = 0; i < setList.size(); i++) {
            Integer[] setIndex = setList.get(i);
            trains.get(setIndex[0].intValue()).getStops().set(setIndex[1].intValue(), stopValue.get(i * 2));
        }
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        staList.set(staIndex, newSta);
        for (int i = 0; i < setList.size(); i++) {
            Integer[] setIndex = setList.get(i);
            trains.get(setIndex[0].intValue()).getStops().set(setIndex[1].intValue(), stopValue.get(i * 2 + 1));
        }
    }

}
