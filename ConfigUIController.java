package RouteMapMaker;

import java.net.URL;
import java.util.ResourceBundle;

import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.factories.View;
import RouteMapMaker.models.Configuration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class ConfigUIController implements Initializable{
	private final Configuration config;//設定はここに保存。
	private final SceneFactory sceneFactory;
	
	@FXML CheckBox showGrid;
	@FXML Spinner<Integer> GridInterval;
	@FXML CheckBox BindX;
	@FXML CheckBox BindY;
	@FXML CheckBox menubarMode;
	@FXML ColorPicker fixedColor;
	@FXML ColorPicker nonFixedColor;
	@FXML RadioButton gridTriangle;
	@FXML RadioButton gridSquare;
	@FXML Label uiFont;
	@FXML Button selectFont;
	
	public ConfigUIController(Configuration config, SceneFactory sceneFactory) {
		this.config = config;
		this.sceneFactory = sceneFactory;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		GridInterval.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5,1000));
		showGrid.setOnAction((ActionEvent) ->{
			config.setR_grid(showGrid.isSelected());
		});
		GridInterval.valueProperty().addListener((obs, oldVal, newVal) -> {
			config.setR_gridInterval(GridInterval.getValue());
		});
		BindX.setOnAction((ActionEvent) ->{
			config.setR_bindToGridX(BindX.isSelected());
		});
		BindY.setOnAction((ActionEvent) ->{
			config.setR_bindToGridY(BindY.isSelected());
		});
		menubarMode.setOnAction((ActionEvent) ->{
			config.setMenubarMode(menubarMode.isSelected());
		});
		fixedColor.setOnAction((ActionEvent) ->{
			config.setFixedColor(fixedColor.getValue());
		});
		nonFixedColor.setOnAction((ActionEvent) ->{
			config.setNonFixedColor(nonFixedColor.getValue());
		});
		selectFont.setOnAction((ActionEvent) -> {
			SelectFontFactory factory = new SelectFontFactory(sceneFactory);
			View<selectFontController> view = factory.createSelectFontView(config.getUiFont());
			Stage stage = view.getStage();
			stage.showAndWait();
			selectFontController controller = view.getController();

			if (controller.shouldSave()) {
				String newFont = controller.getFontName();
				config.setUiFont(newFont);
				uiFont.setText(newFont);
			}
		});
		
		final ToggleGroup gridGroup = new ToggleGroup();
		gridTriangle.setToggleGroup(gridGroup);
		gridSquare.setToggleGroup(gridGroup);
		gridTriangle.setOnAction((ActionEvent) ->{
			config.setGridTriangle(true);
		});
		gridSquare.setOnAction((ActionEvent) ->{
			config.setGridTriangle(false);
		});

		this.initializeView();
	}
	
	private void initializeView() {
		showGrid.setSelected(config.getR_grid());
		GridInterval.getValueFactory().setValue(config.getR_gridInterval());
		BindX.setSelected(config.getR_bindToGridX());
		BindY.setSelected(config.getR_bindToGridY());
		fixedColor.setValue(config.getFixedColor());
		nonFixedColor.setValue(config.getNonFixedColor());
		menubarMode.setSelected(config.getMenubarMode());
		(config.isGridTriangle() ? gridTriangle : gridSquare).setSelected(true);
		uiFont.setText(config.getUiFont());
	}
}
