package RouteMapMaker.commands;

import java.util.List;

import RouteMapMaker.models.FreeItem;

/**
 * undo, redo が可能な、すべての自由挿入アイテムを移動するコマンドです。
 */
public class TranslateFreeItemsCommand implements Command {
    private final List<FreeItem> freeItems;
    private final double translateX;
    private final double translateY;

    public TranslateFreeItemsCommand(List<FreeItem> freeItems, double translateX, double translateY) {
        this.freeItems = freeItems;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    /**
     * 平行移動を行います。
     *
     * @param x x方向の移動量
     * @param y y方向の移動量
     */
    private void translate(double x, double y) {
        freeItems.forEach(f -> f.translate(x, y));
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        translate(translateX, translateY);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        translate(-translateX, -translateY);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
