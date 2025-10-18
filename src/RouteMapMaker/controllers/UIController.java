package RouteMapMaker.controllers;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import RouteMapMaker.converters.BackgroundPropertiesConverter;
import RouteMapMaker.converters.FreeItemListPropertiesConverter;
import RouteMapMaker.converters.LineDashListPropertiesConverter;
import RouteMapMaker.converters.LineListPropertiesConverter;
import RouteMapMaker.converters.StopMarkListPropertiesConverter;
import RouteMapMaker.factories.AlertFactory;
import RouteMapMaker.factories.LineFactory;
import RouteMapMaker.factories.SceneFactory;
import RouteMapMaker.factories.SelectFontFactory;
import RouteMapMaker.commands.AddListItemCommand;
import RouteMapMaker.commands.Command;
import RouteMapMaker.commands.CompositeCommand;
import RouteMapMaker.commands.IntegrateStationCommand;
import RouteMapMaker.commands.MoveStationsCommand;
import RouteMapMaker.commands.RemoveListItemCommand;
import RouteMapMaker.commands.ScaleFreeItemsCommand;
import RouteMapMaker.commands.ScaleLineStationsCommand;
import RouteMapMaker.commands.SetBackgroundCommand;
import RouteMapMaker.commands.SetListItemCommand;
import RouteMapMaker.commands.ValueSetCommand;
import RouteMapMaker.commands.SwapListItemDownCommand;
import RouteMapMaker.commands.SwapListItemUpCommand;
import RouteMapMaker.commands.TranslateFreeItemsCommand;
import RouteMapMaker.commands.TranslateLineStationsCommand;
import RouteMapMaker.file.ErmFileReader;
import RouteMapMaker.file.ErmFileWriter;
import RouteMapMaker.file.RmmFileReader;
import RouteMapMaker.file.RmmFileWriter;
import RouteMapMaker.file.SaveData;
import RouteMapMaker.listcells.LineDashCell;
import RouteMapMaker.listcells.StopMarkCell;
import RouteMapMaker.models.Background;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.models.LineDash;
import RouteMapMaker.models.LineList;
import RouteMapMaker.models.FreeItem;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.MvSta;
import RouteMapMaker.models.Point2D;
import RouteMapMaker.models.ScaleParameters;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.Train;
import RouteMapMaker.models.TrainStop;
import RouteMapMaker.models.TranslateParameters;
import RouteMapMaker.models.enums.FileType;
import RouteMapMaker.services.AlertService;
import RouteMapMaker.services.CustomMarkEditDialogService;
import RouteMapMaker.services.ErrorReporter;
import RouteMapMaker.services.FileOpenDialogService;
import RouteMapMaker.services.FileSaveDialogService;
import RouteMapMaker.services.FontSelectDialogService;
import RouteMapMaker.services.IntegerSpinnerEventHandler;
import RouteMapMaker.services.MainURManager;
import RouteMapMaker.services.MapDrawer;
import RouteMapMaker.services.TransformDialogService;

public class UIController implements Initializable{
	
	final double EPSILON = 0.00001;//doubleでの比較用。double変数は==で比較しちゃだめです！
	private ObservableList<String> rnList = FXCollections.observableArrayList();
	private ObservableList<String> tStaListOb = FXCollections.observableArrayList();
	private ObservableList<String> snList = FXCollections.observableArrayList();
	private ObservableList<String> trainNameList = FXCollections.observableArrayList();
	private ObservableList<StopMark> markList = FXCollections.observableArrayList();//駅ごと
	private ObservableList<StopMark> trainMarkList = FXCollections.observableArrayList();//経路ごと
	private final LineList lineList = new LineList();
	private Line line; //現在選択中の路線？（RouteTableのlistenerでセットされている）
	private Station movingSt;
	private List<MvSta> movingStList = new ArrayList<>();
	private FreeItem movingItem = null;
	private GraphicsContext gc;
	private ToggleGroup esGroup;//どちらの編集モードかのToggleGroup
	final double canvasMargin = 200;
	private final double version = 9;//セーブファイルのバージョン。セーブファイルに完全な互換性がなくなった時に変更する。
	private final double ReleaseVersion = 16;//リリースバージョン。ユーザーへの案内用
	private File dataFile;
	private Stage mainStage;//この画面のstage。MODALにするのに使ったり
	private Background background = new Background();
	private StringProperty stationFontFamily = new SimpleStringProperty("System");//駅名に使用するフォントファミリ名
	private ObservableList<StopMark> customMarks = FXCollections.observableArrayList();//カスタム停車駅マークを保持するクラス。
	private ObservableList<FreeItem> freeItems = FXCollections.observableArrayList();//自由挿入テキスト、画像を保持するクラス。
	private final Configuration config;
	private Stage configStage;
	private boolean configWindowOpened = false;//環境設定ウィンドウが既に開かれているかどうか
	private FreeItemsController fic;
	private Stage fiStage;
	private boolean fiWindowOpened = false;//freeItemウィンドウが既に開かれているかどうか
	private MainURManager urManager = MainURManager.urManager;
	private ObservableList<LineDash> lineDashes = FXCollections.observableArrayList();//ライン点線パターンを記憶。
	private Stage changeAllStage;
	private boolean changeAllWindowOpened = false;
	private boolean shortCutKeyPressed = false;//コマンドorCtrlキーが押されてるか否か
	private boolean isLoading = false; //読み込み処理でUIのlistenerが反応するため，それの処理
	private final FileOpenDialogService fileOpenDialog;
	private final FileSaveDialogService fileSaveDialog;
	private final SceneFactory sceneFactory;
	private final AlertFactory alertFactory;
	private final SelectFontFactory selectFontFactory;
	private final AlertService alert;
	private MapDrawer drawer;
	//ドラッグスタート時の座標を記録する。station座標（zoomを考慮）．
	Point2D mouseDownPoint = new Point2D(0, 0);

	@FXML AnchorPane leftPane;
	@FXML AnchorPane rightPane;
	@FXML Button RouteDelete;
	@FXML Button RouteAdd;
	@FXML Button RouteLoad;
	@FXML Button setBgImage;
	@FXML Button staRemoveRestr;
	@FXML Button staDeConnect;
	@FXML Button StationDelete;
	@FXML Button StationAdd;
	@FXML Button stationFont;
	@FXML Button tStaEdit;
	@FXML Button trainAddButton;
	@FXML Button trainDeleteButton;
	@FXML Button trainCopyButton;
	@FXML Button TT_UP;
	@FXML Button TT_DOWN;
	@FXML Button RRT_UP;
	@FXML Button RRT_DOWN;
	@FXML Canvas canvas;
	@FXML CheckBox showBackInLE;
	@FXML CheckBox staCurveConnection;
	@FXML CheckBox staNameNoShow;
	@FXML ColorPicker bgColor_CP;
	@FXML ColorPicker re_line_CP;
	@FXML ColorPicker re_mark_CP;
	@FXML ColorPicker RouteColor;
	@FXML ComboBox<StopMark> re_mark_CB;
	@FXML ComboBox<StopMark> re_staMark_CB;
	@FXML ComboBox<LineDash> re_linePattern_CB;
	@FXML ComboBox<String> re_staPStyle_CB;
	@FXML ComboBox<String> RouteStyle;
	@FXML ComboBox<String> staStyle;
	@FXML Label bgImageLabel;
	@FXML Label currentFont;
	@FXML Label mouseLocation;
	@FXML ListView<String> RouteTable;
	@FXML ListView<String> StationList;
	@FXML ListView<String> trainListView;
	@FXML ListView<String> tStaList;
	@FXML ListView<String> R_RouteTable;
	@FXML MenuBar menubar;
	@FXML MenuItem mb_new;
	@FXML MenuItem mb_open;
	@FXML MenuItem mb_save;
	@FXML MenuItem mb_saveAs;
	@FXML MenuItem mb_exportImage;
	@FXML MenuItem mb_about;
	@FXML MenuItem mb_goWiki;
	@FXML MenuItem mb_checkUpdate;
	@FXML MenuItem mb_config;
	@FXML MenuItem mb_changeAll;
	@FXML MenuItem mb_editCustomMark;
	@FXML MenuItem mb_setCustomMark;
	@FXML MenuItem mb_freeItem;
	@FXML MenuItem mb_lineDashes;
	@FXML MenuItem mb_transform;
	@FXML MenuItem mb_undo;
	@FXML MenuItem mb_redo;
	@FXML MenuItem mb_R;
	@FXML MenuItem mb_T;
	@FXML ToggleButton leftEditButton;
	@FXML ToggleButton rightEditButton;
	@FXML ToggleButton re_staPShift_TB;
	@FXML ToggleButton lineTop;
	@FXML ToggleButton lineBottom;
	@FXML ToggleButton lineRight;
	@FXML ToggleButton lineLeft;
	@FXML ToggleButton lineCenter;
	@FXML ToggleButton lineYoko;
	@FXML ToggleButton lineTate;
	@FXML ToggleGroup lineTextMuki;
	@FXML ToggleGroup lineTextLocation;
	@FXML ToggleButton staTop;
	@FXML ToggleButton staBottom;
	@FXML ToggleButton staRight;
	@FXML ToggleButton staLeft;
	@FXML ToggleButton staCenter;
	@FXML ToggleButton staYoko;
	@FXML ToggleButton staTate;
	@FXML ToggleButton staObeyLine;
	@FXML ToggleGroup staTextMuki;
	@FXML ToggleGroup staTextLocation;
	@FXML Rectangle draggedRect;
	@FXML ScrollPane canvasPane;
	@FXML Slider ZoomSlider;
	@FXML Spinner<Integer> bgImageOpacity;
	@FXML Spinner<Integer> bgImageSize;
	@FXML Spinner<Integer> bgImageX;
	@FXML Spinner<Integer> bgImageY;
	@FXML Spinner<Integer> re_line_SP;
	@FXML Spinner<Integer> re_lineC_SP;
	@FXML Spinner<Integer> re_mark_SP;
	@FXML Spinner<Integer> re_lineSA_SP;
	@FXML Spinner<Integer> re_lineSB_SP;
	@FXML Spinner<Integer> re_staPSize_SP;
	@FXML Spinner<Integer> RouteSize;
	@FXML Spinner<Integer> R_nameX;
	@FXML Spinner<Integer> R_nameY;
	@FXML Spinner<Integer> re_staPX_SP;
	@FXML Spinner<Integer> re_staPY_SP;
	@FXML Spinner<Integer> re_staLAX_SP;
	@FXML Spinner<Integer> re_staLAY_SP;
	@FXML Spinner<Integer> staSize;

