package RouteMapMaker.listcells;

import RouteMapMaker.models.LineDash;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class LineDashCell  extends ListCell<LineDash> 
implements Callback<ListView<LineDash>, ListCell<LineDash>>{

	@Override
	public ListCell<LineDash> call(ListView<LineDash> param) {
		// TODO Auto-generated method stub
		return new LineDashCell(){
			@Override
			protected void updateItem(LineDash item, boolean empty){
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(null);
					Pane p = new Pane();
					p.setPrefSize(60, 15);
					Canvas canvas = new Canvas(60,15);
					GraphicsContext gc = canvas.getGraphicsContext2D();
					gc.setLineDashes(item.get());
					gc.setLineWidth(2);
					gc.setStroke(Color.BLACK);
					gc.strokeLine(0, 7, 60, 7);
					p.getChildren().add(canvas);
					setGraphic(p);
				}
			}
		};
	}

}
