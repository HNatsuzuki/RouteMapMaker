package RouteMapMaker.models;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class Line {//路線の情報を保持するクラス。
	public static final int REGULAR = 0;
	public static final int ITALIC = 1;
	public static final int BOLD = 2;
	public static final int ITALIC_BOLD = 3;
	
	//この数字の値，並びは各所で利用されているので変更は要注意
	public static final int RIGHT = 0;
	public static final int LEFT = 1;
	public static final int TOP = 2;
	public static final int BOTTOM = 3;
	public static final int CENTER = 4;
	
	public class Connection {
		private ObjectProperty<Station> station = new SimpleObjectProperty<>();
		BooleanProperty curve;
		Connection(Station s, boolean c) {
			station.set(s);
			curve = new SimpleBooleanProperty(c);
		}
		Connection(Station s) {
			station.set(s);
			curve = new SimpleBooleanProperty(false);
		}

		public ObjectProperty<Station> getStationProperty() {
			return this.station;
		}

		public BooleanProperty getCurve() {
			return this.curve;
		}

		public Station getStation() {
			return this.station.get();
		}

		public void setStation(Station station) {
			this.station.set(station);
		}
	};
	
	private ObservableList<Connection> connections; //<駅，曲線接続> で駅同士の接続を保持．
	private ObservableList<Train> trains;//運転系統を保持する。
	private StringProperty lineName = new SimpleStringProperty();//路線名
	private final BooleanProperty vertical = new SimpleBooleanProperty(true);//縦書きか横書きか。trueなら縦書き
	private IntegerProperty nameStyle = new SimpleIntegerProperty(REGULAR);
	private IntegerProperty nameSize = new SimpleIntegerProperty(15);
	private final ObjectProperty<TextLocation> nameLocation = new SimpleObjectProperty<>(TextLocation.BOTTOM);
	private ObjectProperty<Color> nameColor = new SimpleObjectProperty<>(Color.BLACK);
	private IntegerProperty nameX = new SimpleIntegerProperty(0);
	private IntegerProperty nameY = new SimpleIntegerProperty(0);
	
	public Line(String name){//コンストラクタ
		connections = FXCollections.observableArrayList();
		trains = FXCollections.observableArrayList();
		setName(name);
	}
	
	public String getName(){
		return lineName.get();
	}
	public StringProperty getNameProperty(){
		return this.lineName;
	}
	public void setName(String name){
		this.lineName.set(name);
	}

	public Station getStation(int index) {
		return this.connections.get(index).getStation();
	}

	// ここで得られるListは編集可能ではないので注意
	public ObservableList<Station> getStations(){
		ObservableList<Station> staList = FXCollections.observableArrayList();
		connections.forEach(c -> staList.add(c.station.get()));
		return staList;
	}
	public void setStations(List<Station> st){
		Point2D start = connections.get(0).station.get().getPoint();
		Point2D terminal = connections.get(connections.size() - 1).station.get().getPoint();
		connections.clear();
		st.forEach(s -> connections.add(new Connection(s)));
		connections.get(0).station.get().setPoint(start);
		connections.get(connections.size() - 1).station.get().setPoint(terminal);
	}
	public Connection insertStation(int idx, Station sta) {
		Connection c = new Connection(sta);
		connections.add(idx, c);
		return c;
	}
	public Connection addStation(Station sta) {
		Connection c = new Connection(sta);
		connections.add(c);
		return c;
	}
	public void addStation(Station station, boolean curve) {
		Connection connection = new Connection(station);
		connection.curve.set(curve);
		connections.add(connection);
	}

	public Connection removeStation(int idx) {
		return connections.remove(idx);
	}

	/**
	 * 座標非固定駅の座標を計算します。
	 */
	public void interpolateIntermediatePoints() {
		List<Station> stations = getStations();

		// 座標固定開始駅
		Point2D start = stations.get(0).getPoint();
		// 開始駅からの駅数 (開始駅は含まない)
		int count = 0;

		for (int i = 1; i < stations.size(); ++i) {
			++count;

			if (!stations.get(i).isSet()) {
				// まず非固定駅はなにもせず、次の固定駅を探す
				continue;
			}

			// 座標固定終了駅
			Point2D end = stations.get(i).getPoint();

			// j = 0 は開始駅のため計算不要、j = count は終了駅のため計算不要
			for (int j = 1; j < count; ++j) {
				// 開始位置を基準に、開始位置から終了位置を等分する
				Point2D point = start.add(end.subtract(start).multiply((double)j / count));
				stations.get(i - count + j).setInterPoint(point.getX(), point.getY());
			}

			// 終了駅を開始駅に設定し直して次の終了駅を探す
			start = end;
			count = 0;
		}
	}

	public ObservableList<Connection> getConnections() {
		return connections;
	}
	public boolean isVertical() {
		return vertical.get();
	}
	public BooleanProperty verticalProperty() {
		return this.vertical;
	}
	public void setVertical(boolean t) {
		this.vertical.set(t);
	}
	public void setNameStyle(int i){
		this.nameStyle.set(i);
	}
	public IntegerProperty getNameStyleProperty(){
		return this.nameStyle;
	}
	public int getNameStyle(){
		return this.nameStyle.get();
	}
	public int getNameSize(){
		return this.nameSize.get();
	}
	public IntegerProperty getNameSizeProperty(){
		return this.nameSize;
	}
	public void setNameSize(int i){
		if(i <= 0) throw new IllegalArgumentException("lineのNameSizeは0以下にできません");//0以下は許容しません。
		this.nameSize.set(i);
	}
	public TextLocation getNameLocation(){
		return this.nameLocation.get();
	}
	public ObjectProperty<TextLocation> getNameLocationProperty(){
		return this.nameLocation;
	}
	public void setNameLocation(TextLocation i){
		this.nameLocation.set(i);
	}
	public Color getNameColor(){
		return this.nameColor.get();
	}
	public ObjectProperty<Color> getNameColorProperty(){
		return this.nameColor;
	}
	public void setNameColor(Color c){
		this.nameColor.set(c);
	}
	public void setNameX(int i){
		this.nameX.set(i);
	}
	public void setNameY(int i){
		this.nameY.set(i);
	}
	public IntegerProperty getNameXProperty(){
		return this.nameX;
	}
	public IntegerProperty getNameYProperty(){
		return this.nameY;
	}
	public Point2D getNameOffset(){
		return new Point2D(nameX.intValue(), nameY.intValue());
	}
	public ObservableList<Train> getTrains(){
		return trains;
	}
	public void setTrains(ObservableList<Train> t){
		trains = t;
	}

	/**
	 * 指定した運転系統が含まれているかを調べます。
	 *
	 * @param train 運転系統
	 * @return 指定した運転系統が含まれている場合 true
	 */
	public boolean hasTrain(Train train) {
		return this.trains.contains(train);
	}

	public boolean isCurvable(int idx) {
		return idx>1 && connections.size()-idx>1 && //端条件
				connections.get(idx-1).station.get().isSet() && connections.get(idx).station.get().isSet() && //固定条件
				!connections.get(idx-1).curve.get() && !connections.get(idx+1).curve.get(); //連続条件
	}
	public boolean getCurveConnection(int idx) {
		return connections.get(idx).curve.get();
	}

	public boolean isConnectedByCurve(int index) {
		return this.getCurveConnection(index) && this.isCurvable(index);
	}
}
