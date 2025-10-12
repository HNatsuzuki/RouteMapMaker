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
import RouteMapMaker.models.PaintMode;
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
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomMarkController implements Initializable{
	private static final int PREVIEW_SIZE = 40;
	private final ObservableList<StopMark> customMarks;//カスタムマークの集合
	private final ObjectProperty<StopMark> selectedMark = new SimpleObjectProperty<>();
	private final ObjectProperty<MarkLayer> selectedLayer = new SimpleObjectProperty<>();
	private final IntegerProperty selectedLayerIndex = new SimpleIntegerProperty();
	private final BooleanProperty isRotated = new SimpleBooleanProperty();
	private GraphicsContext gc;
	private URElements urManager = new URElements();//undoとredoを管理する。
	private final SelectFontFactory selectFontFactory;
	private final AlertService alert;
	private CustomMarkDrawer drawer;
	private final FileOpenDialogService fileOpenDialog;
	private final Configuration config;
	
	@FXML private Canvas markCanvas;
	@FXML private Rectangle previewBackground;
	@FXML private ColorPicker previewBackgroundColorPicker;
	@FXML private ColorPicker layerColorPicker;
	@FXML private ChoiceBox<PaintMode> paintModeChoiceBox;
	@FXML private CheckBox rotateMark;
	@FXML private TextField paramText;
	@FXML private Label paramL1;
	@FXML private Label paramL2;
	@FXML private Label paramL3;
	@FXML private Label paramL4;
	@FXML private Label paramL5;
	@FXML private Label paramL6;
	@FXML private Label paramL7;
	@FXML private Label paramL8;
	@FXML private Label paramLT;
	@FXML private Spinner<Integer> paramS1;
	@FXML private Spinner<Integer> paramS2;
	@FXML private Spinner<Integer> paramS3;
	@FXML private Spinner<Integer> paramS4;
	@FXML private Spinner<Integer> paramS5;
	@FXML private Spinner<Integer> paramS6;
	@FXML private Spinner<Integer> paramS7;
	@FXML private Spinner<Integer> paramS8;
	@FXML private ChoiceBox<String> paramST;
	@FXML private Button ovalLayerAddButton;
	@FXML private Button rectangleLayerAddButton;
	@FXML private Button lineLayerAddButton;
	@FXML private Button arcLayerAddButton;
	@FXML private Button textLayerAddButton;
	@FXML private Button imageLayerAddButton;
	@FXML private Button fontSelectButton;
	@FXML private ListView<StopMark> markListView;
	@FXML private ListView<MarkLayer> layerListView;
	@FXML private Button markAddButton;
	@FXML private Button markCopyButton;
	@FXML private Button markDeleteButton;
	@FXML private Button layerDeleteButton;
	@FXML private Button layerUpButton;
	@FXML private Button layerDownButton;
	@FXML private MenuItem undoMenuItem;
	@FXML private MenuItem redoMenuItem;

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
		markListView.setCellFactory(cellFactory);
		selectedMark.bind(markListView.getSelectionModel().selectedItemProperty());
		selectedMark.addListener((observable, oldValue, newValue) -> {
			draw();

			if (oldValue != null) {
				isRotated.unbindBidirectional(oldValue.getRotateProperty());
			}

			if (newValue == null) {
				layerListView.setItems(null);
			} else {
				StopMark mark = newValue;
				layerListView.setItems(mark.getLayers());
				isRotated.bindBidirectional(mark.getRotateProperty());

				if (mark.getLayers().size() != 0) {
					layerListView.getSelectionModel().selectFirst();
				}
			}
		});
		markListView.setItems(customMarks);
		markAddButton.setOnAction((ActionEvent) ->{//空のマークを追加する。
			System.out.println("add called.");
			StopMark newMark = new StopMark();
			Command command = new AddListItemCommand<>(customMarks, newMark);
			urManager.execute(command);
			markListView.getSelectionModel().selectLast();
		});
		markCopyButton.setOnAction((ActionEvent) ->{//マークをコピー
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				StopMark cloneMark = stopMark.clone();
				Command command = new AddListItemCommand<>(customMarks, cloneMark);
				urManager.execute(command);
			}
		});
		markDeleteButton.setOnAction((ActionEvent) ->{
			int index = markListView.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Command command = new RemoveListItemCommand<>(customMarks, index);
				urManager.execute(command);
			}
		});
		previewBackgroundColorPicker.setValue(Color.BLACK);//初期値は黒。
		previewBackgroundColorPicker.setOnAction((ActionEvent) ->{
			previewBackground.setFill(previewBackgroundColorPicker.getValue());
		});
		layerColorPicker.setOnAction((ActionEvent) ->{
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				Command command = new ValueSetCommand<>(markLayer.getColorProperty(), layerColorPicker.getValue());
				urManager.execute(command);
			}
			draw();
		});
		
		ObservableList<PaintMode> paintModes = FXCollections.observableArrayList(PaintMode.values());
		paintModeChoiceBox.setItems(paintModes);
		paintModeChoiceBox.valueProperty().addListener((obs, oldValue, newValue) -> {
			MarkLayer markLayer = selectedLayer.get();

			if (markLayer != null) {
				if (markLayer.getPaintMode() != newValue) {
					Command command = new ValueSetCommand<>(markLayer.paintModeProperty(), newValue);
					urManager.execute(command);
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
		fontSelectButton.setOnAction((ActionEvent) ->{
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

		// 楕円レイヤー作成ボタン
		ovalLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer layer = MarkLayer.createOvalLayer();
				Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
				urManager.execute(command);
				layerListView.getSelectionModel().selectLast();
				draw();
			}
		});

		// 矩形レイヤー作成ボタン
		rectangleLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer layer = MarkLayer.createRectangleLayer();
				Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
				urManager.execute(command);
				layerListView.getSelectionModel().selectLast();
				draw();
			}
		});

		// 直線レイヤー作成ボタン
		lineLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer layer = MarkLayer.createLineLayer();
				Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
				urManager.execute(command);
				layerListView.getSelectionModel().selectLast();
				draw();
			}
		});

		// 円弧レイヤー作成ボタン
		arcLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer layer = MarkLayer.createArcLayer();
				Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
				urManager.execute(command);
				layerListView.getSelectionModel().selectLast();
				draw();
			}
		});

		// 文字列レイヤー作成ボタン
		textLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				MarkLayer layer = MarkLayer.createTextLayer();
				Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
				urManager.execute(command);
				layerListView.getSelectionModel().selectLast();
				draw();
			}
		});

		// 画像レイヤー
		imageLayerAddButton.setOnAction(event -> {
			StopMark stopMark = selectedMark.get();

			if (stopMark != null) {
				fileOpenDialog.showDialog("画像ファイルを選択してください。", config.getImageFileDir(), FileType.IMAGE)
					.ifPresent(r -> {
						File imageFile = r.getFile();
						config.setImageFileDir(imageFile.getParent());

						try {
							Image image = new Image(new BufferedInputStream(new FileInputStream(imageFile)));
							if (image.isError()) {
								//イメージのロード中にエラーが検出されたことを示す。
								image.getException().printStackTrace();
								alert.showError("画像の読み込みでエラーが発生しました。画像ファイルでない可能性があります。");
							} else if (image.getHeight() == 0 || image.getWidth() == 0) {
								alert.showError("読み込まれた画像のサイズが0です。画像ファイルでない可能性があります。");
							} else {
								//エラーなし
								MarkLayer layer = MarkLayer.createImageLayer(image, imageFile.getName());
								Command command = new AddListItemCommand<>(stopMark.getLayers(), layer);
								urManager.execute(command);
								layerListView.getSelectionModel().selectLast();
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

		// レイヤーリスト
		layerListView.setCellFactory(listView -> new MarkLayerCell());

		// 選択中レイヤーのインデックス
		selectedLayerIndex.bind(layerListView.getSelectionModel().selectedIndexProperty());

		// 選択中レイヤー
		selectedLayer.bind(layerListView.getSelectionModel().selectedItemProperty());
		selectedLayer.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				setParameters(newValue);
			}
		});

		// レイヤー削除ボタン
		layerDeleteButton.setOnAction(event -> deleteSelectedLayer());

		// レイヤー上へ移動ボタン
		layerUpButton.setOnAction(event -> moveUpSelectedLayer());

		// レイヤー下へ移動ボタン
		layerDownButton.setOnAction(event -> moveDownSelectedLayer());

		// マーク回転チェックボックス
		rotateMark.disableProperty().bind(Bindings.createBooleanBinding(() -> selectedMark.get() == null, selectedMark));

		// マーク回転チェック状態
		isRotated.bindBidirectional(rotateMark.selectedProperty());
		isRotated.addListener(this::isRotatedChanged);

		// 元に戻す処理
		undoMenuItem.setOnAction(event -> undo());
		undoMenuItem.disableProperty().bind(urManager.getUndoableProperty().not());

		// やり直し処理
		redoMenuItem.setOnAction(event -> redo());
		redoMenuItem.disableProperty().bind(urManager.getRedoableProperty().not());
	}

	/**
	 * 選択中のレイヤーを削除します。
	 */
	private void deleteSelectedLayer() {
		StopMark stopMark = selectedMark.get();
		int layerIndex = selectedLayerIndex.get();

		if (stopMark != null && layerIndex != -1) {
			Command command = new RemoveListItemCommand<>(stopMark.getLayers(), layerIndex);
			urManager.execute(command);

			if (stopMark.getLayers().size() == 0) {
				layerColorPicker.setDisable(true);
				paintModeChoiceBox.setDisable(true);
				setNumericParams(null, new String[0]);
			} else if (layerIndex < stopMark.getLayers().size()) {
				// 削除前のインデックスが範囲内だった場合は同じ場所を再選択する
				// ListView は要素を削除した場合直前の要素が選択状態になる
				layerListView.getSelectionModel().select(layerIndex);
			}
		}
	}

	/**
	 * 選択中のレイヤーを上へ移動します。
	 */
	private void moveUpSelectedLayer() {
		StopMark stopMark = selectedMark.get();
		int layerIndex = selectedLayerIndex.get();

		if (stopMark != null && layerIndex > 0) {
			// 最初のレイヤー以外の時に移動する
			Command command = new SwapListItemUpCommand<>(stopMark.getLayers(), layerIndex);
			urManager.execute(command);
			layerListView.getSelectionModel().select(layerIndex - 1);
			draw();
		}
	}

	/**
	 * 選択中のレイヤーを下へ移動します。
	 */
	private void moveDownSelectedLayer() {
		StopMark stopMark = selectedMark.get();
		int layerIndex = selectedLayerIndex.get();

		if (stopMark != null && layerIndex != -1 && layerIndex != stopMark.getLayers().size() - 1) {
			// 最後のレイヤー以外の時に移動する
			Command command = new SwapListItemDownCommand<>(stopMark.getLayers(), layerIndex);
			urManager.execute(command);
			layerListView.getSelectionModel().select(layerIndex + 1);
			draw();
		}
	}

	/**
	 * マーク回転変更時イベント
	 *
	 * @param observable イベント発生元
	 * @param oldValue 変更前の値
	 * @param newValue 変更後の値
	 */
	private void isRotatedChanged(ObservableValue<?> observable, Boolean oldValue, Boolean newValue) {
		StopMark stopMark = selectedMark.get();

		if (stopMark != null) {
			BooleanProperty property = stopMark.getRotateProperty();

			if (property.get() != newValue) {
				Command command = new ValueSetCommand<>(property, newValue);
				urManager.execute(command);
			}
		}
	}

	/**
	 * 元に戻す処理
	 */
	private void undo() {
		StopMark stopMark = selectedMark.get();
		int selectedLayerIndex = this.selectedLayerIndex.get();
		urManager.undo();

		if (stopMark == null) {
			markListView.getSelectionModel().selectFirst();
		} else {
			if (selectedLayerIndex != -1 && selectedLayerIndex < stopMark.getLayers().size()){
				layerListView.getSelectionModel().select(selectedLayerIndex);
			} else {
				layerListView.getSelectionModel().selectFirst();
			}
		}
		draw();
	}

	/**
	 * やり直し処理
	 */
	private void redo() {
		StopMark stopMark = selectedMark.get();
		int selectedLayerIndex = this.selectedLayerIndex.get();
		urManager.redo();

		if (stopMark == null) {
			markListView.getSelectionModel().selectFirst();
		} else {
			if (selectedLayerIndex != -1 && selectedLayerIndex < stopMark.getLayers().size()) {
				layerListView.getSelectionModel().select(selectedLayerIndex);
			} else {
				layerListView.getSelectionModel().selectFirst();
			}
		}
		draw();
	}

	void draw(){//プレビューを描画するメソッド。背景処理はやりません。
		StopMark stopMark = selectedMark.get();

		if (stopMark != null) {
			drawer.drawCustomMarkPreview(stopMark, PREVIEW_SIZE);
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
			fontSelectButton.setDisable(true);
		}
		//↑パラメータごとに設定した方が早いゾーン。↓図形ごとに設定するゾーン
		if(l.getType() == MarkLayer.OVAL){
			layerColorPicker.setDisable(false);
			layerColorPicker.setValue(l.getColor());
			paintModeChoiceBox.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			paintModeChoiceBox.getSelectionModel().select(l.getPaintMode());
			String[] texts = {"左上X","左上Y","直径X","直径Y","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.RECT){
			layerColorPicker.setDisable(false);
			layerColorPicker.setValue(l.getColor());
			paintModeChoiceBox.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			paintModeChoiceBox.getSelectionModel().select(l.getPaintMode());
			String[] texts = {"左上X","左上Y","幅","高さ","角円幅","角円高さ","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.LINE){
			layerColorPicker.setDisable(false);
			layerColorPicker.setValue(l.getColor());
			paintModeChoiceBox.setDisable(false);
			paramLT.setDisable(true);
			paramST.setDisable(true);
			paintModeChoiceBox.setDisable(true);
			String[] texts = {"始点X","始点Y","終点X","終点Y","線の太さ"};
			setNumericParams(l,texts);
			paramLT.setDisable(false);
			paramLT.setText("閉じタイプ");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("SQUARE","ROUND");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(5));
		}else if(l.getType() == MarkLayer.ARC){
			layerColorPicker.setDisable(false);
			layerColorPicker.setValue(l.getColor());
			paintModeChoiceBox.setDisable(false);
			paintModeChoiceBox.getSelectionModel().select(l.getPaintMode());
			String[] texts = {"X","Y","幅","高さ","始角(°)","角大きさ","線の太さ"};
			setNumericParams(l,texts);
			paramLT.setDisable(false);
			paramLT.setText("閉じタイプ");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("CHORD","OPEN","ROUND");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(7));
		}else if(l.getType() == MarkLayer.TEXT){
			layerColorPicker.setDisable(false);
			layerColorPicker.setValue(l.getColor());
			paintModeChoiceBox.setDisable(false);
			paramLT.setDisable(false);
			paramLT.setText("スタイル");
			paramST.setDisable(false);
			ObservableList<String> paramSTOb = FXCollections.observableArrayList("REGULAR","BOLD","ITALIC","BOLD_ITALIC");
			paramST.setItems(paramSTOb);
			paramST.getSelectionModel().select((int)l.getParam(4));
			fontSelectButton.setDisable(false);
			paramText.setDisable(false);
			paramText.setText(l.getText());
			paintModeChoiceBox.getSelectionModel().select(l.getPaintMode());
			String[] texts = {"X","Y","文字サイズ","線の太さ"};
			setNumericParams(l,texts);
		}else if(l.getType() == MarkLayer.IMAGE){
			layerColorPicker.setDisable(true);
			paintModeChoiceBox.setDisable(true);
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
				paramSs.get(h).getValueFactory().setValue((int)(l.getParam(h) * PREVIEW_SIZE));
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
			if (oldValue == markLayer.getParam(index) * PREVIEW_SIZE) {
				Command command = new ValueSetCommand<>(markLayer.getParamProperty().get(index),
						oldValue.doubleValue() / PREVIEW_SIZE, newValue.doubleValue() / PREVIEW_SIZE);
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
