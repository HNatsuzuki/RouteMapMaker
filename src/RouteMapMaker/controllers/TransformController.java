package RouteMapMaker.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import RouteMapMaker.factories.AlertFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TransformController implements Initializable {

	private final int WITH_FI = -10;
	private final int NO_FI = -11;
	private final int CANCEL = -12;
	private final AlertFactory alertFactory;
	private final Dimension2D canvasSize;
	private TransformType transformType = TransformType.NONE;
	private boolean transformWithFreeItem = false;

	public enum TransformType {
		NONE,
		TRANSLATE,
		SCALE,
	}
	
	@FXML Spinner<Integer> trans_X;
	@FXML Spinner<Integer> trans_Y;
	@FXML Button trans_AP;
	@FXML Spinner<Integer> scale_Width;
	@FXML Spinner<Integer> scale_Height;
	@FXML CheckBox scale_fix;
	@FXML Spinner<Integer> scale_X;
	@FXML Spinner<Integer> scale_Y;
	@FXML Label scale_after;
	@FXML Button scale_AP;

	public TransformController(AlertFactory alertFactory, Dimension2D canvasSize) {
		this.alertFactory = alertFactory;
		this.canvasSize = canvasSize;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		trans_X.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
		trans_Y.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
		trans_AP.setOnAction((ActionEvent) ->{
			if (!validateTranslateParameters()) {
				return;
			}

			int choice = confirm("平行移動");
			if(choice != CANCEL){
				transformType = TransformType.TRANSLATE;
				transformWithFreeItem = choice == WITH_FI;
				((Stage)((Node)ActionEvent.getSource()).getScene().getWindow()).close();
			}
		});
		scale_Width.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE));
		scale_Height.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE));
		scale_fix.setSelected(true);
		scale_X.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
		scale_Y.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0));
		scale_Width.getValueFactory().setValue(100);
		scale_Height.getValueFactory().setValue(100);
		scale_Width.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(scale_fix.isSelected()) scale_Height.getValueFactory().setValue(scale_Width.getValue());
			calcSize();
		});
		scale_Height.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(scale_fix.isSelected()) scale_Width.getValueFactory().setValue(scale_Height.getValue());
			calcSize();
		});
		scale_X.valueProperty().addListener((obs, oldVal, newVal) -> {
			calcSize();
		});
		scale_Y.valueProperty().addListener((obs, oldVal, newVal) -> {
			calcSize();
		});
		scale_AP.setOnAction((ActionEvent) ->{
			if (!validateScaleParameters()) {
				return;
			}

			int choice = confirm("平行移動");
			if(choice != CANCEL){
				transformType = TransformType.SCALE;
				transformWithFreeItem = choice == WITH_FI;
				((Stage)((Node)ActionEvent.getSource()).getScene().getWindow()).close();
			}
		});
		scale_after.setText(canvasSize.getWidth() + " × " + canvasSize.getHeight());
	}

	private int confirm(String text){//FreeItemも一緒に移すかやらないかキャンセルかを問うダイアログを作る
		Alert alert = alertFactory.createAlert(AlertType.CONFIRMATION);
		alert.setContentText(text + " を行います。\n"
				+ "自由挿入アイテムの位置座標も一緒に変更しますか？");
		ButtonType buttonWithFI = new ButtonType("一緒に変更");
		ButtonType buttonNoFI = new ButtonType("駅座標だけ");
		ButtonType buttonCancel = new ButtonType("キャンセル",ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonWithFI,buttonNoFI,buttonCancel);
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == buttonWithFI){
			return WITH_FI;
		}else if(result.get() == buttonNoFI){
			return NO_FI;
		}else{
			return CANCEL;
		}
	}
	private void calcSize(){//スケール変換後のサイズを計算し、表示する。
		double[] after = new double[2];
		try{
			double X = scale_X.getValue().doubleValue();
			double Y = scale_Y.getValue().doubleValue();
			double W = scale_Width.getValue().doubleValue() / 100;
			double H = scale_Height.getValue().doubleValue() / 100;
			after[0] = (canvasSize.getWidth() - X) * W + X;
			after[1] = (canvasSize.getHeight() - Y) * H + Y;
			scale_after.setText((int)after[0] + " × " + (int) after[1]);
		}catch(NumberFormatException e){
			scale_after.setText("");
		}
	}

	/**
	 * 平行移動パラメータの入力チェックを行います。
	 *
	 * @return エラーがない場合 true
	 */
	private boolean validateTranslateParameters() {
		try {
			Integer.parseInt(trans_X.getEditor().getText());
			Integer.parseInt(trans_Y.getEditor().getText());

			return true;
		} catch (NumberFormatException e) {
			Alert alert = alertFactory.createAlert(AlertType.ERROR);
			alert.setContentText("パラメーターを確認してください。\n パラメーターには半角数字を入力してください。");
			alert.showAndWait();

			return false;
		}
	}

	/**
	 * 拡大縮小パラメータの入力チェックを行います。
	 *
	 * @return エラーがない場合 true
	 */
	private boolean validateScaleParameters() {
		try {
			Double.parseDouble(scale_Width.getEditor().getText());
			Double.parseDouble(scale_Height.getEditor().getText());
			Integer.parseInt(scale_X.getEditor().getText());
			Integer.parseInt(scale_Y.getEditor().getText());

			return true;
		} catch(NumberFormatException e) {
			Alert alert = alertFactory.createAlert(AlertType.ERROR);
			alert.setContentText("パラメーターを確認してください。\n パラメーターには半角数字を入力してください。");
			alert.showAndWait();

			return false;
		}
	}

	public double getTranslateX() {
		return Integer.parseInt(trans_X.getEditor().getText());
	}

	public double getTranslateY() {
		return Integer.parseInt(trans_Y.getEditor().getText());
	}

	public double getScaleX() {
		return Double.parseDouble(scale_Width.getEditor().getText()) / 100;
	}

	public double getScaleY() {
		return Double.parseDouble(scale_Height.getEditor().getText()) / 100;
	}

	public double getPivotX() {
		return Integer.parseInt(scale_X.getEditor().getText());
	}

	public double getPivotY() {
		return Integer.parseInt(scale_Y.getEditor().getText());
	}

	public TransformType getTransformType() {
		return transformType;
	}

	public boolean isTransformWithFreeItem() {
		return transformWithFreeItem;
	}
}
