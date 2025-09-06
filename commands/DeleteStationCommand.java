package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.Line;
import RouteMapMaker.Train;
import RouteMapMaker.TrainStop;

public class DeleteStationCommand implements Command {
    private final List<Line.Connection> staList;
    private final int staIndex;
    private final Line.Connection removedCon;
    private final List<Train> trains;
    private final List<Integer[]> removeList;
    private final List<TrainStop> stopValue;

    public DeleteStationCommand(List<Line.Connection> staList, int staIndex, Line.Connection removedCon,
            List<Train> trains, List<Integer[]> removeList, List<TrainStop> stopValue) {
        this.staList = staList;
        this.staIndex = staIndex;
        this.removedCon = removedCon;
        this.trains = trains;
        this.removeList = removeList;
        this.stopValue = stopValue;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Execute is not supported.");
    }

    @Override
    public void undo() {
        staList.add(staIndex, removedCon);//接続自体の復元
        //一緒に削除された停車駅の復元
        for(int i = 0; i < removeList.size(); i++){
            Integer[] removeIndex = removeList.get(i);
            trains.get(removeIndex[0]).getStops().add(removeIndex[1], stopValue.get(i));
        }
    }

    @Override
    public void redo() {
        // TODO Auto-generated method stub
        staList.remove(staIndex);//駅自体の削除
        //停車駅の削除
        for(int i = 0; i < removeList.size(); i++){
            Integer[] removeIndex = removeList.get(i);
            trains.get(removeIndex[0].intValue()).getStops().remove(removeIndex[1].intValue());
        }
    }
}
