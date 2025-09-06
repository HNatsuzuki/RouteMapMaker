package RouteMapMaker.commands;

import RouteMapMaker.models.DoubleArrayWrapper;
import RouteMapMaker.models.Train;

public class SetLineDashesCommand implements Command {
    private final Train train;
    private final DoubleArrayWrapper oldArray;
    private final DoubleArrayWrapper newArray;

    public SetLineDashesCommand(Train train, DoubleArrayWrapper oldArray, DoubleArrayWrapper newArray){
        this.train = train;
        this.oldArray = oldArray;
        this.newArray = newArray;
    }

    @Override
    public void execute() {
        train.setLineDash(newArray);
    }

    @Override
    public void undo() {
        train.setLineDash(oldArray);
    }

    @Override
    public void redo() {
        this.execute();
    }
}
