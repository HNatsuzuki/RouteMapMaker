package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.Line;
import RouteMapMaker.Station;
import RouteMapMaker.TrainStop;

public class IntegrateStationCommand implements Command {
    private final List<Line.Connection> connections; //置き換える駅のConnectionの配列
    private final List<TrainStop> stops; //置き換える駅を含んだTrainStopの配列
    private final Station prevSta;//置き換え前
    private final Station replacing;//置き換え後
    private final boolean fixed;//以前座標固定点だったか否か

    public IntegrateStationCommand(List<Line.Connection> con, List<TrainStop> stops,
            Station prevSta, Station replacing, boolean fixed) {
        this.connections = con;
        this.stops = stops;
        this.prevSta = prevSta;
        this.replacing = replacing;
        this.fixed = fixed;
    }
    @Override
    public void undo() {
        connections.forEach(c -> c.setStation(prevSta));
        stops.forEach(s -> s.setSta(prevSta));
        if(!fixed) replacing.erasePoint();//座標非固定点ならば非固定にする。
    }
    @Override
    public void redo() {
        // TODO Auto-generated method stub
        if(!fixed) replacing.setPoint(replacing.getInterPoint()[0], replacing.getInterPoint()[1]);
        connections.forEach(c -> c.setStation(replacing));
        stops.forEach(s -> s.setSta(replacing));
    }

    public void execute() {
        throw new UnsupportedOperationException("Execute is not supported.");
    }
}
