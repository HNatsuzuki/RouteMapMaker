package RouteMapMaker.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

import RouteMapMaker.factories.AlertFactory;
import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.factories.View;
import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.commands.AddListItemCommand;
import RouteMapMaker.commands.Command;
import RouteMapMaker.commands.RemoveListItemCommand;
import RouteMapMaker.commands.ValueSetCommand;
import RouteMapMaker.commands.SwapListItemDownCommand;
import RouteMapMaker.commands.SwapListItemUpCommand;
import RouteMapMaker.listcells.StopMarkCell;
import RouteMapMaker.services.URElements;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class CustomMarkController implements Initializable{
	final int prevSize = 40;
	private Stage stage;//このstageを保持する。
	private ObservableList<StopMark> customMarks = FXCollections.observableArrayList();//カスタムマークの集合
	private GraphicsContext gc;
	private ObservableList<String> LayerListOb = FXCollections.observableArrayList();
	private ObservableList<String> paramDrawOb = FXCollections.observableArrayList();//fill(0)かStroke(1)かのパラメーターにセット
	private URElements urManager = new URElements();//undoとredoを管理する。
	private final SceneFactory sceneFactory;
	private final AlertFactory alertFactory;
	
	@FXML Canvas markCanvas;
	@FXML Pane prevPane;
	@FXML Rectangle bgRect;
	@FXML ColorPicker prevbgSetter;
	@FXML ColorPicker paramColor;
	@FXML ChoiceBox<String> paramDraw;
	@FXML CheckBox rotateMark;
	@FXML TextField paramText;
	@FXML Label paramL1;
	@FXML Label paramL2;
	@FXML Label paramL3;
	@FXML Label paramL4;
	@FXML Label paramL5;
	@FXML Label paramL6;
	@FXML Label paramL7;
	@FXML Label paramL8;
	@FXML Label paramLT;
	@FXML Spinner<Integer> paramS1;
	@FXML Spinner<Integer> paramS2;
	@FXML Spinner<Integer> paramS3;
	@FXML Spinner<Integer> paramS4;
	@FXML Spinner<Integer> paramS5;
	@FXML Spinner<Integer> paramS6;
	@FXML Spinner<Integer> paramS7;
	@FXML Spinner<Integer> paramS8;
	@FXML ChoiceBox<String> paramST;
	@FXML Button addOval;
	@FXML Button addRect;
	@FXML Button addLine;
	@FXML Button addArc;
	@FXML Button addText;
	@FXML Button addImage;
	@FXML Button selectFont;
	@FXML ListView<StopMark> MarkList;
	@FXML ListView<String> LayerList;
	@FXML Button MarkAdd;
	@FXML Button MarkCopy;
	@FXML Button MarkDelete;
	@FXML Button LayerDelete;
	@FXML Button Layer_UP;
	@FXML Button Layer_DOWN;
	@FXML MenuItem Undo;
	@FXML MenuItem Redo;

	public CustomMarkController(SceneFactory sceneFactory, AlertFactory alertFactory) {
		this.sceneFactory = sceneFactory;
		this.alertFactory = alertFactory;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		gc = markCanvas.getGraphicsContext2D();
		MarkList.setItems(customMarks);
		StopMarkCell cellFactory = new StopMarkCell();
		MarkList.setCellFactory(cellFactory);
		MarkList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			draw();//まずは描画。
			if(MarkList.getSelectionModel().getSelectedIndex() != -1){
				setLayerList(customMarks.get(MarkList.getSelectionModel().getSelectedIndex()));
				StopMark mark = customMarks.get(MarkList.getSelectionModel().getSelectedIndex());
				if(mark.getLayers().size() != 0){
					LayerList.getSelectionModel().select(0);
				}
				rotateMark.setDisable(false);
				rotateMark.setSelected(mark.isRotated());
			}
		});
		MarkAdd.setOnAction((ActionEvent) ->{//空のマークを追加する。
			System.out.println("add called.");
			StopMark newMark = new StopMark();
			Command command = new AddListItemCommand<>(customMarks, newMark);
			command.execute();
			urManager.push(command);
			MarkList.getSelectionModel().selectLast();
		});
		MarkCopy.setOnAction((ActionEvent) ->{//マークをコピー
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				StopMark cloneMark = customMarks.get(index).clone();
				Command command = new AddListItemCommand<>(customMarks, cloneMark);
				command.execute();
				urManager.push(command);
			}
		});
		MarkDelete.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Command command = new RemoveListItemCommand<>(customMarks, index);
				command.execute();
				urManager.push(command);
			}
		});
		prevbgSetter.setValue(Color.BLACK);//初期値は黒。
		prevbgSetter.setOnAction((ActionEvent) ->{
			bgRect.setFill(prevbgSetter.getValue());
		});
		paramColor.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1){
				Command command = new ValueSetCommand<>(customMarks.get(indexM).getLayers().get(indexL).getColorProperty(), paramColor.getValue());
				command.execute();
				urManager.push(command);
			}
			draw();
		});
		paramDrawOb.add("fill");
		paramDrawOb.add("stroke");
		paramDraw.setItems(paramDrawOb);
		paramDraw.valueProperty().addListener((obs, oldValue, newValue) -> {
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1){
				MarkLayer l = customMarks.get(indexM).getLayers().get(indexL);
				if(paramDraw.getSelectionModel().getSelectedIndex() == 0){
					if (l.getPaint() == MarkLayer.STROKE) {
						Command command = new ValueSetCommand<>(l.getPaintProperty(), MarkLayer.STROKE, MarkLayer.FILL);
						command.execute();
						urManager.push(command);
					}
				}
				if(paramDraw.getSelectionModel().getSelectedIndex() == 1){
					if (l.getPaint() == MarkLayer.FILL) {
						Command command = new ValueSetCommand<>(l.getPaintProperty(), MarkLayer.FILL, MarkLayer.STROKE);
						command.execute();
						urManager.push(command);
					}
				}
				//レイヤーリスト更新
				setLayerList(MarkList.getSelectionModel().getSelectedItem());
				LayerList.getSelectionModel().select(indexL);
				draw();
			}
		});
		//スピナーについての設定は配列で一気に処理する
		Spinner[] paramSs = {paramS1,paramS2,paramS3,paramS4,paramS5,paramS6,paramS7,paramS8};
		for(int i = 0; i < 8; i++){
			paramSs[i].setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE));
		}
		setParamsReactions();//action定義が頭悪い方法でしかできないので隔離
		paramST.valueProperty().addListener((obs, oldValue, newValue) -> {
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			//コレに関しては他のレイヤーからの切替時にselectIndex-1が送られる不具合がある。とりあえずif文条件で応急処置
			int selectedIndex = paramST.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1 && selectedIndex != -1){
				MarkLayer l = customMarks.get(indexM).getLayers().get(indexL);
				if(l.getType() == MarkLayer.ARC){
					if((int)l.getParam(7) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(l.getParamProperty().get(7), (double)selectedIndex);
						command.execute();
						urManager.push(command);
					}
				}
				if(l.getType() == MarkLayer.TEXT){
					if((int)l.getParam(4) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(l.getParamProperty().get(4), (double)selectedIndex);
						command.execute();
						urManager.push(command);
					}
				}
				if(l.getType() == MarkLayer.LINE){
					if((int)l.getParam(5) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(l.getParamProperty().get(5), (double)selectedIndex);
						command.execute();
						urManager.push(command);
					}
				}
				draw();
			}
		});
		selectFont.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1){
				MarkLayer l = customMarks.get(indexM).getLayers().get(indexL);
				String newFont = null;
				String current = l.getFontName();
				SelectFontFactory factory = new SelectFontFactory(sceneFactory);
				View<SelectFontController> view = factory.createSelectFontView(current);
				SelectFontController euc = view.getController();
				Stage editStage = view.getStage();
				editStage.showAndWait();
				if(euc.shouldSave()){
					newFont = euc.getFontName();
				}else{
					newFont = current;
				}
				if (!current.equals(newFont)) {
					Command command = new ValueSetCommand<>(l.getFontNameProperty(), current, newFont);
					command.execute();
					urManager.push(command);
				}
			}
			draw();
		});
		paramText.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1){
				MarkLayer layer = customMarks.get(indexM).getLayers().get(indexL);

				if (layer.getText() != paramText.getText()) {
					Command command = new ValueSetCommand<>(layer.getTextProperty(), paramText.getText());
					command.execute();
					urManager.push(command);
				}
				draw();
			}
		});
		addOval.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newOval = new MarkLayer(MarkLayer.OVAL);
				//初期値投入
				newOval.addParam(0.0);//左上X
				newOval.addParam(0.0);//左上Y
				newOval.addParam(1.0);//横直径
				newOval.addParam(1.0);//縦直径
				newOval.addParam(0.05);//デフォの線の太さ2/40（あくまでも相対比なのでprevSizeが変わってもここは問題ない）
				newOval.setPaint(MarkLayer.FILL);
				newOval.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newOval);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(index));
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addRect.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newRect = new MarkLayer(MarkLayer.RECT);
				//初期値投入
				newRect.addParam(0.0);//左上X
				newRect.addParam(0.0);//左上Y
				newRect.addParam(1.0);//幅
				newRect.addParam(1.0);//高さ
				newRect.addParam(0.0);//円弧幅
				newRect.addParam(0.0);//円弧高さ
				newRect.addParam(0.05);//lineWidth
				newRect.setPaint(MarkLayer.FILL);
				newRect.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newRect);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(index));
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addLine.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newLine = new MarkLayer(MarkLayer.LINE);
				newLine.addParam(0.0);//始点X
				newLine.addParam(0.0);//始点Y
				newLine.addParam(1.0);//終点X
				newLine.addParam(1.0);//終点Y
				newLine.addParam(0.05);//lineWidth
				newLine.addParam(0);//端はSQUARE
				newLine.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newLine);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(index));
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addArc.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newArc = new MarkLayer(MarkLayer.ARC);
				//初期値投入
				newArc.addParam(0.0);//X
				newArc.addParam(0.0);//Y
				newArc.addParam(1.0);//幅
				newArc.addParam(1.0);//高さ
				newArc.addParam(0.0);//始角
				newArc.addParam(120.0);//角の大きさ
				newArc.addParam(0.05);//lineWidth
				newArc.addParam(2.0);//closure
				newArc.setPaint(MarkLayer.FILL);
				newArc.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newArc);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(index));
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addText.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newText = new MarkLayer(MarkLayer.TEXT);
				//初期値投入
				newText.addParam(0.0);//X
				newText.addParam(1.0);//Y
				newText.addParam(1.0);//size
				newText.addParam(0.05);//lineWidth
				newText.addParam(0.0);//type
				newText.setPaint(MarkLayer.FILL);
				newText.setColor(Color.WHITE);
				newText.setText("※");
				newText.setFontName("system");
				Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newText);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(index));
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addImage.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				MarkLayer newImage = new MarkLayer(MarkLayer.IMAGE);
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("画像ファイルを選択してください。");
				fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files(jpg,png,gif,bmp)", 
						"*.png", "*.jpg", "*.jpeg", "*.gif","*.bmp", "*.PNG", "*.JPG", "*.JPEG", "*.GIF","*.BMP"));
				File imageFile = fileChooser.showOpenDialog(null);
				if(imageFile != null){
					try {
						newImage.setImage(new Image(new BufferedInputStream(new FileInputStream(imageFile))));
						newImage.setText(imageFile.getName());
						if(newImage.getImage().isError()){//イメージのロード中にエラーが検出されたことを示す。
							newImage.getImage().getException().printStackTrace();
							Alert alert = alertFactory.createAlert(AlertType.ERROR,"画像の読み込みエラー",ButtonType.CLOSE);
							alert.getDialogPane().setContentText("画像の読み込みでエラーが発生しました。画像ファイルでない可能性があります。");
							alert.showAndWait();
						}else if(newImage.getImage().getHeight() == 0 || newImage.getImage().getWidth() == 0){
							Alert alert = alertFactory.createAlert(AlertType.ERROR,"画像の読み込みエラー",ButtonType.CLOSE);
							alert.getDialogPane().setContentText("読み込まれた画像のサイズが0です。画像ファイルでない可能性があります。");
							alert.showAndWait();
						}else{//エラーなし
							//初期値設定
							double h = newImage.getImage().getHeight();
							double w = newImage.getImage().getWidth();
							if(w < h){//縦長
								newImage.addParam((1-w/h)/2);//X
								newImage.addParam(0.0);//Y
								newImage.addParam(w/h);//幅
								newImage.addParam(1.0);//高さ
							}else{//横長
								newImage.addParam(0.0);//X
								newImage.addParam((1-h/w)/2);//Y
								newImage.addParam(1.0);//幅
								newImage.addParam(h/w);//高さ
							}
							Command command = new AddListItemCommand<>(customMarks.get(index).getLayers(), newImage);
							command.execute();
							urManager.push(command);
							setLayerList(customMarks.get(index));
							LayerList.getSelectionModel().selectLast();
							draw();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Alert alert = alertFactory.createAlert(AlertType.ERROR,"ファイルのエラー",ButtonType.CLOSE);
						alert.getDialogPane().setContentText("選択されたファイルを開くことができませんでした。");
						alert.showAndWait();
					}
				}
			}
		});
		LayerList.setItems(LayerListOb);
		LayerList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			System.out.println("LayerList selected "+indexL);
			if(indexM != -1 && indexL != -1){
				setParameters(customMarks.get(indexM).getLayers().get(indexL));
			}
		});
		LayerList.setOnMouseClicked((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			System.out.println("LayerList selected on mouse"+indexL);
			if(indexM != -1 && indexL != -1){
				setParameters(customMarks.get(indexM).getLayers().get(indexL));
			}
		});
		LayerDelete.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1){
				Command command = new RemoveListItemCommand<>(customMarks.get(indexM).getLayers(), indexL);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(indexM));
				if(customMarks.get(indexM).getLayers().size() == 0){
					paramColor.setDisable(true);
					paramDraw.setDisable(true);
					setNumericParams(null, new String[0]);
				}else if(customMarks.get(indexM).getLayers().size() == indexL){
					LayerList.getSelectionModel().selectLast();
				}else{
					LayerList.getSelectionModel().select(indexL);
				}
			}
		});
		Layer_UP.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL > 0){//indexLが0だとコレは意味を持たない
				Command command = new SwapListItemUpCommand<>(customMarks.get(indexM).getLayers(), indexL);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(indexM));
				LayerList.getSelectionModel().select(indexL - 1);
				draw();
			}
		});
		Layer_DOWN.setOnAction((ActionEvent) ->{
			int indexM = MarkList.getSelectionModel().getSelectedIndex();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if(indexM != -1 && indexL != -1 && indexL != customMarks.get(indexM).getLayers().size() - 1){
				//indexLが最後だとコレは意味を持たない
				Command command = new SwapListItemDownCommand<>(customMarks.get(indexM).getLayers(), indexL);
				command.execute();
				urManager.push(command);
				setLayerList(customMarks.get(indexM));
				LayerList.getSelectionModel().select(indexL + 1);
				draw();
			}
		});
		rotateMark.setOnAction((ActionEvent)->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				BooleanProperty property = customMarks.get(index).getRotateProperty();
				Command command = new ValueSetCommand<>(property, rotateMark.isSelected());
				command.execute();
				urManager.push(command);
			}
		});
		Undo.setOnAction((ActionEvent)->{
			int layerListSelectedIndex = LayerList.getSelectionModel().getSelectedIndex();
			urManager.undo();
			if(MarkList.getSelectionModel().getSelectedItem() == null){
				MarkList.getSelectionModel().selectFirst();
			}else{
				setLayerList(MarkList.getSelectionModel().getSelectedItem());
				if(layerListSelectedIndex != -1 && layerListSelectedIndex < 
						MarkList.getSelectionModel().getSelectedItem().getLayers().size()){
					LayerList.getSelectionModel().select(layerListSelectedIndex);
				}else{
					LayerList.getSelectionModel().selectFirst();
				}
			}
			draw();
		});
		Redo.setOnAction((ActionEvent)->{
			int layerListSelectedIndex = LayerList.getSelectionModel().getSelectedIndex();
			urManager.redo();
			if(MarkList.getSelectionModel().getSelectedItem() == null){
				MarkList.getSelectionModel().selectFirst();
			}else{
				setLayerList(MarkList.getSelectionModel().getSelectedItem());
				if(layerListSelectedIndex != -1 && layerListSelectedIndex < 
						MarkList.getSelectionModel().getSelectedItem().getLayers().size()){
					LayerList.getSelectionModel().select(layerListSelectedIndex);
				}else{
					LayerList.getSelectionModel().selectFirst();
				}
			}
			draw();
		});
		urManager.getUndoableProperty().addListener((observable, oldValue, newValue) -> {
			Undo.setDisable(! urManager.getUndoableProperty().get());
		});
		urManager.getRedoableProperty().addListener((observable, oldValue, newValue) -> {
			Redo.setDisable(! urManager.getRedoableProperty().get());
		});
	}
	
	public void setObject(Stage stage,ObservableList<StopMark> mm){
		this.customMarks = mm;
		MarkList.setItems(customMarks);//これをしないとObservableListからListViewに変更通知が行きません。
		this.stage = stage;
	}
	
	public static void markDraw(GraphicsContext gc, StopMark mark, double size, double[] coordinate, double theta){//主に実際の路線図上での描画用
		actMarkDraw(gc, mark, size, coordinate, theta);
	}
	public static void markDraw(GraphicsContext gc, StopMark mark, double prevSize){//主にプレビュー画面での描画用
		gc.clearRect(0, 0, prevSize, prevSize);//はじめに全領域消去
		double[] coordinate = {prevSize/2,prevSize/2};
		actMarkDraw(gc, mark, prevSize, coordinate, 0);
	}
	private static void actMarkDraw(GraphicsContext gc, StopMark mark, double size, double[] coor, double theta){//実際の描画処理。
		//背景処理、領域消去処理はやりません。
		gc.save();
		gc.translate(coor[0], coor[1]);
		gc.rotate(theta*180/Math.PI);
		gc.translate(size * -0.5, size * -0.5);
		for(int i = mark.getLayers().size() - 1; 0 <= i; i--){
			MarkLayer layer = mark.getLayers().get(i);//現在のレイヤー
			Double[] prevParams;//パラメーター収納に使う
			switch(layer.getType()){
			case MarkLayer.OVAL:
				prevParams = new Double[5];
				for(int k = 0; k < 5; k++){
					prevParams[k] = layer.getParam(k) * size;
				}
				if(layer.getPaint() == MarkLayer.FILL){
					gc.setFill(layer.getColor());
					gc.fillOval(prevParams[0], prevParams[1], prevParams[2], prevParams[3]);
				}
				if(layer.getPaint() == MarkLayer.STROKE){
					gc.setStroke(layer.getColor());
					gc.setLineWidth(prevParams[4]);
					gc.strokeOval(prevParams[0], prevParams[1], prevParams[2], prevParams[3]);
				}
				break;
			case MarkLayer.RECT:
				prevParams = new Double[7];
				for(int k = 0; k < 7; k++){//RECTは全てsize倍する
					prevParams[k] = layer.getParam(k) * size;
				}
				if(layer.getPaint() == MarkLayer.FILL){
					gc.setFill(layer.getColor());
					gc.fillRoundRect(prevParams[0], prevParams[1], prevParams[2], prevParams[3], prevParams[4],
							prevParams[5]);
				}
				if(layer.getPaint() == MarkLayer.STROKE){
					gc.setStroke(layer.getColor());
					gc.setLineWidth(prevParams[6]);
					gc.strokeRoundRect(prevParams[0], prevParams[1], prevParams[2], prevParams[3],
							prevParams[4],prevParams[5]);
				}
				break;
			case MarkLayer.LINE:
				prevParams = new Double[6];
				for(int k = 0; k < 5; k++){//LINEは全てsize倍する
					prevParams[k] = layer.getParam(k) * size;
				}
				prevParams[5] = layer.getParam(5);
				gc.setStroke(layer.getColor());
				gc.setLineWidth(prevParams[4]);
				gc.setLineCap(prevParams[5].intValue()==1 ? StrokeLineCap.ROUND : StrokeLineCap.SQUARE);
				gc.strokeLine(prevParams[0],prevParams[1],prevParams[2],prevParams[3]);
				break;
			case MarkLayer.ARC:
				prevParams = new Double[8];//ARCはsize倍するやつとしないやつがある。
				prevParams[0] = layer.getParam(0) * size;
				prevParams[1] = layer.getParam(1) * size;
				prevParams[2] = layer.getParam(2) * size;
				prevParams[3] = layer.getParam(3) * size;
				prevParams[4] = layer.getParam(4);
				prevParams[5] = layer.getParam(5);
				prevParams[6] = layer.getParam(6) * size;
				prevParams[7] = layer.getParam(7);
				ArcType a = ArcType.CHORD;
				if(prevParams[7].intValue() == 0) a = ArcType.CHORD;
				if(prevParams[7].intValue() == 1) a = ArcType.OPEN;
				if(prevParams[7].intValue() == 2) a = ArcType.ROUND;
				if(layer.getPaint() == MarkLayer.FILL){
					gc.setFill(layer.getColor());
					gc.fillArc(prevParams[0], prevParams[1], prevParams[2], prevParams[3],prevParams[4],
							prevParams[5], a);
				}
				if(layer.getPaint() == MarkLayer.STROKE){
					gc.setStroke(layer.getColor());
					gc.setLineWidth(prevParams[6]);
					gc.strokeArc(prevParams[0], prevParams[1], prevParams[2], prevParams[3],prevParams[4],
							prevParams[5], a);
				}
				break;
			case MarkLayer.TEXT:
				prevParams = new Double[5];//TEXTはsize倍するやつとしないやつがある。
				prevParams[0] = layer.getParam(0) * size;
				prevParams[1] = layer.getParam(1) * size;
				prevParams[2] = layer.getParam(2) * size;
				prevParams[3] = layer.getParam(3) * size;
				prevParams[4] = layer.getParam(4);
				Font font = Font.getDefault();
				if(prevParams[4].intValue() == 0) font = Font.font(layer.getFontName(), FontWeight.NORMAL, FontPosture.REGULAR,
						prevParams[2]);//NORMAL
				if(prevParams[4].intValue() == 1) font = Font.font(layer.getFontName(), FontWeight.BOLD, FontPosture.REGULAR,
						prevParams[2]);//BOLD
				if(prevParams[4].intValue() == 2) font = Font.font(layer.getFontName(), FontWeight.NORMAL, FontPosture.ITALIC,
						prevParams[2]);//ITALIC
				if(prevParams[4].intValue() == 3) font = Font.font(layer.getFontName(), FontWeight.BOLD, FontPosture.ITALIC,
						prevParams[2]);//BOLD_ITALIC
				if(layer.getPaint() == MarkLayer.FILL){
					gc.setFill(layer.getColor());
					gc.setFont(font);
					gc.fillText(layer.getText(), prevParams[0], prevParams[1]);
				}
				if(layer.getPaint() == MarkLayer.STROKE){
					gc.setStroke(layer.getColor());
					gc.setLineWidth(prevParams[3]);
					gc.setFont(font);
					gc.strokeText(layer.getText(), prevParams[0], prevParams[1]);
				}
				break;
			case MarkLayer.IMAGE:
				prevParams = new Double[4];
				for(int k = 0; k < 4; k++){//IMAGEは全てsize倍する
					prevParams[k] = layer.getParam(k) * size;
				}
				gc.drawImage(layer.getImage(), prevParams[0], prevParams[1], prevParams[2], prevParams[3]);
				break;
			}
		}
		//gc.setTransform(Math.cos(theta),-1*Math.sin(theta),Math.sin(theta),Math.cos(theta),-1*coor[0],-1*coor[1]);
		gc.restore();
	}
	void draw(){//プレビューを描画するメソッド。背景処理はやりません。
		int markIndex = MarkList.getSelectionModel().getSelectedIndex();
		if(markIndex != -1){
			markDraw(gc, customMarks.get(markIndex), this.prevSize);
			//リストも更新・・・この処理は不具合を引き起こすのであとで対策
			/*
			customMarks.add(new StopMark());
			MarkList.getSelectionModel().selectFirst();
			customMarks.remove(customMarks.size() - 1);
			MarkList.getSelectionModel().select(markIndex);
			*/
		}
	}
	void setLayerList(StopMark s){//LayerListを更新する
		LayerListOb.clear();
		for(MarkLayer l: s.getLayers()){//各MarkLayerに対して
			String fs = null;
			if(l.getPaint() == MarkLayer.FILL) fs = "Fill";
			if(l.getPaint() == MarkLayer.STROKE) fs = "Stroke";
			if(l.getType() == MarkLayer.OVAL) LayerListOb.add("円・楕円["+fs+"]");
			if(l.getType() == MarkLayer.ARC) LayerListOb.add("円弧["+fs+"]");
			if(l.getType() == MarkLayer.RECT) LayerListOb.add("長方形["+fs+"]");
			if(l.getType() == MarkLayer.POLYGON) LayerListOb.add("多角形["+fs+"]");
			if(l.getType() == MarkLayer.LINE) LayerListOb.add("直線["+fs+"]");
			if(l.getType() == MarkLayer.TEXT) LayerListOb.add("文字列["+fs+"]");
			if(l.getType() == MarkLayer.IMAGE) LayerListOb.add("画像["+l.getText()+"]");
		}
	}
	void setParameters(MarkLayer l){//各種パラメーターを設定していく。
		if(l.getType() != MarkLayer.TEXT){
			paramText.setDisable(true);
			selectFont.setDisable(true);
		}
		//↑パラメータごとに設定した方が早いゾーン。↓図形ごとに設定するゾーン
		if(l.getType() == MarkLayer.OVAL){
			paramColor.setDisable(false);
			paramColor.setValue(l.getColor());
			paramDraw.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			if(l.getPaint() == MarkLayer.FILL) paramDraw.getSelectionModel().select(0);
			if(l.getPaint() == MarkLayer.STROKE) paramDraw.getSelectionModel().select(1);
			String[] texts = {"左上X","左上Y","直径X","直径Y","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.RECT){
			paramColor.setDisable(false);
			paramColor.setValue(l.getColor());
			paramDraw.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			if(l.getPaint() == MarkLayer.FILL) paramDraw.getSelectionModel().select(0);
			if(l.getPaint() == MarkLayer.STROKE) paramDraw.getSelectionModel().select(1);
			String[] texts = {"左上X","左上Y","幅","高さ","角円幅","角円高さ","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.LINE){
			paramColor.setDisable(false);
			paramColor.setValue(l.getColor());
			paramDraw.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			paramDraw.setDisable(true);
			String[] texts = {"始点X","始点Y","終点X","終点Y","線の太さ"};
			setNumericParams(l,texts);
			paramLT.setDisable(false);
			paramLT.setText("閉じタイプ");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("SQUARE","ROUND");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(5));
		}else if(l.getType() == MarkLayer.ARC){
			paramColor.setDisable(false);
			paramColor.setValue(l.getColor());
			paramDraw.setDisable(false);
			if(l.getPaint() == MarkLayer.FILL) paramDraw.getSelectionModel().select(0);
			if(l.getPaint() == MarkLayer.STROKE) paramDraw.getSelectionModel().select(1);
			String[] texts = {"X","Y","幅","高さ","始角(°)","角大きさ","線の太さ"};
			setNumericParams(l,texts);
			paramLT.setDisable(false);
			paramLT.setText("閉じタイプ");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("CHORD","OPEN","ROUND");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(7));
		}else if(l.getType() == MarkLayer.TEXT){
			paramColor.setDisable(false);
			paramColor.setValue(l.getColor());
			paramDraw.setDisable(false);
			paramLT.setDisable(false);
			paramLT.setText("スタイル");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("REGULAR","BOLD","ITALIC","BOLD_ITALIC");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(4));
			selectFont.setDisable(false);
			paramText.setDisable(false);
			paramText.setText(l.getText());
			if(l.getPaint() == MarkLayer.FILL) paramDraw.getSelectionModel().select(0);
			if(l.getPaint() == MarkLayer.STROKE) paramDraw.getSelectionModel().select(1);
			String[] texts = {"X","Y","文字サイズ","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.IMAGE){
			paramColor.setDisable(true);
			paramDraw.setDisable(true);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			String[] texts = {"左上X","左上Y","画像幅","画像高さ"};
			setNumericParams(l,texts);
		}
	}
	void setNumericParams(MarkLayer l, String[] texts){//下のパラメーターたちのdisableパラメタを一斉に切り替え、各種代入する
		Label[] paramLs = {paramL1,paramL2,paramL3,paramL4,paramL5,paramL6,paramL7,paramL8};
		Spinner[] paramSs = {paramS1,paramS2,paramS3,paramS4,paramS5,paramS6,paramS7,paramS8};
		int i = texts.length;//定義文字配列の長さが有効パラメーターの個数と一致する。
		for(int h = 0; h < i; h++){//iまでは全てfalse
			paramLs[h].setDisable(false);
			paramLs[h].setText(texts[h]);
			paramSs[h].setDisable(false);
			if(l.getParamsProportion()[h] == true)paramSs[h].getValueFactory().setValue((int)(l.getParam(h) * prevSize));
			if(l.getParamsProportion()[h] == false)paramSs[h].getValueFactory().setValue((int)l.getParam(h));
		}
		for(int h = i; h < 8; h++){
			paramLs[h].setDisable(true);
			paramSs[h].setDisable(true);
		}
	}
	void setParamsReactions(){//Spinnerのジェネリクス縛り等の関係で全部手書きという頭悪いことしかうまくいかないのでここに隔離します。
		paramS1.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(0, oldValue, newValue);
		});
		paramS2.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(1, oldValue, newValue);
		});
		paramS3.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(2, oldValue, newValue);
		});
		paramS4.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(3, oldValue, newValue);
		});
		paramS5.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(4, oldValue, newValue);
		});
		paramS6.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(5, oldValue, newValue);
		});
		paramS7.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(6, oldValue, newValue);
		});
		paramS8.valueProperty().addListener((obs, oldValue, newValue) -> {
			parameterChanged(7, oldValue, newValue);
		});
	}

	private void parameterChanged(int index, Integer oldValue, Integer newValue) {
		int markIndex = MarkList.getSelectionModel().getSelectedIndex();
		int layerIndex = LayerList.getSelectionModel().getSelectedIndex();

		if (markIndex == -1 || layerIndex == -1) {
			return;
		}

		MarkLayer layer = customMarks.get(markIndex).getLayers().get(layerIndex);

		if (layer.getParamsProportion()[index]) {
			if (oldValue == layer.getParam(index) * prevSize) {
				Command command = new ValueSetCommand<>(layer.getParamProperty().get(index),
						oldValue.doubleValue() / prevSize, newValue.doubleValue() / prevSize);
				command.execute();
				urManager.push(command);
			}
		} else {
			if (oldValue == layer.getParam(index)) {
				Command command = new ValueSetCommand<>(layer.getParamProperty().get(index),
						oldValue.doubleValue(), newValue.doubleValue());
				command.execute();
				urManager.push(command);
			}
		}

		draw();
	}
}
