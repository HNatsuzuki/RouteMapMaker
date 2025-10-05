package RouteMapMaker.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import RouteMapMaker.listcells.FontFormatCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SelectFontController implements Initializable{

	@FXML Button saveBT;
	@FXML Button cancelBT;
	@FXML Button defaultFont;
	@FXML Label sampleText;
	@FXML ListView<String> FontList;
	private boolean accepted;
	private final ObservableList<String> fontNames = FXCollections.observableArrayList();
	private final StringProperty selectedFontName = new SimpleStringProperty();
	private final String currentFont;
	private final String DEFAULT_FONT_NAME = "System";

	public SelectFontController(String currentFont) {
		// すべてのフォントを表示用リストに追加
		fontNames.addAll(Font.getFamilies());

		if (fontNames.contains(currentFont)) {
			this.currentFont = currentFont;
		} else {
			this.currentFont = DEFAULT_FONT_NAME;
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// フォント名プロパティのバインド
		selectedFontName.bind(FontList.getSelectionModel().selectedItemProperty());
		selectedFontName.addListener((observable, oldValue, newValue) -> {
			sampleText.setFont(Font.font(newValue));
		});

		// フォントリストの初期化
		FontList.setItems(fontNames);
		FontList.getSelectionModel().select(currentFont);
		FontList.setCellFactory((ListView<String> l) -> new FontFormatCell());

		// 「デフォルトのフォント」ボタン押下時処理
		defaultFont.setOnAction(e ->{
			FontList.getSelectionModel().select(DEFAULT_FONT_NAME);
		});

		// 「キャンセル」ボタン押下時処理
		cancelBT.setOnAction(e ->{
			accepted = false;
			((Stage)((Node)e.getSource()).getScene().getWindow()).close();
		});

		// 「決定」ボタン押下時処理
		saveBT.setOnAction(e ->{
			accepted = true;
			((Stage)((Node)e.getSource()).getScene().getWindow()).close();
		});
	}

	/** ダイアログの結果 OK となったか */
	public boolean isAccepted() {
		return accepted;
	}

	/** 選択したフォント */
	public String getSelectedFontName() {
		if (accepted) {
			return selectedFontName.get();
		} else {
			throw new IllegalStateException("選択したフォントを取得できません。フォント選択がキャンセルされました。");
		}
	}
}
