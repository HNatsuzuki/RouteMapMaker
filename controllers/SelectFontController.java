package RouteMapMaker.controllers;

import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

import RouteMapMaker.FontFormatCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	private int fontIndex;//今どれが選択されているか
	private int defaultIndex;//デフォルトのフォントのindexを保持する
	private boolean saved;//戻る時に変更を反映するかしないか
	private ObservableList<String> fn = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		//デフォルトフォントを探索
		defaultIndex = getFontIndex("System");

		for(int i = 0; i < Font.getFamilies().size(); i++){
			fn.add(Font.getFamilies().get(i));
		}
		FontList.setItems(fn);
		FontList.setCellFactory((ListView<String> l) -> new FontFormatCell());
		FontList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			fontIndex = FontList.getSelectionModel().getSelectedIndex();
			sampleText.setFont(Font.font(Font.getFamilies().get(fontIndex)));
		});
		defaultFont.setOnAction((ActionEvent) ->{
			fontIndex = defaultIndex;
			FontList.getSelectionModel().select(fontIndex);
			sampleText.setFont(Font.font(Font.getFamilies().get(fontIndex)));
		});
		cancelBT.setOnAction((ActionEvent) ->{
			saved = false;
			Stage stage = (Stage)cancelBT.getScene().getWindow();
			stage.close();
		});
		saveBT.setOnAction((ActionEvent) ->{
			saved = true;
			Stage stage = (Stage)saveBT.getScene().getWindow();
			stage.close();
		});
	}
	public void setObject(String currentFont){
		if(currentFont == null){//nullのときはSystemを指定します。
			fontIndex = defaultIndex;
		}else{
			int index = getFontIndex(currentFont);

			if (index == -1){
				fontIndex = defaultIndex;
			} else {
				fontIndex = index;
			}
		}
		FontList.getSelectionModel().select(fontIndex);
	}
	public boolean shouldSave(){
		return saved;
	}
	public String getFontName(){
		if(saved){
			return Font.getFamilies().get(fontIndex);
		}else{
			throw new IllegalArgumentException("shouldSaveがfalseです。フォント名は渡せません");
		}
	}

	/**
	 * フォント名からフォントのインデックスを取得します。
	 *
	 * @param  fontName フォント名
	 * @return インデックス (見つからなかった場合は -1)
	 */
	private int getFontIndex(String fontName) {
		List<String> families = Font.getFamilies();

		for (int i = 0; i < families.size(); ++i) {
			if (families.get(i).equals(fontName)) {
				return i;
			}
		}

		return -1;
	}
}
