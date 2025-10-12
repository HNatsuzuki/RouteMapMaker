package RouteMapMaker.controllers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.enums.FileType;
import RouteMapMaker.commands.AddListItemCommand;
import RouteMapMaker.commands.Command;
import RouteMapMaker.commands.RemoveListItemCommand;
import RouteMapMaker.commands.ValueSetCommand;
import RouteMapMaker.commands.SwapListItemDownCommand;
import RouteMapMaker.commands.SwapListItemUpCommand;
import RouteMapMaker.listcells.MarkLayerCell;
import RouteMapMaker.listcells.StopMarkCell;
import RouteMapMaker.services.AlertService;
import RouteMapMaker.services.CustomMarkDrawer;
import RouteMapMaker.services.FileOpenDialogService;
import RouteMapMaker.services.FontSelectDialogService;
import RouteMapMaker.services.URElements;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomMarkController implements Initializable{
	final int prevSize = 40;
	private final ObservableList<StopMark> customMarks;//カスタムマークの集合
	private final ObjectProperty<StopMark> selectedMark = new SimpleObjectProperty<>();
	private final ObjectProperty<MarkLayer> selectedLayer = new SimpleObjectProperty<>();
	private GraphicsContext gc;
	private ObservableList<String> paramDrawOb = FXCollections.observableArrayList();//fill(0)かStroke(1)かのパラメーターにセット
	private URElements urManager = new URElements();//undoとredoを管理する。
	private final SelectFontFactory selectFontFactory;
	private final AlertService alert;
	private CustomMarkDrawer drawer;
	private final FileOpenDialogService fileOpenDialog;
	private final Configuration config;
	
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
	@FXML ListView<MarkLayer> LayerList;
	@FXML Button MarkAdd;
	@FXML Button MarkCopy;
	@FXML Button MarkDelete;
	@FXML Button LayerDelete;
	@FXML Button Layer_UP;
	@FXML Button Layer_DOWN;
	@FXML MenuItem Undo;
	@FXML MenuItem Redo;

	public CustomMarkController(ObservableList<StopMark> customMarks, SelectFontFactory selectFontFactory, AlertService alert, FileOpenDialogService fileOpenDialog, Configuration config) {
		this.customMarks = customMarks;
		this.selectFontFactory = selectFontFactory;
		this.alert = alert;
		this.fileOpenDialog = fileOpenDialog;
		this.config = config;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		gc = markCanvas.getGraphicsContext2D();
		drawer = new CustomMarkDrawer(gc);
		StopMarkCell cellFactory = new StopMarkCell();
		MarkList.setCellFactory(cellFactory);
		selectedMark.bind(MarkList.getSelectionModel().selectedItemProperty());
		selectedMark.addListener((observable, oldValue, newValue) -> {
			draw();

			if (newValue == null) {
				LayerList.setItems(null);
			} else {
				StopMark mark = newValue;
				LayerList.setItems(mark.getLayers());

				if (mark.getLayers().size() != 0) {
					LayerList.getSelectionModel().selectFirst();
				}

				rotateMark.setDisable(false);
				rotateMark.setSelected(mark.isRotated());
			}
		});
		MarkList.setItems(customMarks);
		MarkAdd.setOnAction((ActionEvent) ->{//空のマークを追加する。
			System.out.println("add called.");
			StopMark newMark = new StopMark();
			Command command = new AddListItemCommand<>(customMarks, newMark);
			urManager.execute(command);
			MarkList.getSelectionModel().selectLast();
		});
		MarkCopy.setOnAction((ActionEvent) ->{//マークをコピー
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				StopMark cloneMark = stopMark.clone();
				Command command = new AddListItemCommand<>(customMarks, cloneMark);
				urManager.execute(command);
			}
		});
		MarkDelete.setOnAction((ActionEvent) ->{
			int index = MarkList.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Command command = new RemoveListItemCommand<>(customMarks, index);
				urManager.execute(command);
			}
		});
		prevbgSetter.setValue(Color.BLACK);//初期値は黒。
		prevbgSetter.setOnAction((ActionEvent) ->{
			bgRect.setFill(prevbgSetter.getValue());
		});
		paramColor.setOnAction((ActionEvent) ->{
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				Command command = new ValueSetCommand<>(markLayer.getColorProperty(), paramColor.getValue());
				urManager.execute(command);
			}
			draw();
		});
		paramDrawOb.add("fill");
		paramDrawOb.add("stroke");
		paramDraw.setItems(paramDrawOb);
		paramDraw.valueProperty().addListener((obs, oldValue, newValue) -> {
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				if(paramDraw.getSelectionModel().getSelectedIndex() == 0){
					if (markLayer.getPaint() == MarkLayer.STROKE) {
						Command command = new ValueSetCommand<>(markLayer.getPaintProperty(), MarkLayer.STROKE, MarkLayer.FILL);
						urManager.execute(command);
					}
				}
				if(paramDraw.getSelectionModel().getSelectedIndex() == 1){
					if (markLayer.getPaint() == MarkLayer.FILL) {
						Command command = new ValueSetCommand<>(markLayer.getPaintProperty(), MarkLayer.FILL, MarkLayer.STROKE);
						urManager.execute(command);
					}
				}

				draw();
			}
		});

		//スピナーについての設定は配列で一気に処理する
		List<Spinner<Integer>> paramSs = List.of(paramS1, paramS2, paramS3, paramS4, paramS5, paramS6, paramS7, paramS8);
		for (int i = 0; i < paramSs.size(); i++) {
			Spinner<Integer> spinner = paramSs.get(i);
			int spinnerId = i;
			spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE));
			spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
				parameterChanged(spinnerId, oldValue, newValue);
			});
		}

		paramST.valueProperty().addListener((obs, oldValue, newValue) -> {
			MarkLayer markLayer = selectedLayer.get();

			//コレに関しては他のレイヤーからの切替時にselectIndex-1が送られる不具合がある。とりあえずif文条件で応急処置
			int selectedIndex = paramST.getSelectionModel().getSelectedIndex();
			if (markLayer != null && selectedIndex != -1) {
				if (markLayer.getType() == MarkLayer.ARC) {
					if((int)markLayer.getParam(7) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(7), (double)selectedIndex);
						urManager.execute(command);
					}
				}
				if (markLayer.getType() == MarkLayer.TEXT) {
					if((int)markLayer.getParam(4) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(4), (double)selectedIndex);
						urManager.execute(command);
					}
				}
				if (markLayer.getType() == MarkLayer.LINE) {
					if((int)markLayer.getParam(5) != paramST.getSelectionModel().getSelectedIndex()) {
						Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(5), (double)selectedIndex);
						urManager.execute(command);
					}
				}
				draw();
			}
		});
		selectFont.setOnAction((ActionEvent) ->{
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				String current = markLayer.getFontName();
				var dialog = new FontSelectDialogService(current, selectFontFactory);
				dialog.showDialog().ifPresent(newFont -> {
					if (!current.equals(newFont)) {
						Command command = new ValueSetCommand<>(markLayer.getFontNameProperty(), current, newFont);
						urManager.execute(command);
					}
				});
			}
			draw();
		});
		paramText.setOnAction((ActionEvent) ->{
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				if (markLayer.getText() != paramText.getText()) {
					Command command = new ValueSetCommand<>(markLayer.getTextProperty(), paramText.getText());
					urManager.execute(command);
				}
				draw();
			}
		});
		addOval.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer newOval = new MarkLayer(MarkLayer.OVAL);
				//初期値投入
				newOval.addParam(0.0);//左上X
				newOval.addParam(0.0);//左上Y
				newOval.addParam(1.0);//横直径
				newOval.addParam(1.0);//縦直径
				newOval.addParam(0.05);//デフォの線の太さ2/40（あくまでも相対比なのでprevSizeが変わってもここは問題ない）
				newOval.setPaint(MarkLayer.FILL);
				newOval.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(stopMark.getLayers(), newOval);
				urManager.execute(command);
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addRect.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
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
				Command command = new AddListItemCommand<>(stopMark.getLayers(), newRect);
				urManager.execute(command);
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addLine.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer newLine = new MarkLayer(MarkLayer.LINE);
				newLine.addParam(0.0);//始点X
				newLine.addParam(0.0);//始点Y
				newLine.addParam(1.0);//終点X
				newLine.addParam(1.0);//終点Y
				newLine.addParam(0.05);//lineWidth
				newLine.addParam(0);//端はSQUARE
				newLine.setColor(Color.WHITE);
				Command command = new AddListItemCommand<>(stopMark.getLayers(), newLine);
				urManager.execute(command);
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addArc.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
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
				Command command = new AddListItemCommand<>(stopMark.getLayers(), newArc);
				urManager.execute(command);
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addText.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
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
				Command command = new AddListItemCommand<>(stopMark.getLayers(), newText);
				urManager.execute(command);
				LayerList.getSelectionModel().selectLast();
				draw();
			}
		});
		addImage.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer newImage = new MarkLayer(MarkLayer.IMAGE);
				fileOpenDialog.showDialog("画像ファイルを選択してください。", config.getImageFileDir(), FileType.IMAGE)
					.ifPresent(r -> {
						File imageFile = r.getFile();
						config.setImageFileDir(imageFile.getParent());

						try {
							newImage.setImage(new Image(new BufferedInputStream(new FileInputStream(imageFile))));
							newImage.setText(imageFile.getName());
							if (newImage.getImage().isError()) {//イメージのロード中にエラーが検出されたことを示す。
								newImage.getImage().getException().printStackTrace();
								alert.showError("画像の読み込みでエラーが発生しました。画像ファイルでない可能性があります。");
							} else if (newImage.getImage().getHeight() == 0 || newImage.getImage().getWidth() == 0) {
								alert.showError("読み込まれた画像のサイズが0です。画像ファイルでない可能性があります。");
							} else {//エラーなし
								//初期値設定
								double h = newImage.getImage().getHeight();
								double w = newImage.getImage().getWidth();
								if (w < h) {//縦長
									newImage.addParam((1-w/h)/2);//X
									newImage.addParam(0.0);//Y
									newImage.addParam(w/h);//幅
									newImage.addParam(1.0);//高さ
								} else {//横長
									newImage.addParam(0.0);//X
									newImage.addParam((1-h/w)/2);//Y
									newImage.addParam(1.0);//幅
									newImage.addParam(h/w);//高さ
								}
								Command command = new AddListItemCommand<>(stopMark.getLayers(), newImage);
								urManager.execute(command);
								LayerList.getSelectionModel().selectLast();
								draw();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							alert.showError("選択されたファイルを開くことができませんでした。");
						}
				});
			}
		});
		LayerList.setCellFactory(listView -> new MarkLayerCell());
		selectedLayer.bind(LayerList.getSelectionModel().selectedItemProperty());
		selectedLayer.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				setParameters(newValue);
			}
		});
		LayerDelete.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if (stopMark != null && indexL != -1) {
				Command command = new RemoveListItemCommand<>(stopMark.getLayers(), indexL);
				urManager.execute(command);
				if (stopMark.getLayers().size() == 0) {
					paramColor.setDisable(true);
					paramDraw.setDisable(true);
					setNumericParams(null, new String[0]);
				} else if (indexL < LayerList.getItems().size()) {
					LayerList.getSelectionModel().select(indexL);
				}
			}
		});
		Layer_UP.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if (stopMark != null && indexL > 0) {//indexLが0だとコレは意味を持たない
				Command command = new SwapListItemUpCommand<>(stopMark.getLayers(), indexL);
				urManager.execute(command);
				LayerList.getSelectionModel().select(indexL - 1);
				draw();
			}
		});
		Layer_DOWN.setOnAction((ActionEvent) ->{
			StopMark stopMark = selectedMark.get();
			int indexL = LayerList.getSelectionModel().getSelectedIndex();
			if (stopMark != null && indexL != -1 && indexL != stopMark.getLayers().size() - 1) {
				//indexLが最後だとコレは意味を持たない
				Command command = new SwapListItemDownCommand<>(stopMark.getLayers(), indexL);
				urManager.execute(command);
				LayerList.getSelectionModel().select(indexL + 1);
				draw();
			}
		});
		rotateMark.setOnAction((ActionEvent)->{
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				BooleanProperty property = stopMark.getRotateProperty();
				Command command = new ValueSetCommand<>(property, rotateMark.isSelected());
				urManager.execute(command);
			}
		});
		Undo.setOnAction((ActionEvent)->{
			StopMark stopMark = selectedMark.get();
			int layerListSelectedIndex = LayerList.getSelectionModel().getSelectedIndex();
			urManager.undo();
			if (stopMark == null) {
				MarkList.getSelectionModel().selectFirst();
			} else {
				if (layerListSelectedIndex != -1 && layerListSelectedIndex < stopMark.getLayers().size()){
					LayerList.getSelectionModel().select(layerListSelectedIndex);
				} else {
					LayerList.getSelectionModel().selectFirst();
				}
			}
			draw();
		});
		Redo.setOnAction((ActionEvent)->{
			StopMark stopMark = selectedMark.get();
			int layerListSelectedIndex = LayerList.getSelectionModel().getSelectedIndex();
			urManager.redo();
			if (stopMark == null) {
				MarkList.getSelectionModel().selectFirst();
			} else {
				if (layerListSelectedIndex != -1 && layerListSelectedIndex < stopMark.getLayers().size()) {
					LayerList.getSelectionModel().select(layerListSelectedIndex);
				} else {
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
	
	void draw(){//プレビューを描画するメソッド。背景処理はやりません。
		StopMark stopMark = selectedMark.get();

		if (stopMark != null) {
			drawer.drawCustomMarkPreview(stopMark, this.prevSize);
			//リストも更新・・・この処理は不具合を引き起こすのであとで対策
			/*
			customMarks.add(new StopMark());
			MarkList.getSelectionModel().selectFirst();
			customMarks.remove(customMarks.size() - 1);
			MarkList.getSelectionModel().select(markIndex);
			*/
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
		List<Spinner<Integer>> paramSs = List.of(paramS1, paramS2, paramS3, paramS4, paramS5, paramS6, paramS7, paramS8);
		int i = texts.length;//定義文字配列の長さが有効パラメーターの個数と一致する。
		for(int h = 0; h < i; h++){//iまでは全てfalse
			paramLs[h].setDisable(false);
			paramLs[h].setText(texts[h]);
			paramSs.get(h).setDisable(false);
			if (l.getParamsProportion()[h]) {
				paramSs.get(h).getValueFactory().setValue((int)(l.getParam(h) * prevSize));
			} else {
				paramSs.get(h).getValueFactory().setValue((int)l.getParam(h));
			}
		}
		for(int h = i; h < 8; h++){
			paramLs[h].setDisable(true);
			paramSs.get(h).setDisable(true);
		}
	}

	private void parameterChanged(int index, Integer oldValue, Integer newValue) {
		MarkLayer markLayer = selectedLayer.get();

		if (markLayer == null) {
			return;
		}

		if (markLayer.getParamsProportion()[index]) {
			if (oldValue == markLayer.getParam(index) * prevSize) {
				Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(index),
						oldValue.doubleValue() / prevSize, newValue.doubleValue() / prevSize);
				urManager.execute(command);
			}
		} else {
			if (oldValue == markLayer.getParam(index)) {
				Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(index),
						oldValue.doubleValue(), newValue.doubleValue());
				urManager.execute(command);
			}
		}

		draw();
	}
}
