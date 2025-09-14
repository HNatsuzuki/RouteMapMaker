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
	private BooleanProperty tategaki = new SimpleBooleanProperty(true);//縦書きか横書きか。trueなら縦書き
	private IntegerProperty nameStyle = new SimpleIntegerProperty(REGULAR);
	private IntegerProperty nameSize = new SimpleIntegerProperty(15);
	private IntegerProperty nameLocation = new SimpleIntegerProperty(BOTTOM);
	private ObjectProperty<Color> nameColor = new SimpleObjectProperty<>(Color.BLACK);
	private IntegerProperty NameX = new SimpleIntegerProperty(0);
	private IntegerProperty NameY = new SimpleIntegerProperty(0);
	
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
	// ここで得られるListは編集可能ではないので注意
	public ObservableList<Station> getStations(){
		ObservableList<Station> staList = FXCollections.observableArrayList();
		connections.forEach(c -> staList.add(c.station.get()));
		return staList;
	}
	public void setStations(List<Station> st){
		double[] start = connections.get(0).station.get().getPoint();
		double[] terminal = connections.get(connections.size() - 1).station.get().getPoint();
		connections.clear();
		st.forEach(s -> connections.add(new Connection(s)));
		connections.get(0).station.get().setPoint(start[0], start[1]);
		connections.get(connections.size() - 1).station.get().setPoint(terminal[0], terminal[1]);
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
	public ObservableList<Connection> getConnections() {
		return connections;
	}
	public boolean isTategaki(){
		return tategaki.get();
	}
	public BooleanProperty getTategakiProperty(){
		return this.tategaki;
	}
	public void setTategaki(boolean t){
		this.tategaki.set(t);
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
	public int getNameLocation(){
		return this.nameLocation.get();
	}
	public IntegerProperty getNameLocationProperty(){
		return this.nameLocation;
	}
	public void setNameLocation(int i){
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
		this.NameX.set(i);
	}
	public void setNameY(int i){
		this.NameY.set(i);
	}
	public IntegerProperty getNameXProperty(){
		return this.NameX;
	}
	public IntegerProperty getNameYProperty(){
		return this.NameY;
	}
	public int[] getNameZure(){
		int[] ia = {NameX.get(), NameY.get()};
		return ia;
	}
	public ObservableList<Train> getTrains(){
		return trains;
	}
	public void setTrains(ObservableList<Train> t){
		trains = t;
	}
	public boolean isCurvable(int idx) {
		return idx>1 && connections.size()-idx>1 && //端条件
				connections.get(idx-1).station.get().isSet() && connections.get(idx).station.get().isSet() && //固定条件
				!connections.get(idx-1).curve.get() && !connections.get(idx+1).curve.get(); //連続条件
	}
	public boolean getCurveConnection(int idx) {
		return connections.get(idx).curve.get();
	}
}
