package RouteMapMaker.listcells;

import RouteMapMaker.models.MarkLayer;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;

/**
 * カスタムマークのレイヤー用 ListCell です。
 */
public class MarkLayerCell extends ListCell<MarkLayer> {
    @Override
    protected void updateItem(MarkLayer item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            // 古いバインドを解除
            textProperty().unbind();
            setText(null);
        } else {
            textProperty().bind(Bindings.createStringBinding(() -> {
                String paintType;

                switch (item.getPaint()) {
                    case MarkLayer.FILL:
                        paintType = "Fill";
                        break;
                    case MarkLayer.STROKE:
                        paintType = "Stroke";
                        break;
                    default:
                        paintType = "";
                        break;
                }

                switch (item.getType()) {
                    case MarkLayer.OVAL:
                        return "円・楕円[" + paintType + "]";
                    case MarkLayer.ARC:
                        return "円弧[" + paintType + "]";
                    case MarkLayer.RECT:
                        return "長方形[" + paintType + "]";
                    case MarkLayer.POLYGON:
                        return "多角形[" + paintType + "]";
                    case MarkLayer.LINE:
                        return "直線[" + paintType + "]";
                    case MarkLayer.TEXT:
                        return "文字列[" + paintType + "]";
                    case MarkLayer.IMAGE:
                        return "画像[" + item.getText() + "]";
                    default:
                        return "";
                }
            }, item.getPaintProperty()));
        }
    }
}
