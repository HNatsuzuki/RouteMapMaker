package RouteMapMaker.listcells;

import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.services.CustomMarkDrawer;
import javafx.collections.ListChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class StopMarkCell extends ListCell<StopMark> implements Callback<ListView<StopMark>, ListCell<StopMark>> {
	private final double PREVIEW_SIZE = 20;
	private final Circle circle = new Circle(7, 7, 5, Color.BLACK);
	private final Pane pane = new Pane();
	private final Canvas canvas = new Canvas(PREVIEW_SIZE, PREVIEW_SIZE);
	private final CustomMarkDrawer drawer = new CustomMarkDrawer(canvas.getGraphicsContext2D());
	private final ListChangeListener<MarkLayer> listener = change -> updateItem(getItem(), isEmpty());
	private StopMark lastItem = null;

	public StopMarkCell() {
		pane.setPrefSize(PREVIEW_SIZE, PREVIEW_SIZE);
		pane.getChildren().add(canvas);
	}

	@Override
	public ListCell<StopMark> call(ListView<StopMark> param) {
		return new StopMarkCell();
	}

	@Override
	protected void updateItem(StopMark item, boolean empty) {
		super.updateItem(item, empty);

		if (lastItem != null && lastItem != item) {
			lastItem.getLayers().removeListener(listener);
		}

		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			//実際のcellでの描画処理はここ。
			if (item == StopMark.OBEY_LINE) {
				setGraphic(null);
				setText("経路準拠");
			} else if (item == StopMark.NO_DRAW) {
				setGraphic(null);
				setText("非表示");
			} else if (item == StopMark.CIRCLE) {
				setText(null);
				setGraphic(circle);
			} else {
				if (lastItem != item) {
					// リスナを使ってレイヤー変更時に再描画がかかるようにする
					item.getLayers().addListener(listener);
				}

				if (item.getLayers().size() == 0) {
					//まだ中身の無い空マークだった場合
					setGraphic(null);
					setText("空のマーク");
				} else {
					//ここから、実際の描画処理
					drawer.drawCustomMarkPreview(item, PREVIEW_SIZE);
					setText(null);
					setGraphic(pane);
				}
			}
		}

		lastItem = item;
	}
}
