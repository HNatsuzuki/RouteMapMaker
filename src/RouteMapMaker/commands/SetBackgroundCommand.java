package RouteMapMaker.commands;

import RouteMapMaker.models.Background;

public class SetBackgroundCommand implements Command {
    private final Background prevBg, replacedBg, target;
    public SetBackgroundCommand(Background prev, Background replaced, Background target) {
        this.prevBg = prev;
        this.replacedBg = replaced;
        this.target = target;
    }
    @Override
    public void undo() {
        target.copyParams(prevBg);
    }
    @Override
    public void redo() {
        target.copyParams(replacedBg);
    }

    public void execute() {
        throw new UnsupportedOperationException("Execute is not supported.");
    }
}