	public UIController(Configuration config, SceneFactory sceneFactory, AlertFactory alertFactory, FileOpenDialogService fileOpenDialog, FileSaveDialogService fileSaveDialog) {
		this.config = config;
		this.sceneFactory = sceneFactory;
		this.alertFactory = alertFactory;
		this.fileOpenDialog = fileOpenDialog;
		this.fileSaveDialog = fileSaveDialog;
		selectFontFactory = new SelectFontFactory(sceneFactory);
		alert = new AlertService(alertFactory);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		gc = canvas.getGraphicsContext2D();
		drawer = new MapDrawer(config, gc, stationFontFamily);
		drawer.initialize();

		RouteTable.setItems(rnList);
		RouteTable.setEditable(true);
		RouteTable.setCellFactory(TextFieldListCell.forListView());
		Line newLine = lineList.createAndAddLine("路線1");
		setCanvasOriginal(lineList.getMaxPoint());
		rnList.add(newLine.getName());
		StationList.setCellFactory(TextFieldListCell.forListView());
		(new Thread(){
			@Override
			public void run(){
				checkUpdate(true);
			}
		}).start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> config.save()));
		//起動時ダイアログの表示
		if(! config.getNoAlert()){
			Alert alert = alertFactory.createAlert(AlertType.WARNING,"",ButtonType.CLOSE);
			alert.getDialogPane().setHeaderText("路線図メーカー使用上の注意・お願い");
			Text text = new Text("本ソフトウェアの使用にあたり以下の3つをお願いしています。\n"
					+ "1.不具合によるデータの破損などに十分注意してください。\n"
					+ "2.随時アップデートを配信しますのでアップデートを確認し、インストールしてください。\n"
					+ "3.不具合を発見した時は開発者へバグ報告をしてください。（Twitter @himeshi_hob にお願いします。）\n\n"
					+ "開発者の情報、ライセンスなどはHelp→Aboutを参照してください。");
			CheckBox box = new CheckBox("次からこのダイアログを表示しない");
			VBox vbox = new VBox(10.0,text,box);
			alert.getDialogPane().setContent(vbox);
			alert.showAndWait();
			config.setNoAlert(box.isSelected());
		}
		selectSomething(true);
		lineDraw();
		line = lineList.get(0);
		RouteAdd.setOnAction((ActionEvent) ->{
			createNewLine(null);
			lineDraw();
		});
		RouteDelete.setOnAction((ActionEvent) ->{
			int index = RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Command command = new RemoveListItemCommand<>(lineList, index);
				urManager.execute(command);
				rnList.clear();
				for(int i=0; i < lineList.size(); i++){
					rnList.add(lineList.get(i).getName());
				}
				lineDraw();
			}
		});
		RouteLoad.setOnAction((ActionEvent) ->{
			// 駅名が書かれたファイルを選択
			ArrayList<String> staNames = new ArrayList<String>();

			fileOpenDialog.showDialog("ファイルを開く", config.getTextFileDir(), FileType.TXT)
				.filter(r -> r.getFileType() == FileType.TXT)
				.ifPresent(r -> {
					try {
						var selectedFile = r.getFile();
						config.setTextFileDir(selectedFile.getParent());
						BufferedReader br = new BufferedReader(new FileReader(selectedFile));
						String line = br.readLine();
						while(line != null) {
							staNames.add(line);
							line = br.readLine();
						}
						br.close();
						createNewLine(staNames);
						lineDraw();
					} catch (IOException e) {
						alert.showError("エラーが発生しました。ファイルを読み込めません。");
					} catch (Exception e) {
						e.printStackTrace();
						alert.showError("エラーが発生しました。\n"
								+ "以下のエラーメッセージを@himeshi_hobにお知らせください。\n" + e.getLocalizedMessage());
					}
			});
		});
		//駅名文字列の向きに関するトグルボタンの設定（路線単位）
		lineTextLocation.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
				Toggle new_toggle) ->{
					int RouteIndex = RouteTable.getSelectionModel().getSelectedIndex();
					if(RouteIndex == -1){
						alert.showWarning("路線を選択してください。");

						return;
					}
					Toggle[] tlToggle = {lineRight, lineLeft, lineTop, lineBottom, lineCenter};
					int newT = Arrays.asList(tlToggle).indexOf(new_toggle);
					int oldT = Arrays.asList(tlToggle).indexOf(old_toggle);
					if(newT == -1 && oldT != -1) {
						//トグルの選択が解除されたことによるlisterの呼び出し．再選択
						lineTextLocation.selectToggle(old_toggle);
					}else if(lineList.get(RouteIndex).getNameLocation() == oldT && oldT != newT){
						//手動で操作されたことによるlistenerの呼び出し
						Command command = new ValueSetCommand<>(lineList.get(RouteIndex).getNameLocationProperty(), oldT, newT);
						urManager.execute(command);
					}
					lineDraw();
				});
		lineTextMuki.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
				Toggle new_toggle) ->{
					int RouteIndex = RouteTable.getSelectionModel().getSelectedIndex();
					if(RouteIndex == -1){
						alert.showWarning("路線を選択してください。");

						return;
					}
					if(new_toggle==null && old_toggle!=null) {
						//トグルの選択が解除されたことによるlisterの呼び出し．再選択
						lineTextMuki.selectToggle(old_toggle);
					} else if(old_toggle != null && old_toggle != new_toggle
							&& lineList.get(RouteIndex).isTategaki() == (old_toggle==lineTate)) {
						//手動で操作されたことによるlistenerの呼び出し
						boolean nt = (new_toggle==lineTate);
						Command command = new ValueSetCommand<>(lineList.get(RouteIndex).getTategakiProperty(), nt);
						urManager.execute(command);
					}
					lineDraw();
				});
		RouteTable.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends String> ov, 
				String old_val, String new_val) -> {
					//路線が選択された時の処理
					int index = RouteTable.getSelectionModel().getSelectedIndex();
					if(index == -1) {
						//index-1は何も選択されてないことを示すので処理しない。
						return;
					}
					line = lineList.get(index);
					StationList.setItems(snList);
					snList.clear();
					line.getStations().stream().forEach(s -> snList.add(s.getName())); //駅名リストの更新
					StationList.getSelectionModel().selectLast();
					StationList.setEditable(true);
					//トグルの選択と駅名表示位置設定
					Toggle[] tlToggle = {lineRight, lineLeft, lineTop, lineBottom, lineCenter};
					lineTextLocation.selectToggle(tlToggle[line.getNameLocation()]);
					lineTextMuki.selectToggle(line.isTategaki() ? lineTate : lineYoko);
					RouteSize.getValueFactory().setValue(lineList.get(index).getNameSize());//サイズ設定
					RouteStyle.getSelectionModel().select(lineList.get(index).getNameStyle());//style設定
					RouteColor.setValue(lineList.get(index).getNameColor());//色設定
				});
		RouteTable.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>(){
			@Override
			public void handle(ListView.EditEvent<String> t){
				if(! t.getNewValue().equals("")){
					if(! t.getNewValue().equals(lineList.get(t.getIndex()).getName())){
						Command command = new ValueSetCommand<>(lineList.get(t.getIndex()).getNameProperty(), t.getNewValue());
						urManager.execute(command);
					}
				}
				rnList.clear();
				for(int i=0; i < lineList.size(); i++){
					rnList.add(lineList.get(i).getName());
				}
				RouteTable.getSelectionModel().select(t.getIndex());
			}
		});
		RouteColor.setOnAction((ActionEvent) -> {
			int index = RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				Command command = new ValueSetCommand<>(lineList.get(index).getNameColorProperty(), RouteColor.getValue());
				urManager.execute(command);
				lineDraw();
			}
		});
		RouteSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,15,1));
		RouteSize.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(RouteSize));
		RouteSize.valueProperty().addListener((obs, oldVal, newVal) -> {
			int index = RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				if(oldVal.intValue() == lineList.get(index).getNameSize()) {
					Command command = new ValueSetCommand<>(lineList.get(index).getNameSizeProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				lineDraw();
			}
		});
		ObservableList<String> RouteStyle_Options = FXCollections.observableArrayList("Regular", "Italic", "Bold", "BoldItalic");
		RouteStyle.setItems(RouteStyle_Options);
		RouteStyle.valueProperty().addListener((obs, oldVal, newVal) -> {
			int index = RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				if(oldVal != null){
					if((oldVal.equals("Regular") && lineList.get(index).getNameStyle() == Line.REGULAR) ||
						(oldVal.equals("Italic") && lineList.get(index).getNameStyle() == Line.ITALIC) ||
						(oldVal.equals("Bold") && lineList.get(index).getNameStyle() == Line.BOLD) ||
						(oldVal.equals("BoldItalic") && lineList.get(index).getNameStyle() == Line.ITALIC_BOLD)) {
						Command command = new ValueSetCommand<>(lineList.get(index).getNameStyleProperty(), RouteStyle.getSelectionModel().getSelectedIndex());
						urManager.execute(command);
					}
				}

				lineDraw();
			}
		});
		stationFont.setOnAction((ActionEvent) ->{//フォントを設定。これは全路線共通です。
			String oldVal = stationFontFamily.get();
			String newVal = selectFontFamily(stationFontFamily.get());

			if (!newVal.equals(oldVal)) {
				Command command = new ValueSetCommand<>(stationFontFamily, oldVal, newVal);
				urManager.execute(command);
			}

			currentFont.setText(stationFontFamily.get());
			currentFont.setFont(Font.font(stationFontFamily.get()));
			lineDraw();
		});
		StationAdd.setOnAction((ActionEvent) ->{
			int index = StationList.getSelectionModel().getSelectedIndex();
			if(index == 0){
				alert.showWarning("駅は2番目以降に挿入してください。");
			}
			else if(line.getCurveConnection(index) && line.isCurvable(index)) {
				alert.showWarning("曲線区間に駅を挿入することはできません．");
			}
			else{
				int staNum = 0;
				while(true){
					String d = staNum + "駅";
					if (lineList.hasStationWithName(d)) {
						staNum++;
					} else {
						break;
					}
				}
				Line.Connection newCon = line.insertStation(index, new Station(staNum + "駅"));
				Command command = new AddListItemCommand<>(line.getConnections(), index, newCon);
				urManager.push(command);
				snList.clear();
				for(int i=0; i < line.getStations().size(); i++){
					snList.add(line.getStations().get(i).getName());
				}
				StationList.getSelectionModel().select(index + 1);
				lineDraw();
			}
		});
		StationDelete.setOnAction((ActionEvent) ->{
			int index = StationList.getSelectionModel().getSelectedIndex();
			if(index == 0 || index == line.getStations().size() - 1){
				//削除は受け付けない
				alert.showWarning("始点と終点は削除できません。");
			}else if(index != -1){
				Command removeConnectionCommand = new RemoveListItemCommand<>(line.getConnections(), index);
				Station removeCandidate = line.getStations().get(index);
				List<Command> commands = new ArrayList<>();
				List<String> trainNames = new ArrayList<>();

				// 運転系統から削除対象駅を検索する
				for (int i = 0; i < line.getTrains().size(); ++i) {
					Train train = line.getTrains().get(i);

					for (int h = 0; h < train.getStops().size(); ++h) {
						if (train.getStops().get(h).getSta() == removeCandidate) {
							Command command = new RemoveListItemCommand<>(train.getStops(), h);
							commands.add(command);
							trainNames.add(train.getName());
						}
					}
				}

				if (commands.size() > 0) {
					// 運転系統に削除対象がある場合は確認ダイアログを出す
					String trainName = String.join(" ", trainNames);
					Optional<ButtonType> result = alert.showConfirmation("運転経路" + trainName + "に削除対象駅が含まれています。削除してよろしいですか？");

					if (result.get() == ButtonType.OK) {
						CompositeCommand compositeCommand = new CompositeCommand(commands);
						compositeCommand.addCommand(removeConnectionCommand);
						urManager.execute(compositeCommand);
					}
				} else {
					urManager.execute(removeConnectionCommand);
				}

				snList.clear();
				for(int i=0; i < line.getStations().size(); i++){
					snList.add(line.getStations().get(i).getName());
				}
				StationList.getSelectionModel().select(index);
				lineDraw();
			}
		});
		StationList.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>(){
			@Override
			public void handle(ListView.EditEvent<String> t){
				int indexR = RouteTable.getSelectionModel().getSelectedIndex();
				int indexS = StationList.getSelectionModel().getSelectedIndex();
				String prev = lineList.get(indexR).getStations().get(indexS).getName();//変更前の駅名
				StationList.getItems().set(t.getIndex(), t.getNewValue());
				String str = StationList.getSelectionModel().getSelectedItem();//変更しようとしてる駅名
				//途中駅でも接続することにしました。
				if(str.equals("")){
					alert.showWarning("駅名は空にはできません。\n"
							+ "中継点を設定するときは駅名大きさパラメーターを-1にしてください。");
				}else if(!str.equals(prev)){
					//同名の駅による置き換えを試みる
					if(stationConnect(indexS, indexR, str)==1) {
						//同名の駅は存在しない。駅名を書き換えるだけ。
						Command command = new ValueSetCommand<>(lineList.get(indexR).getStations().get(indexS).getNameProperty(), prev, str);
						urManager.execute(command);
					}
				}
				snList.clear();
				for(int i=0; i < line.getStations().size(); i++){
					snList.add(line.getStations().get(i).getName());
				}
				StationList.getSelectionModel().select(indexS);
				lineDraw();
			}
		});
		staObeyLine.setSelected(true);
		staObeyLine.setOnAction((ActionEvent)->{
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR == -1 || indexS == -1) {
				return;
			}
			Station s = lineList.get(indexR).getStations().get(indexS);
			if((s.getTextLocation()==Station.TEXT_UNSET)==staObeyLine.isSelected()) {
				//状態更新不要
				return;
			}
			int newLocation = staObeyLine.isSelected() ? Station.TEXT_UNSET : Station.TEXT_LEFT;
			Command command = new ValueSetCommand<>(s.getTextLocationProperty(), newLocation);
			urManager.execute(command);
			//位置指定トグルの有効/無効を切り替える
			Toggle[] stlToggles = {staLeft, staRight, staBottom, staTop, staCenter, staTate, staYoko};
			boolean obeyLine = newLocation==Station.TEXT_UNSET;
			Arrays.asList(stlToggles).forEach(t -> ((javafx.scene.Node)t).setDisable(obeyLine));
			if(!obeyLine) {
				staTextLocation.selectToggle(stlToggles[s.getTextLocation()-Station.TEXT_LEFT]);
			}
			lineDraw();
		});
		staTextLocation.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
			Toggle new_toggle) ->{
				int indexR = RouteTable.getSelectionModel().getSelectedIndex();
				int indexS = StationList.getSelectionModel().getSelectedIndex();
				Toggle[] stlToggles = {staLeft, staRight, staBottom, staTop, staCenter};
				int oldT = Arrays.asList(stlToggles).indexOf(old_toggle);
				int newT = Arrays.asList(stlToggles).indexOf(new_toggle);
				if(indexR == -1 || indexS == -1 || oldT == -1) {
					return;
				}
				if(newT == -1) {
					//トグルの選択が解除されたことによるlisterの呼び出し．再選択
					staTextLocation.selectToggle(old_toggle);
				} else {
					oldT += Station.TEXT_LEFT;
					newT += Station.TEXT_LEFT;
					Station sta = lineList.get(indexR).getStations().get(indexS);
					if(oldT == sta.getTextLocation()) {
						Command command = new ValueSetCommand<>(sta.getTextLocationProperty(), oldT, newT);
						urManager.execute(command);
					}
				}
				lineDraw();
			});
		staTextMuki.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
			Toggle new_toggle) ->{
				int indexR = RouteTable.getSelectionModel().getSelectedIndex();
				int indexS = StationList.getSelectionModel().getSelectedIndex();
				if(indexR == -1 || indexS == -1 || old_toggle==null) {
					return;
				}
				Station s = lineList.get(indexR).getStations().get(indexS);
				if(new_toggle==null) {
					//トグルの選択が解除されたことによるlisterの呼び出し．再選択
					staTextMuki.selectToggle(old_toggle);
				} else if(old_toggle != new_toggle && s.isTategaki() == (old_toggle==staTate)) {
					//手動で操作されたことによるlistenerの呼び出し
					boolean nt = (new_toggle==staTate);
					Command command = new ValueSetCommand<>(s.getTategakiProperty(), nt);
					urManager.execute(command);
				}
				lineDraw();
			});
		staSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1,Integer.MAX_VALUE,0,1));
		staSize.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(staSize));
		staSize.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexS != -1){
				if(lineList.get(indexR).getStations().get(indexS).getNameSize() == oldVal.intValue()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getStations().get(indexS).getNameSizeProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				lineDraw();
			}
		});
		ObservableList<String> staStyle_Options = FXCollections.observableArrayList("Regular", "Italic", "Bold", 
				"BoldItalic", "路線準拠");
		staStyle.setItems(staStyle_Options);
		staStyle.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexS != -1){
				if(lineList.get(indexR).getStations().get(indexS).getNameStyle() == staStyle_Options.indexOf(oldVal)) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getStations().get(indexS).getNameStyleProperty(),
							staStyle_Options.indexOf(oldVal), staStyle_Options.indexOf(newVal));
					urManager.execute(command);
				}

				lineDraw();
			}
		});
		staCurveConnection.setOnAction((ActionEvent)->{
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			BooleanProperty cp = lineList.get(indexR).getConnections().get(indexS).getCurve();
			Command command = new ValueSetCommand<>(cp, staCurveConnection.isSelected());
			urManager.execute(command);
			lineDraw();
		});
		staNameNoShow.setOnAction((ActionEvent)->{
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR == -1 || indexS == -1) { return; }
			Station sta = lineList.get(indexR).getStations().get(indexS);
			if(staNameNoShow.isSelected()) {
				Command command = new ValueSetCommand<>(sta.getNameSizeProperty(), -1);
				urManager.execute(command);
			} else {
				Command command = new ValueSetCommand<>(sta.getNameSizeProperty(), -1, 0);
				urManager.execute(command);
				staSize.getValueFactory().setValue(0);
			}
			staSize.setDisable(staNameNoShow.isSelected());
			lineDraw();
		});
		staRemoveRestr.setOnAction((ActionEvent)->{
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexS != -1){
				if(indexS == 0 || indexS == lineList.get(indexR).getStations().size() - 1){
					alert.showWarning("始点または終点の座標固定を解除することはできません");
				}else if(detectConnectedLine(lineList.get(indexR).getStations().get(indexS)).size() > 1){
					alert.showWarning("複数路線に所属する駅の座標固定を解除することはできません。\n"
							+ "「駅の接続解除」ボタンで駅の接続を解除できます。");
				}else{
					BooleanProperty property = lineList.get(indexR).getStations().get(indexS).getPointSetProperty();
					Command command = new ValueSetCommand<>(property, false);
					urManager.execute(command);
					lineDraw();
				}
			}
		});
		staDeConnect.setOnAction((ActionEvent)->{
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexS != -1){
				if(detectConnectedLine(lineList.get(indexR).getStations().get(indexS)).size() < 2){
					//この場合は接続を解除する意味がないのでなにもしない。
					alert.showWarning("指定された駅は他の路線と接続していません。");
				}else{
					Station oldSta = lineList.get(indexR).getStations().get(indexS);
					Station newSta = new Station("新-" + oldSta.getName());
					//以下初期設定。clone使いたいけどshiftCoorでトラブりそうなのでやめる
					newSta.setPoint(oldSta.getPoint().add(50, 50));
					newSta.setConnection(oldSta.getConnection());
					newSta.setTextLocation(oldSta.getTextLocation());
					newSta.setNameSize(oldSta.getNameSize());
					newSta.setNameStyle(oldSta.getNameStyle());
					//描画位置設定は引き継がないことにする
					List<Command> commands = new ArrayList<>();
					Command stationUpdateCommand = new ValueSetCommand<>(lineList.get(indexR).getConnections().get(indexS).getStationProperty(), newSta);
					commands.add(stationUpdateCommand);

					//交点駅が登録されている運転経路も新駅にチェンジ
					for (Train train : lineList.get(indexR).getTrains()) {
						for (int i = 0; i < train.getStops().size(); ++i) {
							if (train.getStops().get(i).getSta() == oldSta) {
								TrainStop newStop = new TrainStop(newSta);
								Command setStopCommand = new SetListItemCommand<>(train.getStops(), i, newStop);
								commands.add(setStopCommand);
							}
						}
					}

					Command command = new CompositeCommand(commands);
					urManager.execute(command);

					lineDraw();
					snList.clear();
					for(int i=0; i < lineList.get(indexR).getStations().size(); i++){
						snList.add(lineList.get(indexR).getStations().get(i).getName());
					}

					//運転経路編集ウィンドウで変更を反映させる
					int indexRR = R_RouteTable.getSelectionModel().getSelectedIndex();
					int indexT = trainListView.getSelectionModel().getSelectedIndex();
					if(indexRR != -1 && indexT != -1){
						tStaListOb.clear();
						for(TrainStop ts: lineList.get(indexRR).getTrains().get(indexT).getStops()){
							tStaListOb.add(ts.getSta().getName());
						}
					}
				}
			}
		});
		StationList.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends String> ov, 
			String old_val, String new_val) -> {
				int indexR = RouteTable.getSelectionModel().getSelectedIndex();
				int indexS = StationList.getSelectionModel().getSelectedIndex();
				if(indexR == -1 || indexS == -1){
					return;
				}
				Station s = lineList.get(indexR).getStations().get(indexS);
				staSize.getValueFactory().setValue(s.getNameSize());
				staSize.setDisable(s.getNameSize()==-1);
				staNameNoShow.setSelected(s.getNameSize()==-1);
				staStyle.getSelectionModel().select(s.getNameStyle());
				staCurveConnection.setDisable(!lineList.get(indexR).isCurvable(indexS));
				staCurveConnection.setSelected(lineList.get(indexR).getCurveConnection(indexS));
				Toggle[] stlToggles = {staLeft, staRight, staBottom, staTop, staCenter};
				//位置指定トグルの有効/無効を切り替える
				boolean obeyLine = s.getTextLocation()==Station.TEXT_UNSET;
				staObeyLine.setSelected(obeyLine);
				Arrays.asList(stlToggles).forEach(t -> ((javafx.scene.Node)t).setDisable(obeyLine));
				((javafx.scene.Node)staTate).setDisable(obeyLine);
				((javafx.scene.Node)staYoko).setDisable(obeyLine);
				staTextMuki.selectToggle(s.isTategaki() ? staTate : staYoko);
				if(!obeyLine) {
					staTextLocation.selectToggle(stlToggles[s.getTextLocation()-Station.TEXT_LEFT]);
				}
				//選択中の駅を赤点で表示する
				if(movingStList.size() < 2) {
					movingStList.clear();
					movingStList.add(new MvSta(s));
					lineDraw();
				}
			});
		
		showBackInLE.setOnAction((ActionEvent) -> {
			lineDraw();
		});
		
		bgColor_CP.setValue(background.getColor());
		bgColor_CP.setOnAction((ActionEvent) ->{
			// undo stackにpushする必要があるか？
			if(!bgColor_CP.getValue().equals(background.getColor()) || background.getImage()!=null) {
				Background prev_bg = background.clone();
				background.setColor(bgColor_CP.getValue());
				updateBackgroundComponents();
				Command command = new SetBackgroundCommand(prev_bg, background.clone(), background);
				urManager.push(command);
			}
			lineDraw();
		});
		
		setBgImage.setOnAction((ActionEvent) -> {
			fileOpenDialog.showDialog("画像ファイルを選択してください。", config.getImageFileDir(), FileType.IMAGE)
				.ifPresent(r -> {
					File imageFile = r.getFile();
					config.setImageFileDir(imageFile.getParent());

					try {
						Image im = new Image(new BufferedInputStream(new FileInputStream(imageFile)));
						if (im.isError()) { //イメージのロード中にエラーが検出されたことを示す。
							alert.showError("画像の読み込みでエラーが発生しました。画像ファイルでない可能性があります。");
							return;
						}
						Background prev_bg = background.clone();
						background.setImage(im);
						Command command = new SetBackgroundCommand(prev_bg, background.clone(), background);
						urManager.push(command);
						updateBackgroundComponents();
						lineDraw();
					} catch (Exception e) {
						e.printStackTrace();
						alert.showError("選択されたファイルを開くことができませんでした。");
					}
				});
		});
		
		bgImageX.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE,Integer.MAX_VALUE,0));
		bgImageY.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE,Integer.MAX_VALUE,0));
		bgImageSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1000,100));
		bgImageOpacity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,100,0));
		Spinner[] bgSpinners = {bgImageX, bgImageY, bgImageSize, bgImageOpacity};
		for(Spinner<Integer> spinner: bgSpinners) {
			// SpinnerEventHandlerの登録
			spinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(spinner));
			// Listenerの登録．newValの設定先以外は共通
			spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
				if(isLoading) { return; }
				Background prev_bg = background.clone();
				if(spinner==bgImageX) {background.setX(newVal);}
				else if(spinner==bgImageY) {background.setY(newVal);}
				else if(spinner==bgImageSize) {background.setZoomRatio(newVal);}
				else if(spinner==bgImageOpacity) {background.setOpacity(newVal);}
				Command command = new SetBackgroundCommand(prev_bg, background.clone(), background);
				urManager.push(command);
				lineDraw();
			});
		}
		
		draggedRect.setVisible(false);
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){//canvas上でマウスが押された時
			@Override
			public void handle(MouseEvent e){
				double x = e.getX()/drawer.getZoomRatio();
				double y = e.getY()/drawer.getZoomRatio();
				mouseDownPoint = new Point2D(x, y);
				if(esGroup.getSelectedToggle() == rightEditButton){
					movingSt = searchStation(new Point2D(x, y));
					if(movingSt == null){
						draggedRect.setVisible(true);
						draggedRect.setX(e.getX());
						draggedRect.setY(e.getY());
						draggedRect.setWidth(0);
						draggedRect.setHeight(0);
						movingStList.clear();
					}else{
						//駅移動時の開始座標は開始時のマウス座標ではなく駅座標にする。
						mouseDownPoint = movingSt.getPointUS();
						boolean contain = movingStList.stream().filter(ms -> ms.getStation()==movingSt).count()>0;
						if(contain && shortCutKeyPressed){//movingStListから選択されたものを削除する
							//ConcurrentModificationExceptionを回避するためにIteratorを使う
							Iterator<MvSta> iter = movingStList.iterator();
							while(iter.hasNext()){
								MvSta ms = iter.next();
								if(ms.getStation() == movingSt) iter.remove();
							}
						}
						if(! contain){
							if(! shortCutKeyPressed) movingStList.clear();
							movingStList.add(new MvSta(movingSt));
						}
						for(MvSta ms: movingStList){//start座標の更新
							ms.setStart(ms.getStation().getPointUS());
						}
						draggedRect.setVisible(false);
					}
					lineDraw();
				}else{//leftEditbuttonが選択されている状態
					
				}
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){//canvas上でマウスがドラッグされた時
			@Override
			public void handle(MouseEvent e){
				if(esGroup.getSelectedToggle() == rightEditButton){
					if(e.getButton()!=MouseButton.PRIMARY) {
						return;
					}
					//zoomを考慮した現在のマウス座標
					final Point2D cc = new Point2D(e.getX()/drawer.getZoomRatio(), e.getY()/drawer.getZoomRatio());
					if(movingSt != null){//特定の駅が選択されている時
						//movingSt.setPoint(e.getX(), e.getY());
						for(MvSta ms: movingStList){
							ms.getStation().setPoint(ms.getStart().add(cc).subtract(mouseDownPoint));
						}
						//領域の自動拡大
						if(canvas.getWidth() - e.getX() < canvasMargin) canvas.setWidth(e.getX() + canvasMargin);
						if(canvas.getHeight() - e.getY() < canvasMargin) canvas.setHeight(e.getY() + canvasMargin);
						lineDraw();
					}else{//特定の駅が選択されているわけではないとき
						if(e.getX() - mouseDownPoint.getX()*drawer.getZoomRatio() <= 0){//符号の反転が必要
							draggedRect.setX(e.getX());
							draggedRect.setWidth(mouseDownPoint.getX()*drawer.getZoomRatio() - e.getX());
						}else{//反転必要なし
							draggedRect.setWidth(e.getX() - mouseDownPoint.getX()*drawer.getZoomRatio());
						}
						if(e.getY() - mouseDownPoint.getY()*drawer.getZoomRatio() <= 0){
							draggedRect.setY(e.getY());
							draggedRect.setHeight(mouseDownPoint.getY()*drawer.getZoomRatio() - e.getY());
						}else{
							draggedRect.setHeight(e.getY() - mouseDownPoint.getY()*drawer.getZoomRatio());
						}
					}
				}else{//leftEditbuttonが選択されている状態
					
				}
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){//canvas上でマウスが離された時
			@Override
			public void handle(MouseEvent e){
				if(esGroup.getSelectedToggle() == rightEditButton){
					//zoomを考慮した現在のマウス座標
					final Point2D cc = new Point2D(e.getX()/drawer.getZoomRatio(), e.getY()/drawer.getZoomRatio());
					if(movingSt != null){//特定の駅が選択されている時
						if(e.getButton()!=MouseButton.PRIMARY) {
							return;
						}
						Point2D snappedPoint = snapToGrid(cc);

						for(MvSta ms: movingStList){
							ms.getStation().setPoint(ms.getStart().add(snappedPoint).subtract(mouseDownPoint));
						}
						//マウスが全く動いてないかつ全てがもともと座標固定駅だった場合はpushしてはならない
						boolean shouldBePushed = false;
						for(MvSta ms: movingStList){
							//完全にイコールにするとすごく小さな値で差がついてしまう
							if(! ms.getIsSet() || ms.getStart().distance(ms.getStation().getPoint()) > 0.5){
								shouldBePushed = true;
								break;
							}
						}
						if(shouldBePushed){
							Command command = new MoveStationsCommand(movingStList);
							urManager.push(command);
							System.out.println("mouseReleased - pushed!");
						}

						//canvasのサイズを調整する。
						double margin = canvasMargin / drawer.getZoomRatio();
						Point2D maxPoint = lineList.getMaxPoint();
						Point2D canvasMaxPoint = maxPoint.add(margin, margin);
						drawer.setCanvasSize(new Dimension2D(canvasMaxPoint.getX(), canvasMaxPoint.getY()));
						lineDraw();
					}else{//特定の駅が選択されているわけではないとき
						draggedRect.setVisible(false);
						movingStList = lineList.findStationsByArea(
							draggedRect.getX() / drawer.getZoomRatio(),
							draggedRect.getY() / drawer.getZoomRatio(),
							draggedRect.getWidth() / drawer.getZoomRatio(),
							draggedRect.getHeight()/drawer.getZoomRatio()
							).stream().map(s -> new MvSta(s)).collect(Collectors.toList());
						lineDraw();
					}
				}else{
					
				}
				
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>(){//canvas上でマウスが押された時
			@Override
			public void handle(MouseEvent e){
				mouseLocation.setText("X: "+(int)e.getX()+", Y: "+(int)e.getY());
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>(){//canvas上でマウスが押された時
			@Override
			public void handle(MouseEvent e){
				mouseLocation.setText("X: "+(int)e.getX()+", Y: "+(int)e.getY());
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>(){//canvas上でマウスが押された時
			@Override
			public void handle(MouseEvent e){
				mouseLocation.setText("");
			}
		});
		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.isShiftDown()) shortCutKeyPressed = true;
				if(! event.isShiftDown()) shortCutKeyPressed = false;
			}
		};
		canvasPane.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
		canvasPane.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
		//メニューバー項目の動作を定義。
		menubar.setUseSystemMenuBar(config.getMenubarMode());
		mb_new.setOnAction((ActionEvent) ->{
			dataFile = null;
			lineList.clear();
			customMarks.clear();
			freeItems.clear();
			rnList.clear();
			Line newLine1 = lineList.createAndAddLine("路線1");
			setCanvasOriginal(lineList.getMaxPoint());
			rnList.add(newLine1.getName());
			RouteTable.getSelectionModel().select(0);
			urManager.clear();
			lineDraw();
		});
		mb_open.setOnAction((ActionEvent) ->{
			fileOpenDialog.showDialog("ファイルを開く", config.getSaveFileDir(), FileType.RMM, FileType.ERM)
				.ifPresent(r -> {
					File selectedFile = r.getFile();
					FileType fileType = r.getFileType();
					config.setSaveFileDir(selectedFile.getParent());

					try {
						if (fileType == FileType.ERM) {
							urManager.clear();
							dataFile = selectedFile;
							readERMFile(dataFile);
						} else if (fileType == FileType.RMM) {
							urManager.clear();
							dataFile = selectedFile;
							readRMMFile(dataFile);
						}
					} catch (IOException e) {
						alert.showError("エラーが発生しました。ファイルを読み込めません。");
						dataFile = null;
					} catch(Exception e) {
						e.printStackTrace();
						alert.showError("エラーが発生しました。データファイルに不備があります。\n"
								+ "以下のエラーメッセージを@himeshi_hobにお知らせください。\n" + e.getLocalizedMessage());
						dataFile = null;
					}
				});

		});
		mb_save.setOnAction((ActionEvent) ->{
			if(dataFile == null){
				fileSaveDialog.showDialog("ファイルの保存", config.getSaveFileDir(), FileType.RMM, FileType.ERM)
					.ifPresent(r -> {
						dataFile = r.getFile();
						config.setSaveFileDir(dataFile.getParent());
					});
			}
			if(dataFile != null){
				try{
					saveRMMFile(dataFile);
					alert.showInformationAsync("保存しました。\n\n※このダイアログはenterキーで閉じます");
				}catch(IOException e){
					alert.showError("保存中にエラーが発生しました。");
					dataFile = null;
				}
			}
		});
		mb_saveAs.setOnAction((ActionEvent) ->{
			fileSaveDialog.showDialog("ファイルの保存", config.getSaveFileDir(), FileType.RMM, FileType.ERM)
				.ifPresent(r -> {
					dataFile = r.getFile();
					config.setSaveFileDir(dataFile.getParent());
				});
			if(dataFile != null){
				try{
					saveRMMFile(dataFile);
					alert.showInformationAsync("保存しました。\n\n※このダイアログはenterキーで閉じます");
				}catch(IOException e){
					alert.showError("保存中にエラーが発生しました。");
					dataFile = null;
				}
			}
		});
		mb_exportImage.setOnAction((ActionEvent) ->{
			exportImage();
		});
		mb_about.setOnAction((ActionEvent) ->{
			Alert alert = alertFactory.createAlert(AlertType.NONE,"",ButtonType.CLOSE);
			alert.getDialogPane().setHeaderText("バージョン情報");
			alert.getDialogPane().setContentText("version "+ ReleaseVersion +"　Release：2020年11月28日\n"
					+ "使い方の参照、不具合報告等はwikiで行うことができます。\n"
					+ "不具合を発見された際はwikiもしくはTwitterでの報告にご協力をお願いします。\n\n"
					+ "お問い合わせ：@himeshi_hob（Twitter）\n2017-2020 ひめし \nCreativeCommons 表示-非営利4.0国際ライセンスに従います。"
					+ "ライセンスについては付属のReadMeを参照してください。");
			alert.show();
		});
		mb_goWiki.setOnAction((ActionEvent) ->{
			Desktop desktop = Desktop.getDesktop();
			URI uu;
			try {
				uu = new URI("http://wikiwiki.jp/routemapmake/");
				desktop.browse(uu);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		mb_checkUpdate.setOnAction((ActionEvent) ->{
			boolean b = checkUpdate(false);
			if(! b){
				alert.showInformation("このバージョンは最新版です。");
			}
			
		});
		mb_config.setOnAction((ActionEvent) ->{
			if(! configWindowOpened){
				ConfigUIController configEuc = null;
				FXMLLoader editLoader = null;
				configStage = new Stage();
				VBox ap = null;
				try {
					editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/ConfigUIController.fxml"));
					editLoader.setControllerFactory(param -> {
						if (param == ConfigUIController.class) {
							return new ConfigUIController(config, selectFontFactory);
						} else {
							throw new RuntimeException();
						}
					});
					ap= (VBox)editLoader.load();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				configEuc = (ConfigUIController)editLoader.getController();
				Scene sc = sceneFactory.createScene(ap);
				configStage.setScene(sc);
				configStage.setTitle("環境設定");
				configWindowOpened = true;
				configStage.showAndWait();
				configWindowOpened = false;
			}else{
				configStage.toFront();
			}
		});
		mb_changeAll.setOnAction((ActionEvent) ->{
			if(! changeAllWindowOpened){
				ChangeAllController caEuc = null;
				FXMLLoader editLoader = null;
				changeAllStage = new Stage();
				VBox ap = null;
				try {
					editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/ChangeAllController.fxml"));
					editLoader.setControllerFactory(param -> {
						if (param == ChangeAllController.class) {
							return new ChangeAllController(alert);
						} else {
							throw new RuntimeException();
						}
					});
					ap= (VBox)editLoader.load();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				caEuc = editLoader.getController();
				caEuc.setObject(lineList, customMarks, lineDashes, this);
				Scene sc = sceneFactory.createScene(ap);
				changeAllStage.setScene(sc);
				changeAllStage.setTitle("パラメーター 一括変更");
				changeAllWindowOpened = true;
				changeAllStage.showAndWait();
				changeAllWindowOpened = false;
			}else{
				changeAllStage.toFront();
			}
		});

		// カスタム停車マーク編集メニュー
		mb_editCustomMark.setOnAction(actionEvent -> {
			var dialog = new CustomMarkEditDialogService(customMarks, sceneFactory, selectFontFactory, alert, fileOpenDialog, config);
			dialog.showDialog();
			//マークリストを更新
			setMarkList();
		});
		mb_setCustomMark.setOnAction((ActionEvent) ->{
			SetMarkController euc = null;
			FXMLLoader editLoader = null;
			Stage editStage = new Stage();
			editStage.initModality(Modality.APPLICATION_MODAL);
			VBox ap = null;
			try {
				editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/SetMarkController.fxml"));
				ap= (VBox)editLoader.load();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			euc = (SetMarkController)editLoader.getController();
			euc.setObject(lineList, customMarks);
			Scene sc = sceneFactory.createScene(ap);
			editStage.setScene(sc);
			editStage.setTitle("停車マークの一括設定");
			editStage.showAndWait();
			//運転経路編集モードなら再描画
			if(esGroup.getSelectedToggle() == leftEditButton) mapDraw();
		});
		fic = new FreeItemsController(freeItems, this, alert, fileOpenDialog, config);//コントローラーの初期化
		mb_freeItem.setOnAction((ActionEvent e) ->{
			//ショートカットキーを使って起動するとウィンドウを閉じてももう一度開く問題がある。
			if(fiWindowOpened){
				fiStage.toFront();
			}else{
				FXMLLoader editLoader = null;
				fiStage = new Stage();
				fiStage.initModality(Modality.NONE);
				VBox ap = null;
				try {
					editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/FreeItemsController.fxml"));
					editLoader.setController(fic);
					ap= (VBox)editLoader.load();
					fic.initialize(location, resources);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Scene sc = sceneFactory.createScene(ap);
				fiStage.setScene(sc);
				fiStage.setTitle("テキスト・画像の挿入");
				fiWindowOpened = true;
				fiStage.showAndWait();
				fiWindowOpened = false;
			}
		});
		mb_lineDashes.setOnAction((ActionEvent) ->{
			LineDashesController euc = null;
			FXMLLoader editLoader = null;
			Stage editStage = new Stage();
			editStage.initModality(Modality.APPLICATION_MODAL);
			VBox ap = null;
			try {
				editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/LineDashesController.fxml"));
				editLoader.setControllerFactory(param -> {
					if (param == LineDashesController.class) {
						return new LineDashesController(alert);
					} else {
						throw new RuntimeException();
					}
				});
				ap= (VBox)editLoader.load();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			euc = editLoader.getController();
			euc.setObject(lineDashes);
			Scene sc = sceneFactory.createScene(ap);
			editStage.setScene(sc);
			editStage.setTitle("破線パターンの編集");
			editStage.showAndWait();
			//運転経路編集モードなら再描画
			if(esGroup.getSelectedToggle() == leftEditButton) mapDraw();
		});
		mb_transform.setOnAction(e -> {
			var dialog = new TransformDialogService(sceneFactory, alert, drawer.getCanvasSize());
			dialog.showDialog().ifPresent(p -> {
				if (p instanceof TranslateParameters) {
					var params = (TranslateParameters)p;
					translate(params);
				} else if (p instanceof ScaleParameters) {
					var params = (ScaleParameters)p;
					scale(params);
				}
			});
		});
		mb_undo.setOnAction((ActionEvent) ->{
			urManager.undo();
			resetParams();
			ReDraw();
		});
		mb_redo.setOnAction((ActionEvent) ->{
			urManager.redo();
			resetParams();
			ReDraw();
		});
		mb_R.setOnAction((ActionEvent) ->{
			rightEditButton.setSelected(true);
		});
		mb_T.setOnAction((ActionEvent) ->{
			leftEditButton.setSelected(true);
		});
		urManager.getUndoableProperty().addListener((observable, oldValue, newValue) -> {
			mb_undo.setDisable(! urManager.getUndoableProperty().get());
		});
		urManager.getRedoableProperty().addListener((observable, oldValue, newValue) -> {
			mb_redo.setDisable(! urManager.getRedoableProperty().get());
		});
		//以下、運転経路項目
		trainListView.setItems(trainNameList);
		trainListView.setCellFactory(TextFieldListCell.forListView());
		trainListView.setEditable(true);
		//メニューバー横にある２つのトグルボタンについて。編集領域を切り替える。
		esGroup = new ToggleGroup();
		rightEditButton.setToggleGroup(esGroup);
		leftEditButton.setToggleGroup(esGroup);
		rightEditButton.setSelected(true);
		rightPane.setVisible(true);
		leftPane.setVisible(false);
		esGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
				Toggle new_toggle) ->{
					if(esGroup.getSelectedToggle() == rightEditButton){
						rightPane.setVisible(true);
						leftPane.setVisible(false);
						selectSomething(true);
						lineDraw();
					}else if(esGroup.getSelectedToggle() == leftEditButton){
						rightPane.setVisible(false);
						leftPane.setVisible(true);
						selectSomething(false);
						mapDraw();
					}
				});
		
		// 系統追加ボタン
		trainAddButton.setOnAction(actionEvent -> addTrain());

		// 系統削除ボタン
		trainDeleteButton.setOnAction(actionEvent -> deleteTrain());

		// 系統複製ボタン
		trainCopyButton.setOnAction(actionEvent -> copyTrain());

		// 系統リスト
		trainListView.setOnEditCommit(this::trainTableEditCommit);
		TT_UP.setOnAction((ActionEvent) ->{
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT >= 1){//indexが-1と0の時は意味を持たない
				//オブジェクト入れ替え
				Command command = new SwapListItemUpCommand<>(lineList.get(indexR).getTrains(), indexT);
				urManager.execute(command);
				trainNameList.clear();
				for(int i = 0; i < lineList.get(indexR).getTrains().size(); i++){
					trainNameList.add(lineList.get(indexR).getTrains().get(i).getName());
				}
				trainListView.getSelectionModel().select(indexT - 1);
				mapDraw();
			}
		});
		TT_DOWN.setOnAction((ActionEvent) ->{
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexT != -1 && indexT != lineList.get(indexR).getTrains().size() - 1){//indexが-1と最後尾の時は意味を持たない
				//オブジェクト入れ替え
				Command command = new SwapListItemDownCommand<>(lineList.get(indexR).getTrains(), indexT);
				urManager.execute(command);
				trainNameList.clear();
				for(int i = 0; i < lineList.get(indexR).getTrains().size(); i++){
					trainNameList.add(lineList.get(indexR).getTrains().get(i).getName());
				}
				trainListView.getSelectionModel().select(indexT + 1);
				mapDraw();
			}
		});
		
		tStaList.setItems(tStaListOb);
		trainListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
			// TODO Auto-generated method stub
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != -1){
				tStaListOb.clear();
				for(int i = 0; i < lineList.get(indexR).getTrains().get(indexT).getStops().size(); i++){
					tStaListOb.add(lineList.get(indexR).getTrains().get(indexT).getStops().get(i).getSta().getName());
				}
				re_line_CP.setValue(lineList.get(indexR).getTrains().get(indexT).getLineColor());
				re_lineC_SP.getValueFactory().setValue(lineList.get(indexR).getTrains().get(indexT).getLineDistance());
				re_line_SP.getValueFactory().setValue(lineList.get(indexR).getTrains().get(indexT).getLineWidth());
				re_lineSA_SP.getValueFactory().setValue(lineList.get(indexR).getTrains().get(indexT).getEdgeA());
				re_lineSB_SP.getValueFactory().setValue(lineList.get(indexR).getTrains().get(indexT).getEdgeB());
				re_mark_CP.setValue(lineList.get(indexR).getTrains().get(indexT).getMarkColor());
				re_mark_SP.getValueFactory().setValue(lineList.get(indexR).getTrains().get(indexT).getMarkSize());
				re_mark_CB.setValue(lineList.get(indexR).getTrains().get(indexT).getMark());
				re_linePattern_CB.setValue(lineList.get(indexR).getTrains().get(indexT).getLineDash());
				selectSomething(false);
			}
		});
		tStaEdit.setOnAction((ActionEvent) ->{
			//ウィンドウがポップアップして編集する。
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != -1){
				editTrainStops(lineList.get(indexR), lineList.get(indexR).getTrains().get(indexT));
			}
		});
		//色パレットの初期設定
		re_line_CP.setOnAction((ActionEvent) ->{
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getLineColorProperty(), re_line_CP.getValue());
				urManager.execute(command);
			}
			mapDraw();
		});
		re_mark_CP.setOnAction((ActionEvent) ->{
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getMarkColorProperty(), re_mark_CP.getValue());
				urManager.execute(command);
			}
			mapDraw();
		});
		Spinner[] re_line_spinners = {re_line_SP, re_lineC_SP, re_lineSA_SP, re_lineSB_SP, re_mark_SP};
		for(Spinner<Integer> sp: re_line_spinners) {
			int min = (sp==re_line_SP || sp==re_mark_SP ? 1 : Integer.MIN_VALUE);
			int initial = (sp==re_line_SP ? 10 : sp==re_mark_SP ? 8 : 0);
			sp.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min,Integer.MAX_VALUE,initial));
			sp.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(sp));
		}
		re_line_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				if(oldVal.intValue() == lineList.get(indexR).getTrains().get(indexT).getLineWidth()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getLineWidthProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_lineC_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				if(oldVal.intValue() == lineList.get(indexR).getTrains().get(indexT).getLineDistance()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getLineDistanceProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_lineSA_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				if(lineList.get(indexR).getTrains().get(indexT).getEdgeA() == oldVal.intValue()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getEdgeAProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_lineSB_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				if(oldVal.intValue() == lineList.get(indexR).getTrains().get(indexT).getEdgeB()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getEdgeBProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_mark_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexT != - 1){
				if(oldVal.intValue() == lineList.get(indexR).getTrains().get(indexT).getMarkSize()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexT).getMarkSizeProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		trainMarkList.clear();
		for(int i = 0; i < StopMark.DefaultMarks.length; i++){
			trainMarkList.add(StopMark.DefaultMarks[i]);
		}
		re_mark_CB.setItems(trainMarkList);
		StopMarkCell tcellFactory = new StopMarkCell();
		re_mark_CB.setCellFactory(tcellFactory);
		re_mark_CB.setButtonCell(tcellFactory.call(null));
		re_mark_CB.getSelectionModel().select(1);//CIRCLEをデフォルトにする。
		re_mark_CB.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexK != -1 && indexR != -1){
				if(re_mark_CB.getValue() != null){//setMarkList()でこのComboboxの中身が変えられた時にvalueがnullの状態でリスナーがコールされる
					Train train = lineList.get(indexR).getTrains().get(indexK);
					if(train.getMark() == oldVal) {
						Command command = new ValueSetCommand<>(train.getMarkProperty(), oldVal, newVal);
						urManager.execute(command);
					}
					mapDraw();
				}
			}
		});
		initializeLineDashes();
		re_linePattern_CB.setItems(lineDashes);
		LineDashCell ldCell = new LineDashCell();
		re_linePattern_CB.setCellFactory(ldCell);
		re_linePattern_CB.setButtonCell(ldCell.call(null));
		re_linePattern_CB.getSelectionModel().select(0);
		re_linePattern_CB.valueProperty().addListener((obs, oldVal, newVal) -> {
			if(isLoading) {
				return;
			}
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexK != -1 && indexR != -1){
				if(oldVal == lineList.get(indexR).getTrains().get(indexK).getLineDash()) {
					Command command = new ValueSetCommand<>(lineList.get(indexR).getTrains().get(indexK).getLineDashProperty(), oldVal, newVal);
					urManager.execute(command);
				}
			}
			mapDraw();
		});
		R_RouteTable.setItems(rnList);
		R_RouteTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int index = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				trainNameList.clear();
				for(int i = 0; i < lineList.get(index).getTrains().size(); i++){
					trainNameList.add(lineList.get(index).getTrains().get(i).getName());
				}
				R_nameX.getValueFactory().setValue((int)lineList.get(index).getNameOffset().getX());
				R_nameY.getValueFactory().setValue((int)lineList.get(index).getNameOffset().getY());
				selectSomething(false);
			}
		});
		RRT_UP.setOnAction((ActionEvent) ->{
			int index = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(index > 0){//0と-1は意味を持たない
				Command command = new SwapListItemUpCommand<>(lineList, index);
				urManager.execute(command);
				rnList.clear();
				for(int i=0; i < lineList.size(); i++){
					rnList.add(lineList.get(i).getName());
				}
				R_RouteTable.getSelectionModel().select(index - 1);
				mapDraw();
			}
		});
		RRT_DOWN.setOnAction((ActionEvent) ->{
			int index = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1 && index != lineList.size() - 1){//0とラストは意味を持たない
				Command command = new SwapListItemDownCommand<>(lineList, index);
				urManager.execute(command);
				rnList.clear();
				for(int i=0; i < lineList.size(); i++){
					rnList.add(lineList.get(i).getName());
				}
				R_RouteTable.getSelectionModel().select(index + 1);
				mapDraw();
			}
		});
		R_nameX.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		R_nameX.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(R_nameX));
		R_nameX.valueProperty().addListener((obs, oldVal, newVal) -> {
			int index = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				if(oldVal == (int)lineList.get(index).getNameOffset().getX()) {
					Command command = new ValueSetCommand<>(lineList.get(index).getNameXProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		R_nameY.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		R_nameY.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(R_nameY));
		R_nameY.valueProperty().addListener((obs, oldVal, newVal) -> {
			int index = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(index != -1){
				if(oldVal == (int)lineList.get(index).getNameOffset().getY()) {
					Command command = new ValueSetCommand<>(lineList.get(index).getNameYProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		tStaList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexR != -1 && indexK != -1 && indexS != -1){
				TrainStop stop = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS);
				re_staPSize_SP.getValueFactory().setValue(stop.getSta().getNameSize());
				re_staPStyle_CB.getSelectionModel().select(stop.getSta().getNameStyle());
				re_staPShift_TB.setSelected(stop.getSta().shiftBasedOnStation());
				re_staPX_SP.getValueFactory().setValue((int)stop.getSta().getNameOffset().getX());
				re_staPY_SP.getValueFactory().setValue((int)stop.getSta().getNameOffset().getY());
				re_staLAX_SP.getValueFactory().setValue(stop.getShift()[0]);
				re_staLAY_SP.getValueFactory().setValue(stop.getShift()[1]);
				re_staMark_CB.setValue(stop.getMark());
			}
		});
		re_staPSize_SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, Integer.MAX_VALUE, 0, 1));
		re_staPSize_SP.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(re_staPSize_SP));
		re_staPSize_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1){
				Station station = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS).getSta();

				if(oldVal.intValue() == station.getNameSize()) {
					Command command = new ValueSetCommand<>(station.getNameSizeProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_staPStyle_CB.setItems(staStyle_Options);
		re_staPStyle_CB.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1){
				int oldT = -1;
				int newT = re_staPStyle_CB.getSelectionModel().getSelectedIndex();
				//"Regular", "Italic", "Bold", "BoldItalic", "路線準拠"
				if(oldVal != null){
					if(oldVal.equals("Regular")) oldT = Station.REGULAR;
					if(oldVal.equals("Italic")) oldT = Station.ITALIC;
					if(oldVal.equals("Bold")) oldT = Station.BOLD;
					if(oldVal.equals("BoldItalic")) oldT = Station.BOLD_ITALIC;
					if(oldVal.equals("路線準拠")) oldT = Station.STYLE_UNSET;
				}

				Station station = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS).getSta();

				if(oldT == station.getNameStyle() && newT != -1) {
					Command command = new ValueSetCommand<>(station.getNameStyleProperty(), oldT, newT);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		ToggleGroup re_staPShift_TB_TG = new ToggleGroup();
		re_staPShift_TB.setToggleGroup(re_staPShift_TB_TG);
		re_staPShift_TB_TG.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1){
				Station sta = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS).getSta();
				if(sta.shiftBasedOnStation() != re_staPShift_TB.isSelected()) {
					Command command = new ValueSetCommand<>(sta.getShiftOnStationProperty(), re_staPShift_TB.isSelected());
					urManager.execute(command);
				}
				mapDraw();
			}
		});
		re_staPX_SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		re_staPX_SP.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(re_staPX_SP));
		re_staPX_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1){
				Station sta = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS).getSta();
				if(oldVal == (int)sta.getNameOffset().getX()) {
					Command command = new ValueSetCommand<>(sta.getNameXProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_staPY_SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		re_staPY_SP.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(re_staPY_SP));
		re_staPY_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1){
				Station sta = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS).getSta();
				if(oldVal == (int)sta.getNameOffset().getY()) {
					Command command = new ValueSetCommand<>(sta.getNameYProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_staLAX_SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		re_staLAX_SP.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(re_staLAX_SP));
		re_staLAX_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1 && indexR != -1){
				TrainStop stop = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS);
				if(oldVal == stop.getShift()[0]) {
					Command command = new ValueSetCommand<>(stop.getShiftXProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		re_staLAY_SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
		re_staLAY_SP.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, new IntegerSpinnerEventHandler(re_staLAY_SP));
		re_staLAY_SP.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1 && indexR != -1){
				TrainStop stop = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS);
				if(oldVal == stop.getShift()[1]) {
					Command command = new ValueSetCommand<>(stop.getShiftYProperty(), oldVal, newVal);
					urManager.execute(command);
				}

				mapDraw();
			}
		});
		/*
		 * re_staMark_CBについて
		 * StopMark.DefaultMarksを先にインポートしてからオリジナル定義のやつを読み込んでいく。
		 */
		//markListの初期化。先にDefaultMarksを追加する。
		markList.clear();
		markList.add(StopMark.OBEY_LINE);//駅ごとの設定なのでOBEY_LINEを入れておく。
		for(int i = 0; i < StopMark.DefaultMarks.length; i++){
			markList.add(StopMark.DefaultMarks[i]);
		}
		re_staMark_CB.setItems(markList);
		StopMarkCell cellFactory = new StopMarkCell();
		re_staMark_CB.setCellFactory(cellFactory);
		re_staMark_CB.setButtonCell(cellFactory.call(null));
		re_staMark_CB.getSelectionModel().select(0);//路線準拠をデフォルトにする。
		re_staMark_CB.valueProperty().addListener((obs, oldVal, newVal) -> {
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			int indexK = trainListView.getSelectionModel().getSelectedIndex();
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexS != -1 && indexK != -1 && indexR != -1){
				if(re_staMark_CB.getValue() != null){//setMarkList()でこのComboboxの中身が変えられた時にvalueがnullの状態でリスナーがコールされる
					TrainStop trainStop = lineList.get(indexR).getTrains().get(indexK).getStops().get(indexS);
					if (oldVal == trainStop.getMark()) {
						Command command = new ValueSetCommand<>(trainStop.getMarkProperty(), oldVal, newVal);
						urManager.execute(command);
					}

					mapDraw();
				}
			}
		});
		
		ZoomSlider.setMin(-3);
		ZoomSlider.setMax(3);
		ZoomSlider.setValue(0);
		ZoomSlider.setShowTickLabels(true);
		ZoomSlider.setShowTickMarks(true);
		ZoomSlider.setMajorTickUnit(1);
		ZoomSlider.setMinorTickCount(2);
		ZoomSlider.setSnapToTicks(true);
		ZoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
			double d = ZoomSlider.getValue();
			drawer.setZoomRatio(Math.pow(2, d));
			ReDraw();
		});

		// 設定変更時イベント処理
		config.getR_gridProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getR_gridIntervalProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getR_bindToGridXProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getR_bindToGridYProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getTriangleGridProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getMenubarModeProperty().addListener((obs, oldValue, newValue) -> {
			setMenuBarMode(newValue);
		});

		config.getFixedColorProperty().addListener((obs) -> {
			ReDraw();
		});

		config.getNonFixedColorProperty().addListener((obs) -> {
			ReDraw();
		});
	}

	/**
	 * 系統追加処理
	 */
	private void addTrain() {
		int index = R_RouteTable.getSelectionModel().getSelectedIndex();

		if (index == -1)
		{
			return;
		}

		Line line = lineList.get(index);
		List<Train> trains = line.getTrains();
		Train newTrain = new Train("系統" + (trains.size() + 1));

		if (trains.size() > 0) {
			//先例があればそれに従って自動補完を行う
			Train prevTrain = trains.get(trains.size() - 1);
			newTrain.setLineDistance(prevTrain.getLineDistance() + prevTrain.getLineWidth());
			newTrain.setLineWidth(prevTrain.getLineWidth());
			newTrain.setEdgeA(prevTrain.getEdgeA());
			newTrain.setEdgeB(prevTrain.getEdgeB());
			newTrain.setMarkColor(prevTrain.getMarkColor());
			newTrain.setMarkSize(prevTrain.getMarkSize());
		}

		// 系統追加
		Command command = new AddListItemCommand<>(trains, newTrain);
		urManager.execute(command);

		// 停車駅編集処理
		editTrainStops(line, newTrain);

		// 系統リスト更新
		trainNameList.setAll(trains.stream().map(t -> t.getName()).collect(Collectors.toList()));
		trainListView.getSelectionModel().selectLast();
	}

	/**
	 * 系統削除処理
	 */
	private void deleteTrain() {
		int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
		int indexT = trainListView.getSelectionModel().getSelectedIndex();

		if (indexR != -1 && indexT != -1) {
			Command command = new RemoveListItemCommand<>(lineList.get(indexR).getTrains(), indexT);
			urManager.execute(command);
			trainNameList.remove(indexT);
		}
	}

	/**
	 * 系統複製処理
	 */
	private void copyTrain() {
		int lineIndex = R_RouteTable.getSelectionModel().getSelectedIndex();
		int trainIndex = trainListView.getSelectionModel().getSelectedIndex();

		if (lineIndex != -1 && trainIndex != -1) {
			Train src = lineList.get(lineIndex).getTrains().get(trainIndex);
			Train dest = src.clone();
			dest.setName(src.getName() + " のコピー");
			Command command = new AddListItemCommand<>(lineList.get(lineIndex).getTrains(), trainIndex + 1, dest);
			urManager.execute(command);

			// 系統リスト更新
			trainNameList.add(trainIndex + 1, dest.getName());
			trainListView.getSelectionModel().select(trainIndex + 1);
		}
	}

	/**
	 * 系統リストの編集完了時イベント
	 *
	 * @param editEvent イベント引数
	 */
	private void trainTableEditCommit(ListView.EditEvent<String> editEvent) {
		int lineIndex = R_RouteTable.getSelectionModel().getSelectedIndex();
		int trainIndex = editEvent.getIndex();
		String newTrainName = editEvent.getNewValue();

		if (newTrainName.equals("")) {
			//空白の系統名はダメです
			alert.showWarning("系統名は空白にできません。何かしら名前をつけてください。");
		} else {
			Train train = lineList.get(lineIndex).getTrains().get(trainIndex);

			if (!train.getName().equals(newTrainName)) {
				Command command = new ValueSetCommand<>(train.getNameProperty(), newTrainName);
				urManager.execute(command);
				trainListView.getItems().set(trainIndex, newTrainName);
			}
		}
	}
	
	private Object Integer(int indexS) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 平行移動を行います。
	 *
	 * @param params 平行移動のパラメータ
	 */
	private void translate(TranslateParameters params) {
		CompositeCommand commands = new CompositeCommand();
		Command translateLineStationsCommand = new TranslateLineStationsCommand(lineList, params.getX(), params.getY());
		commands.addCommand(translateLineStationsCommand);

		if (params.isTransformWithFreeItem()) {
			Command translateFreeItemsCommand = new TranslateFreeItemsCommand(freeItems, params.getX(), params.getY());
			commands.addCommand(translateFreeItemsCommand);
		}

		var canvasSize = drawer.getCanvasSize();
		Command setCanvasSizeCommand = new ValueSetCommand<>(drawer.getCanvasSizeProperty(), new Dimension2D(canvasSize.getWidth() + params.getX(), canvasSize.getHeight() + params.getY()));
		commands.addCommand(setCanvasSizeCommand);

		urManager.execute(commands);

		ReDraw();
	}

	/**
	 * 拡大縮小を行います。
	 *
	 * @param params 拡大縮小のパラメータ
	 */
	private void scale(ScaleParameters params) {
		CompositeCommand commands = new CompositeCommand();
		Command scaleLineStationsCommand = new ScaleLineStationsCommand(lineList, params.getScaleX(), params.getScaleY(), params.getPivotX(), params.getPivotY());
		commands.addCommand(scaleLineStationsCommand);

		if (params.isTransformWithFreeItem()) {
			Command scaleFreeItemsCommand = new ScaleFreeItemsCommand(freeItems, params.getScaleX(), params.getScaleY(), params.getPivotX(), params.getPivotY());
			commands.addCommand(scaleFreeItemsCommand);
		}

		var canvasSize = drawer.getCanvasSize();
		double width = (canvasSize.getWidth() - params.getPivotX()) * params.getScaleX() + params.getPivotX();
		double height = (canvasSize.getHeight() - params.getPivotY()) * params.getScaleY() + params.getPivotY();
		Command setCanvasSizeCommand = new ValueSetCommand<>(drawer.getCanvasSizeProperty(), new Dimension2D(width, height));
		commands.addCommand(setCanvasSizeCommand);

		urManager.execute(commands);
		ReDraw();
	}
	
	// 背景関連のGUIコンポーネントを更新する
	void updateBackgroundComponents() {
		// isLoadingを一時的にtrueにすることでspinnerのsetVauleに対する発火を抑える
		final boolean isl = isLoading;
		isLoading = true;
		bgColor_CP.setValue(background.getColor());
		bgImageX.getValueFactory().setValue(background.getX());
		bgImageY.getValueFactory().setValue(background.getY());
		bgImageSize.getValueFactory().setValue(background.getZoomRatio());
		bgImageOpacity.getValueFactory().setValue(background.getZoomRatio());
		bgImageX.setDisable(background.getImage()==null);
		bgImageY.setDisable(background.getImage()==null);
		bgImageSize.setDisable(background.getImage()==null);
		bgImageOpacity.setDisable(background.getImage()==null);
		isLoading = isl;
	}

	// 路線を作成し，作成されたLineを返す
	Line createNewLine(ArrayList<String> staNames) {
		Point2D point = lineList.getMaxPoint();
		Line newLine = LineFactory.create("路線" + (lineList.size() + 1), LineList.INITIAL_LINE_OFFSET_X, point.getY() + LineList.INITIAL_LINE_OFFSET_Y);

		if(lineList.size() > 0){//既に路線があった場合は入力補助として駅名色、駅名大きさ、駅名スタイルを引き継ぐ
			final Line lastLine = lineList.get(lineList.size()-1);
			newLine.setTategaki(lastLine.isTategaki());
			newLine.setNameColor(lastLine.getNameColor());
			newLine.setNameSize(lastLine.getNameSize());
			newLine.setNameStyle(lastLine.getNameStyle());
		}
		
		// staNamesが設定されている場合は駅を設定する
		if(staNames!=null) {
			// 空白などの無効な文字列を除く
			final List<String> validStaNames = staNames.stream().filter(p -> p!=null && !p.isEmpty())
					.collect(Collectors.toList());
			if(validStaNames.size() < 2) {
				// 駅名の数が足りない．
				return null;
			}
			// 駅リストを用意する
			List<Station> newLineStations = staNames.stream().map(n -> new Station(n))
					.collect(Collectors.toList());
			// 重複駅名について問い合わせる
			// dup_process = 1は問い合わせダイアログを設けるまでの応急処置
			int dup_process = 1; // 0:問い合わせ 1:すべて統合　2:すべて不統合
			for(int i=0; i<newLineStations.size(); i++) {
				final Station s = newLineStations.get(i);
				final Optional<Station> dup = lineList.findStationByName(s.getName());
				if(!dup.isPresent() || dup_process==2) {
					// 重複なし or すべて不統合 → そのまま
					continue;
				}
				else if(dup_process==1) {
					// すべて統合 → 置き換え
					newLineStations.set(i, dup.get());
				}
				else {
					// 問い合わせ
				}
			}
			newLine.setStations(newLineStations);
		}
		
		Command command = new AddListItemCommand<>(lineList, newLine);
		urManager.execute(command);
		setCanvasOriginal(lineList.getMaxPoint());
		rnList.add(newLine.getName());
		RouteTable.getSelectionModel().select(rnList.size() - 1);
		return newLine;
	}
	
	/**
	 * グリッドの交点に近い座標を取得します。
	 *
	 * @param point 元の座標
	 * @return グリッドの交点
	 */
	private Point2D snapToGrid(Point2D point) {
		if(!config.getR_grid()) {
			// グリッド非表示．グリッド補正の必要なし
			return point;
		}
		
		double x = point.getX();
		double y = point.getY();
		int interval = config.getR_gridInterval();
		if(config.isGridTriangle()) {
			// 三角形グリッド．XとY個別の固定はサポートしない．
			if(config.getR_bindToGridX()) {
				//まずy座標を確定させる
				double y_interval = interval * Math.sqrt(3) / 2;
				int idx = (int) (Math.round(point.getY() / y_interval));
				y = idx * y_interval;
				//つづいてx座標を計算する．idxが偶数か奇数かで半interval分ずれる
				double offset = (idx%2==1 ? interval/2.0 : 0);
				x = Math.round((point.getX() - offset) / interval) * interval + offset;
			}
		} else {
			//四角形グリッド
			if(config.getR_bindToGridX()){//グリッドにバインドする設定だった場合は座標の補正を行う。
				x = Math.round(point.getX() / interval) * interval;
			}
			if(config.getR_bindToGridY()){
				y = Math.round(point.getY() / interval) * interval;
			}
		}
		return new Point2D(x, y);
	}

	private void setCanvasOriginal(Point2D point) {
		//最初だけ余分に取っておいたほうがいいっぽい
		double margin = canvasMargin * 2;
		Point2D canvasMaxPoint = point.add(margin, margin);
		drawer.setCanvasSize(new Dimension2D(canvasMaxPoint.getX(), canvasMaxPoint.getY()));
	}
	
	void selectSomething(boolean b){//編集画面で何も選択されていない状態を避けるメソッド。trueを渡せば路線編集モード、falseで系統編集モード
		if(b){//路線編集モード
			int indexR = RouteTable.getSelectionModel().getSelectedIndex();
			if(indexR == -1){
				if(lineList.size() != 0){
					RouteTable.getSelectionModel().select(0);
					indexR = RouteTable.getSelectionModel().getSelectedIndex();
					StationList.setItems(snList);
					snList.clear();
					for(int i=0; i < lineList.get(indexR).getStations().size(); i++){
						snList.add(lineList.get(indexR).getStations().get(i).getName());
					}
				}
			}
			int indexS = StationList.getSelectionModel().getSelectedIndex();
			if(indexS == -1 && lineList.size() != 0){
				if(lineList.get(indexR).getStations().size() != 0){
					StationList.getSelectionModel().select(0);
					indexS = StationList.getSelectionModel().getSelectedIndex();
				}
			}
			StationList.setEditable(true);
		}else{//系統編集モード
			int indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
			if(indexR == -1){
				if(lineList.size() != 0){
					R_RouteTable.getSelectionModel().select(0);
					indexR = R_RouteTable.getSelectionModel().getSelectedIndex();
					trainNameList.clear();
					for(int i = 0; i < lineList.get(indexR).getTrains().size(); i++){
						trainNameList.add(lineList.get(indexR).getTrains().get(i).getName());
					}
				}
			}
			int indexT = trainListView.getSelectionModel().getSelectedIndex();
			if(indexT == -1 && lineList.size() != 0){
				if(lineList.get(indexR).getTrains().size() != 0){
					trainListView.getSelectionModel().select(0);
					indexT = trainListView.getSelectionModel().getSelectedIndex();
					tStaListOb.clear();
					for(int i = 0; i < lineList.get(indexR).getTrains().get(indexT).getStops().size(); i++){
						tStaListOb.add(lineList.get(indexR).getTrains().get(indexT).getStops().get(i).getSta().getName());
					}
				}
			}
			int indexS = tStaList.getSelectionModel().getSelectedIndex();
			if(indexS == -1 && lineList.size() != 0 && lineList.get(indexR).getTrains().size() != 0){
				if(lineList.get(indexR).getTrains().get(indexT).getStops().size() != 0){
					tStaList.getSelectionModel().select(0);
					indexS = tStaList.getSelectionModel().getSelectedIndex();
				}
			}
		}
	}
	void resetParams(){//パラメーターを更新する。
		int indexLR = RouteTable.getSelectionModel().getSelectedIndex();
		int sizeLR = rnList.size();
		int indexLS = StationList.getSelectionModel().getSelectedIndex();
		int sizeLS = snList.size();
		int indexRR = R_RouteTable.getSelectionModel().getSelectedIndex();
		int indexRT = trainListView.getSelectionModel().getSelectedIndex();
		int sizeRT = trainNameList.size();
		int indexRS = tStaList.getSelectionModel().getSelectedIndex();
		int sizeRS = tStaListOb.size();
		//駅名フォント設定と背景設定は駅を選択し直しても更新されないので個別に更新
		updateBackgroundComponents();
		currentFont.setText(stationFontFamily.get());
		//まずは左の枠
		rnList.clear();
		for(Line l: lineList){
			rnList.add(l.getName());
		}
		if(rnList.size() == sizeLR && indexLR != -1){
			RouteTable.getSelectionModel().select(indexLR);
			if(snList.size() == sizeLS && indexLS != -1){
				StationList.getSelectionModel().select(indexLS);
			}else{
				selectSomething(true);
			}
		}else{
			selectSomething(true);
		}
		//つづいて右枠
		if(rnList.size() == sizeLR && indexRR != -1){
			R_RouteTable.getSelectionModel().select(indexLR);
			if(trainNameList.size() == sizeRT && indexRT != -1){
				trainListView.getSelectionModel().select(indexRT);
				if(tStaListOb.size() == sizeRS && indexRS != -1){
					tStaList.getSelectionModel().select(indexRS);
				}else{
					selectSomething(false);
				}
			}else{
				selectSomething(false);
			}
		}else{
			selectSomething(false);
		}
	}
	void lineDraw(){
		if(isLoading) {
			//データ読み込み中はリスナが反応してdrawを呼ぶので，応答しない．
			return;
		}

		Dimension2D canvasSize = drawer.getZoomedCanvasSize();
		canvas.setWidth(canvasSize.getWidth());
		canvas.setHeight(canvasSize.getHeight());

		drawer.drawMapInEditMode(lineList, line, movingStList, background, showBackInLE.isSelected());
	}
	
	void mapDraw(){//leftEdit状態の時はこちらが描画される。
		if(isLoading) {
			//データ読み込み中はリスナが反応してdrawを呼ぶので，応答しない．
			return;
		}

		Dimension2D canvasSize = drawer.getZoomedCanvasSize();
		canvas.setWidth(canvasSize.getWidth());
		canvas.setHeight(canvasSize.getHeight());

		drawer.drawMapInMapMode(lineList, background, freeItems);
	}
	protected void ReDraw(){//画面を描画し直す。主に外部インスタンスから呼び出す用。
		if(esGroup.getSelectedToggle() == rightEditButton){
			lineDraw();
		}else if(esGroup.getSelectedToggle() == leftEditButton){
			mapDraw();
		}
	}
	
	//与えられた駅について他に同じ駅名をもつ駅があればそれに置き換える
	//戻り値 0->駅統合　1->駅見つからず 2->canceled
	int stationConnect(int stIndex, int lnIndex, String candName){
		//路線indexと駅indexをもらって他に同じ駅名があるかどうかを調べる。
		//candName==null -> readProp()から呼ばれた場合
		String prevName = lineList.get(lnIndex).getStations().get(stIndex).getName();
		String gst = candName==null ? prevName : candName;//サーチする駅名
		Line.Connection con = lineList.get(lnIndex).getConnections().get(stIndex); //置き換え対象connection
		for(Line l : lineList) {
			for(int i=0; i<l.getConnections().size(); i++) {
				Line.Connection c = l.getConnections().get(i);
				if(c==con) {
					//自分自身に関しては探索しない。
					continue;
				}
				//駅名が同じ and 同じ駅objectではない→結合
				if(!gst.equals(c.getStation().getName()) || c.getStation() == con.getStation()) {
					continue;
				}
				if(candName!=null) {
					Optional<ButtonType> result = alert.showConfirmation("マップ内に同じ駅名の駅があります。その駅と統合してよろしいですか？");
					if(result.get() == ButtonType.CANCEL) {
						//結合せずにキャンセル．
						return 2;
					}
				}
				if(!c.getStation().isSet()){//座標非設置点だった場合
					//接続点は座標を固定。
					c.getStation().setPoint(c.getStation().getInterPoint());
				}
				//駅オブジェクト自体を置き換えて共通化してしまう。
				//すべての路線のConnectionとTrainStopを走査し，すべての当該駅を置き換える
				List<Line.Connection> replaced_cons = lineList.stream()
					.flatMap(l_l -> l_l.getConnections().stream())
					.filter(l_c -> l_c.getStation()==con.getStation())
					.collect(Collectors.toList()); //置き換え対象connection
				List<TrainStop> replaced_stop = lineList.stream()
					.flatMap(l_l -> l_l.getTrains().stream())
					.flatMap(l_t -> l_t.getStops().stream())
					.filter(l_s -> l_s.getSta()==con.getStation())
					.collect(Collectors.toList()); //置き換え対象train stop

				Command command = new IntegrateStationCommand(replaced_cons, replaced_stop, con.getStation(), c.getStation());
				command.execute();

				if (candName != null) {
					urManager.push(command);
				}

				return 0;
			}
		}
		return 1;
	}

	Station searchStation(Point2D point){
		for(int i = 0; i < lineList.size(); i++) {
			Line line = lineList.get(i);
			for(int j = 0; j < line.getStations().size(); j++){
				Station station = line.getStation(j);
				Point2D stationPoint = station.isSet()
					? station.getPoint()
					: station.getInterPoint();

				if (stationPoint.distance(point) <= 6) {
					RouteTable.getSelectionModel().select(i);//選択処理をする
					StationList.getSelectionModel().select(j);
					return station;
				}
			}
		}
		return null;
	}

	void readERMFile(File file) throws IOException {
		try (ErmFileReader ermFileReader = new ErmFileReader(file)) {
			SaveData saveData = ermFileReader.read();
			readProp(saveData);
		}
	}
	
	void readRMMFile(File file) throws IOException{
		try (RmmFileReader rmmFileReader = new RmmFileReader(file)) {
			SaveData saveData = rmmFileReader.read();
			readProp(saveData);
		}
	}
	
	void saveRMMFile(File saveFile) throws IOException{
		final String fN = saveFile.getName();
		final boolean rmm = (fN.substring(fN.lastIndexOf(".")).equals(".rmm"));
		//mainの書き出し
		SaveData saveData = saveProp();

		try{
			if (rmm) {
				try (RmmFileWriter rmmFileWriter = new RmmFileWriter(saveFile)) {
					rmmFileWriter.write(saveData);
				}
			} else {
				try (ErmFileWriter ermFileWriter = new ErmFileWriter(saveFile)) {
					ermFileWriter.write(saveData);
				}
			}
			urManager.saveUndoStackSize(); //保存カウントを更新
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			//zos.close();
		}
	}
	void readProp(SaveData saveData) throws IOException{//各種データをセットする。
		//Propertiesを渡す方式に変更しましたので以下の処理は呼び出し元でやってもらう。
		/*
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
		Properties p = new Properties();
		p.load(isr);
		isr.close();
		*/
		Properties p = saveData.getProperties();
		Map<Integer, Image> imageMap = saveData.getImages();
		double pVersion = 0;
		try{
			pVersion = Double.parseDouble(p.getProperty("version"));
		}catch(NumberFormatException e){
			alert.showError("ファイルが不正です。読み込みできません。");
			return;
		}
		if(pVersion > version){
			alert.showError("このバージョンのファイルには対応していません。読み込みできません。" +
				"データのバージョン：" + pVersion);
			//return;
		}
		
		isLoading = true;
		
		BackgroundPropertiesConverter bgConverter = new BackgroundPropertiesConverter();
		Background background = bgConverter.fromProperties(p, imageMap, "");
		this.background.copyParams(background);

		if (bgConverter.hasError()) {
			String message = String.join(System.lineSeparator(), bgConverter.getErrorMessages());
			alert.showWarning(message);
		}

		stationFontFamily.set(p.getProperty("stationFont", "system"));
		//lineDashesを頂点とするデータ群
		lineDashes.clear();
		if(pVersion < 8){//バージョン8未満は初期化して終わり
			initializeLineDashes();
		}else{
			lineDashes.add(LineDash.SOLID);//null値は先に入れておく。
			List<LineDash> lineDashList = LineDashListPropertiesConverter.fromProperties(p, "");
			lineDashes.addAll(lineDashList);
		}
		//freeItemsを頂点とするデータ群
		freeItems.clear();
		if(pVersion >= 5){//freeItemはデータのバージョンが5以上のときのみ
			FreeItemListPropertiesConverter freeItemListPropertiesConverter = new FreeItemListPropertiesConverter();
			List<FreeItem> freeItemList = freeItemListPropertiesConverter.fromProperties(p, imageMap, "");
			freeItems.addAll(freeItemList);

			if (freeItemListPropertiesConverter.hasError()) {
				String message = String.join(System.lineSeparator(), freeItemListPropertiesConverter.getErrorMessages());
				alert.showError(message);
			}
		}
		//customMarksを頂点とするデータ群（customMarksは後で使うので先に読み込んでおく。）
		customMarks.clear();
		if(pVersion >= 4){//マークの読み込み処理はデータのバージョンが4以上のときのみ。
			StopMarkListPropertiesConverter stopMarkListPropertiesConverter = new StopMarkListPropertiesConverter();
			List<StopMark> marks = stopMarkListPropertiesConverter.fromProperties(p, imageMap, "");
			customMarks.addAll(marks);

			if (stopMarkListPropertiesConverter.hasError()) {
				String message = String.join(System.lineSeparator(), stopMarkListPropertiesConverter.getErrorMessages());
				alert.showWarning(message);
			}
		}
		setMarkList();
		//lineListを頂点とするデータ群
		lineList.clear();//lineListは全消去
		rnList.clear();
		LineListPropertiesConverter lineListPropertiesConverter = new LineListPropertiesConverter();
		List<Line> lines = lineListPropertiesConverter.fromProperties(p, pVersion, lineDashes, customMarks, "");
		lineList.addAll(lines);
		rnList.addAll(lines.stream().map(l -> l.getName()).collect(Collectors.toList()));

		if (lineListPropertiesConverter.hasError()) {
				String message =
					"データファイルに不備があります。読み込みは続行されます。\n"
					+ String.join(System.lineSeparator(), lineListPropertiesConverter.getErrorMessages());
				alert.showWarning(message);
		}

		for (int i = 0; i < lineList.size(); ++i) {
			for (int j = 0; j < lineList.get(i).getStations().size(); ++j) {
				stationConnect(j, i, null);//接続駅はオブジェクト共通化手続き
			}
		}

		//canvasの設定
		Point2D maxPoint = lineList.getMaxPoint();
		Point2D canvasMaxPoint = maxPoint.add(canvasMargin, canvasMargin);
		drawer.setCanvasSize(new Dimension2D(canvasMaxPoint.getX(), canvasMaxPoint.getY()));
		canvas.setWidth(canvasMaxPoint.getX());
		canvas.setHeight(canvasMaxPoint.getY());
		resetParams();//適切にGUIパラメータを再セット。
		rightEditButton.setSelected(true);//読み込み時は路線編集モードにする。
		isLoading = false;
		// zoomをリセット
		drawer.setZoomRatio(1);
		ZoomSlider.setValue(0);
		lineDraw();
	}
	SaveData saveProp() {//データの保存を行う。
		Properties p = new Properties();
		Map<Integer, Image> images = new HashMap<>();
		
		p.setProperty("version", String.valueOf(version));

		// 背景情報
		Properties backgroundProperties = BackgroundPropertiesConverter.toProperties(background, images, "");
		p.putAll(backgroundProperties);

		p.setProperty("stationFont", stationFontFamily.get());

		//lineListを頂点とするデータ群
		Properties linesProperties = LineListPropertiesConverter.toProperties(lineList, lineDashes, customMarks, "");
		p.putAll(linesProperties);

		//customMarksを頂点とするデータ群
		Properties customMarksProperties = StopMarkListPropertiesConverter.toProperties(customMarks, images, "");
		p.putAll(customMarksProperties);

		//freeItemsを頂点とするデータ群
		Properties freeItemsProperties = FreeItemListPropertiesConverter.toProperties(freeItems, images, "");
		p.putAll(freeItemsProperties);

		//lineDashesを頂点とするデータ群
		Properties lineDashesProperties = LineDashListPropertiesConverter.toProperties(lineDashes, "");
		p.putAll(lineDashesProperties);

		return new SaveData(p, images);
	}
	public void setObject(Stage s){//このコントローラーに渡したいデータがあればここで。
		this.mainStage = s;//結局使ってません
	}
	List<Line> detectConnectedLine(Station sta){//与えられたstationが所属するlineを全て返す
		List<Line> lines = lineList.stream().filter(l -> l.getStations().contains(sta)).collect(Collectors.toList());

		return lines;
	}
	void setMarkList(){//markListをセットする。
		markList.clear();
		trainMarkList.clear();
		markList.add(StopMark.OBEY_LINE);//駅ごとの設定なのでOBEY_LINEを入れておく。
		for(int i = 0; i < StopMark.DefaultMarks.length; i++){
			markList.add(StopMark.DefaultMarks[i]);
			trainMarkList.add(StopMark.DefaultMarks[i]);
		}
		for(StopMark m: customMarks){//カスタムマークを追加する
			markList.add(m);
			trainMarkList.add(m);
		}
	}
	void editTrainStops(Line l, Train t){//系統の停車駅編集は処理が長く色んな所で使うのでメソッド化
		FXMLLoader editLoader = null;
		Stage editStage = new Stage();
		editStage.initModality(Modality.APPLICATION_MODAL);
		editStage.initStyle(StageStyle.UNDECORATED);
		AnchorPane ap = null;
		try {
			editLoader = new FXMLLoader(getClass().getResource("/RouteMapMaker/views/TrainStopsEditView.fxml"));
			editLoader.setControllerFactory(param -> {
				if (param == TrainStopsEditController.class) {
					return new TrainStopsEditController(l.getStations(), t, alert);
				} else {
					throw new RuntimeException();
				}
			});
			ap= (AnchorPane)editLoader.load();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Scene sc = sceneFactory.createScene(ap, 600, 300);
		editStage.setScene(sc);
		editStage.setTitle("駅編集ウィンドウ");
		editStage.showAndWait();
		tStaListOb.clear();
		for(int i = 0; i < t.getStops().size(); i++){
			tStaListOb.add(t.getStops().get(i).getSta().getName());
		}
		mapDraw();
	}
	String selectFontFamily(String current){//フォント選択画面を出す。選択されたフォントファミリ名を返す。（not exactフォント名）
		//個別のテキスト挿入にも対応したいので選択されたファミリ名を直接変数に代入することはしません
		var dialog = new FontSelectDialogService(current, selectFontFactory);

		return dialog.showDialog().orElse(current);
	}
	void exportImage(){
		//新しくウィンドウを開いて何倍にするか聞く
		var canvasSize = drawer.getCanvasSize();
		Stage expStage = new Stage();
		expStage.initModality(Modality.APPLICATION_MODAL);
		VBox expBox = new VBox();
		Label l1 = new Label("出力するイメージの大きさを設定してください（現在の倍率：200%）");
		Label l2 = new Label("元のサイズ：縦" + canvasSize.getHeight() + "、横" + canvasSize.getWidth());
		Label l3 = new Label("出力サイズ：縦" + canvasSize.getHeight() * 2 + "、横" + canvasSize.getWidth() * 2);
		Slider slider = new Slider();
		slider.setMin(100);
		slider.setMax(1600);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(200);
		slider.setMinorTickCount(1);
		slider.setSnapToTicks(true);
		slider.setValue(200);
		slider.valueProperty().addListener((obs, oldVal, newVal) -> {
			double zoomer = slider.getValue() / 100;
			l3.setText("元のサイズ：縦" + canvasSize.getHeight() * zoomer + "、横" + canvasSize.getWidth() * zoomer);
			l1.setText("出力するイメージの大きさを設定してください（現在の倍率：" + slider.getValue() + "%）");
		});
		Button b1 = new Button("出力");
		Button b2 = new Button("キャンセル");
		b1.setDefaultButton(true);
		b1.setOnAction((ActionEvent) ->{
			try{
				SnapshotParameters ssp = new SnapshotParameters();
				drawer.setZoomRatio(slider.getValue()/100);
				mapDraw();
				WritableImage wi = canvas.snapshot(ssp, null);
				drawer.setZoomRatio(1);;
				mapDraw();
				fileSaveDialog.showDialog("画像の書き出し", config.getImageFileDir(), FileType.PNG)
					.ifPresent(r -> {
						File imageFile = r.getFile();
						config.setImageFileDir(imageFile.getParent());

						try {
							switch (r.getFileType()) {
								case PNG:
									ImageIO.write(SwingFXUtils.fromFXImage(wi,null), "png", imageFile);
									break;
								case JPG:
									ImageIO.write(SwingFXUtils.fromFXImage(wi,null), "jpg", imageFile);
									break;
								case BMP:
									ImageIO.write(SwingFXUtils.fromFXImage(wi,null), "bmp", imageFile);
									break;
								default:
									break;
							}
						} catch (IOException e) {
							alert.showError("保存中にエラーが発生しました。");
						}
				});

				expStage.close();
			}catch(RuntimeException e){
				e.printStackTrace();
				alert.showError("出力サイズが大きすぎるようです。倍率を下げてみてください。");
			}
		});
		b2.setCancelButton(true);
		b2.setOnAction((ActionEvent) ->{
			expStage.close();
		});
		expBox.getChildren().add(l1);
		expBox.getChildren().add(l2);
		expBox.getChildren().add(l3);
		expBox.getChildren().add(slider);
		expBox.getChildren().add(b1);
		expBox.getChildren().add(b2);
		expStage.setScene(sceneFactory.createScene(expBox));
		expStage.showAndWait();
	}
	boolean checkUpdate(boolean onLaunch){//アップデートがあればtrue。なければfalse。このフラグは手動で確認が行われた時用。
		boolean nv = false;
		URL url;
		try {
			url = new URL("https://spreadsheets.google.com/feeds/cells/1AzBe7Jdny2YnIW9hUixfbGBLmaj1xRGjEEl042fZ7kE/od6/public/values");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			/*
			 * スプレッドシートの書式について　全てA列を使って（[a]はanalyzeXMLから返ってくる配列のindex）
			 * [0]1:最新バージョンの番号
			 * [1]2:Description in Japanese
			 * [2]3:URL for description in Japanese
			 * [3]4:Description in English
			 * [4]5:URL for description in English
			 * [5]6:Auto-Download URL
			 * [6]7:Usage Post用URL（本番）
			 * [7]8:Usage Post用URL（テスト）
			 * [8]9:Error report用URL（本場）
			 * [9]10:Error report用URL（テスト）
			 */
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				System.out.println("接続に問題はありません。");
				InputStream is = conn.getInputStream();
				//BufferedReader br = new BufferedReader(new InputStreamReader(is));
				//System.out.println(br.readLine());
				BufferedInputStream bis = new BufferedInputStream(is);
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = factory.newDocumentBuilder();
				Document document = documentBuilder.parse(bis);
				bis.close();
				
				//XMLの解析は別メソッドに丸投げします。
				String[] xmlDatas = analyzeXML(document);//XMLを分析した結果はここに大きさ9の配列で返ってくる。
				if(Double.parseDouble(xmlDatas[0]) > ReleaseVersion){
					nv = true;
					Platform.runLater(() ->{
						Alert alert = alertFactory.createAlert(AlertType.INFORMATION,"",ButtonType.CLOSE);
						alert.getDialogPane().setHeaderText("新しいバージョンがリリースされています。");
						Hyperlink link = new Hyperlink(xmlDatas[2]);
						link.setOnAction((ActionEvent) -> {
							Desktop desktop = Desktop.getDesktop();
							URI uu;
							try {
								uu = new URI(xmlDatas[2]);
								desktop.browse(uu);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
						Label l1 = new Label("新しいバージョン：" + xmlDatas[0]);
						Label l2 = new Label(xmlDatas[1]);
						alert.getDialogPane().setContent(new VBox(4.0, l1, l2, link));
						alert.showAndWait();
					});
				}
				else {
					System.out.println("この本体は最新バージョンです．");
				}
				if(onLaunch) {
					postUsage(xmlDatas[ErrorReporter.isDebugBuild() ? 7 : 6]);
					ErrorReporter.setReportURL(xmlDatas[ErrorReporter.isDebugBuild() ? 9 : 8], ReleaseVersion);
				}
			}else{
				nv = true;
				Platform.runLater(() ->{
					try {
						alert.showErrorAsync("ソフトウェアのアップデートを確認できませんでした。\n" +
							conn.getResponseCode() + conn.getResponseMessage());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
			nv = true;
			Platform.runLater(() ->{
				alert.showErrorAsync("ソフトウェアのアップデートを確認できませんでした。\n" + 
					"ネットワークに接続できません。");
			});
		} catch(SAXException e){
			e.printStackTrace();
		} catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		
		return nv;
	}
	
	void postUsage(String url_header) {
		try {
			
			URL url = new URL(url_header + "?version=" + ReleaseVersion
					+ "&os=" + URLEncoder.encode(System.getProperty("os.name"), "UTF-8")
					+ "&locale=" + Locale.getDefault().getCountry()
					+ "&mac=" + ErrorReporter.getMacAddr());
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
			conn.getInputStream(); //これをもってHTTP接続が行われる．
		} catch(IOException e) {
			// usageの送信だけなので特にエラー処理はしない．
			e.printStackTrace();
		}
	}

	String[] analyzeXML(Document document){
		String[] data = new String[10];
		Element root = document.getDocumentElement();
		NodeList children1 = root.getChildNodes();
		for(int i1 = 0 ; i1 < children1.getLength(); i1++){
			Node node1 = children1.item(i1);
			if(node1.getNodeType() == Node.ELEMENT_NODE){
				Element element1 = (Element)node1;
				if(element1.getNodeName().equals("entry")){
					NodeList children2 = node1.getChildNodes();
					String categoryTitle = null;//ここでこのcategoryがどのデータに相当するのかを記憶します。
					String categoryContent = null;
					for(int i2 = 0; i2 < children2.getLength(); i2++){
						Node node2 = children2.item(i2);
						if(node2.getNodeType() == Node.ELEMENT_NODE){
							Element element2 = (Element)node2;
							if(element2.getNodeName().equals("title")) categoryTitle = element2.getTextContent();
							if(element2.getNodeName().equals("content")) categoryContent = element2.getTextContent();
						}
					}
					if(categoryTitle != null){
						if(categoryTitle.equals("A1")) data[0] = categoryContent;
						if(categoryTitle.equals("A2")) data[1] = categoryContent;
						if(categoryTitle.equals("A3")) data[2] = categoryContent;
						if(categoryTitle.equals("A4")) data[3] = categoryContent;
						if(categoryTitle.equals("A5")) data[4] = categoryContent;
						if(categoryTitle.equals("A6")) data[5] = categoryContent;
						if(categoryTitle.equals("A7")) data[6] = categoryContent;
						if(categoryTitle.equals("A8")) data[7] = categoryContent;
						if(categoryTitle.equals("A9")) data[8] = categoryContent;
						if(categoryTitle.equals("A10")) data[9] = categoryContent;
					}
				}
			}
		}
		//全ての項目が満たされてるか一応チェック
		for(int i = 0; i < 6; i++){
			if(data[i] == null) throw new IllegalArgumentException("update情報項目" + i + "が取得できていません。");
		}
		return data;
	}
	void setAutoUpdate(Button bt, String surl){//オートアップデートを実行するメソッド。結局やらないことにしました。
		bt.setOnAction((ActionEvent) ->{
			boolean goNext = true;//エラーがあったらこいつをfalseにして止めます。
			try{
				URL url = new URL(surl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
					System.out.println("新しい実行ファイルのダウンロードに問題ありません。");
					String CURRENT_DIRECTORY = new File("").getAbsolutePath();
					InputStream is = conn.getInputStream();
				}
			}catch(MalformedURLException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		});
	}
	void saveHistory(){//現在の状態をヒストリに加える。
		//最初に保存していいか確認
		Optional<ButtonType> result = alert.showConfirmation("ヒストリに保存するために現在の状態を保存します。よろしいですか？");
		if(result.get() == ButtonType.OK){
			
		}
	}
	protected void setMenuBarMode(boolean b){//メニューバーはシステムのものを使うかどうか設定
		menubar.setUseSystemMenuBar(b);
	}
	void initializeLineDashes(){
		lineDashes.clear();
		double[] d1 = {5d,5d};
		double[] d2 = {12d,8d};
		lineDashes.add(LineDash.SOLID);//直線
		lineDashes.add(new LineDash(d1));
		lineDashes.add(new LineDash(d2));
	}
}
