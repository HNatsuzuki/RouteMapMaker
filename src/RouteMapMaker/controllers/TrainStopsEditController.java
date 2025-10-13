package RouteMapMaker.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import RouteMapMaker.models.Station;
import RouteMapMaker.models.Train;
import RouteMapMaker.models.TrainStop;
import RouteMapMaker.services.AlertService;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class TrainStopsEditController implements Initializable{
	
	private final List<Station> stations;
	private final Train train;
	private ObservableList<String> stationNameList = FXCollections.observableArrayList();
	private ObservableList<String> trainStopNameList = FXCollections.observableArrayList();
	private final AlertService alert;
	
	@FXML ToggleGroup group;
	@FXML ToggleButton insertButton;
	@FXML ToggleButton deleteButton;
	@FXML ToggleButton insertAllButton;
	@FXML ToggleButton deleteAllButton;
	@FXML Button closeButton;
	@FXML ListView<String> stationListView;
	@FXML ListView<String> trainStopListView;
	@FXML Label infoLabel;

	public TrainStopsEditController(List<Station> stations, Train train, AlertService alert) {
		this.stations = stations;
		this.train = train;
		this.alert = alert;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		infoLabel.setText("停車駅は駅一覧の中から\n"
				+ "上から順に追加してください。\n\n"
				+ "挿入：挿入位置を右枠で選択し\n"
				+ "挿入ボタンを押してから左枠\n"
				+ "から挿入する駅を選択する。\n"
				+ "hint:反応しない場合は\n"
				+ "別の駅を選択してみてください\n\n"
				+ "削除：削除ボタンを押してから\n"
				+ "右枠で削除する駅を選択する。");
		
		// 路線の駅リスト
		stationNameList.setAll(stations.stream().map(s -> s.getName()).collect(Collectors.toList()));
		stationListView.setItems(stationNameList);

		// 系統の駅リスト
		trainStopNameList.setAll(train.getStops().stream().map(s -> s.getSta().getName()).collect(Collectors.toList()));
		trainStopNameList.add("<最後に追加>");
		trainStopListView.setItems(trainStopNameList);
		trainStopListView.getSelectionModel().selectLast();

		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle,
				Toggle new_toggle) ->{
					if(group.getSelectedToggle() == insertAllButton){
						Optional<ButtonType> result = alert.showConfirmation("全ての駅を停車駅として追加してよろしいですか？", "選択路線駅全追加の確認");
						if(result.get() == ButtonType.OK){
							//全削除してから再度追加だと既存駅の属性が失われるので足りない分を追加する。
							int count = 0;
							for(int i = 0; i < stations.size(); i++){
								if(train.getStops().size()==count||!train.getStops().get(count).getSta().getName().equals(stations.get(i).getName())){
									//駅が存在しない
									train.getStops().add(count, new TrainStop(stations.get(i)));
								}
								count++;
							}
							trainStopNameList.clear();
							for(int i = 0; i < train.getStops().size(); i++){
								trainStopNameList.add(train.getStops().get(i).getSta().getName());
							}
							trainStopNameList.add("<最後に追加>");
							trainStopListView.getSelectionModel().select(0);
						}
						insertAllButton.setSelected(false);
					}
					if(group.getSelectedToggle() == deleteAllButton){
						Optional<ButtonType> result = alert.showConfirmation("全ての停車駅を削除してよろしいですか？", "選択駅全消去の確認");
						if(result.get() == ButtonType.OK){
							//全消去処理
							train.getStops().clear();
							trainStopNameList.clear();
							trainStopNameList.add("<最後に追加>");
							trainStopListView.getSelectionModel().select(train.getStops().size());
						}
						deleteAllButton.setSelected(false);
					}
				});
		stationListView.setOnMouseClicked((MouseEvent) ->{
			if(group.getSelectedToggle() == insertButton){
				int indexB = stationListView.getSelectionModel().getSelectedIndex();
				int indexC = trainStopListView.getSelectionModel().getSelectedIndex();
				if(indexC == -1){
					alert.showError("停車駅を追加する位置を選んでください。");
				}else if(indexB != -1){
					//追加して大丈夫か検査する。
					int pre;//前の停車駅の路線でのindex
					int next;//後の停車駅の路線でのindex
					try{
						pre = stations.indexOf(train.getStops().get(indexC - 1).getSta());
					}catch(IndexOutOfBoundsException e){
						pre = -1;//系統の先頭に追加要求があった場合。
					}
					try{
						next = stations.lastIndexOf(train.getStops().get(indexC).getSta());
					}catch(IndexOutOfBoundsException e){
						next = stations.size();//系統の最後に追加要求があった場合
					}
					if(pre < indexB && indexB < next){
						boolean adjon = false;//連続して同じ駅が登録されていると都合が悪い。
						try{
							if(stations.get(indexB) == train.getStops().get(indexC -1).getSta()) adjon = true;
							if(stations.get(indexB) == train.getStops().get(indexC).getSta()) adjon = true;
						}catch(Exception e){
							//indexエラーはここでは無視していいので何もしない
						}
						if(adjon){
							alert.showWarning("同じ駅を隣接して追加することはできません。");
						}else{//順番検査と隣接検査をクリアしたら追加する。
							train.getStops().add(indexC, new TrainStop(stations.get(indexB)));
						}
					}else{
						alert.showWarning("停車駅は駅一覧の上から順である必要があります。");
					}
					trainStopNameList.clear();
					for(int i = 0; i < train.getStops().size(); i++){
						trainStopNameList.add(train.getStops().get(i).getSta().getName());
					}
					trainStopNameList.add("<最後に追加>");
					trainStopListView.getSelectionModel().select(indexC + 1);
				}
			}
		});

		// 系統の駅リスト
		trainStopListView.setOnMouseClicked((MouseEvent) ->{
			//動作に不具合は見られないけどIndexOutOfBoundsExceptionが出てくる
			if(group.getSelectedToggle() == deleteButton){
				deleteStation();
			}
		});

		// 閉じるボタン
		closeButton.setOnAction(event -> {
			((Stage)((Node)event.getSource()).getScene().getWindow()).close();
		});
	}

	/**
	 * 系統から選択した駅を削除します。
	 */
	private void deleteStation() {
		int selectedTrainStopIndex = trainStopListView.getSelectionModel().getSelectedIndex();

		if (selectedTrainStopIndex != -1 && selectedTrainStopIndex < train.getStops().size()) {
			train.getStops().remove(selectedTrainStopIndex);
			trainStopNameList.remove(selectedTrainStopIndex);
			trainStopListView.getSelectionModel().select(selectedTrainStopIndex);
		}
	}
}
