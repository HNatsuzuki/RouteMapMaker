package RouteMapMaker.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import RouteMapMaker.models.FreeItem;
import javafx.geometry.Point2D;

/**
 * undo, redo が可能な、自由挿入アイテムを拡大・縮小するコマンドです。
 */
public class ScaleFreeItemsCommand implements Command {
    private final List<FreeItem> freeItems;
    private final double scaleX;
    private final double scaleY;
    private final double pivotX;
    private final double pivotY;
    private final Map<FreeItem, Point2D> oldPoints;

    /**
     * コンストラクタ
     *
     * @param freeItems 自由挿入アイテムのリスト
     * @param scaleX X方向の拡大率
     * @param scaleY Y方向の拡大率
     * @param pivotX 拡大中心X座標
     * @param pivotY 拡大中心Y座標
     */
    public ScaleFreeItemsCommand(List<FreeItem> freeItems, double scaleX, double scaleY, double pivotX, double pivotY) {
        this.freeItems = freeItems;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        oldPoints = freeItems.stream().collect(Collectors.toMap(f -> f, f -> new Point2D(f.getX(), f.getY())));
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        for (FreeItem freeItem : freeItems) {
            //X
            double oldX = freeItem.getX();
            freeItem.setX((oldX - pivotX) * scaleX + pivotX);
            //Y
            double oldY = freeItem.getY();
            freeItem.setY((oldY - pivotY) * scaleY + pivotY);
        }
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        for (Entry<FreeItem, Point2D> oldPoint : oldPoints.entrySet()) {
            FreeItem freeItem = oldPoint.getKey();
            Point2D point = oldPoint.getValue();
            freeItem.setX(point.getX());
            freeItem.setY(point.getY());
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
