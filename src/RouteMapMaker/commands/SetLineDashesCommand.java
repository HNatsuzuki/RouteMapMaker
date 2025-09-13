package RouteMapMaker.commands;

import RouteMapMaker.models.LineDash;
import RouteMapMaker.models.Train;

public class SetLineDashesCommand implements Command {
    private final Train train;
    private final LineDash oldArray;
    private final LineDash newArray;

    public SetLineDashesCommand(Train train, LineDash oldArray, LineDash newArray){
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
