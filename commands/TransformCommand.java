package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.UIController;
import javafx.beans.property.DoubleProperty;

public class TransformCommand implements Command {
    private final List<DoubleProperty> props;
    private final List<Double> oldVals;
    private final List<Double> newVals;
    private final double[] oldSize;
    private final double[] newSize;
    private final UIController uic;
    public TransformCommand(List<DoubleProperty> props, List<Double> oldVals, List<Double> newVals,
            double[] oldSize, double[] newSize, UIController uic){
        this.props = props;
        this.oldVals = oldVals;
        this.newVals = newVals;
        this.oldSize = oldSize;
        this.newSize = newSize;
        this.uic = uic;
    }
    public void execute() {
        throw new UnsupportedOperationException("Execute is not supported.");
    }

    @Override
    public void undo() {
        for(int i = 0; i < props.size(); i++){
            props.get(i).set(oldVals.get(i));
        }
        uic.canvasOriginal = oldSize;
    }

    @Override
    public void redo() {
        for(int i = 0; i < props.size(); i++){
            props.get(i).set(newVals.get(i));
        }
        uic.canvasOriginal = newSize;
    }
}
