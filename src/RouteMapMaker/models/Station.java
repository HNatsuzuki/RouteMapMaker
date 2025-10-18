package RouteMapMaker.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Station {//駅に関する情報を保持するクラス
	
	public static final int TEXT_RIGHT = 10;
	public static final int TEXT_LEFT = 9;
	public static final int TEXT_BOTTOM = 11;
	public static final int TEXT_TOP = 12;
	public static final int TEXT_CENTER = 13;
	public static final int TEXT_UNSET = -11;
	
	public static final int REGULAR = 0;
	public static final int ITALIC = 1;
	public static final int BOLD = 2;
	public static final int BOLD_ITALIC = 3;
	public static final int STYLE_UNSET = 4;
	
	private StringProperty name = new SimpleStringProperty();//駅名
	private BooleanProperty pointSet = new SimpleBooleanProperty(false);//固定座標があるか否か
	private final ObjectProperty<Point2D> point = new SimpleObjectProperty<>();
	private int stationConnection = 0;
	private IntegerProperty textLocation = new SimpleIntegerProperty(TEXT_UNSET);//駅ごとの駅名表示位置
	private BooleanProperty tategaki = new SimpleBooleanProperty(true);//縦書きか横書きか。trueなら縦書き
	private IntegerProperty size = new SimpleIntegerProperty(0);//駅ごとに設定される文字サイズ。0は経路準拠
	private IntegerProperty style = new SimpleIntegerProperty(STYLE_UNSET);//駅ごとに設定される文字スタイル
	private IntegerProperty nameX = new SimpleIntegerProperty(0);//駅名の描画位置のズレ
	private IntegerProperty nameY = new SimpleIntegerProperty(0);
	private BooleanProperty shiftOnStation = new SimpleBooleanProperty(false);//描画位置修正を駅ごとの設定に従うか否か
	
	public Station(String name){
		setName(name);
	}
	
	public void setName(String name){
		this.name.set(name);
	}
	public StringProperty getNameProperty(){
		return this.name;
	}
	public String getName(){
		return name.get();
	}
	public void setPoint(double x, double y){
		point.set(new Point2D(x, y));
		pointSet.set(true);
	}
	public void setPoint(Point2D point) {
		this.point.set(point);
		pointSet.set(true);
	}
	public ObjectProperty<Point2D> getPointProperty(){
		return point;
	}
	public void erasePoint(){
		pointSet.set(false);
	}
	public BooleanProperty getPointSetProperty(){
		return this.pointSet;
	}
	public boolean isSet(){
		return pointSet.get();
	}

	/**
	 * 駅の固定座標を取得します。
	 *
	 * @return 駅の固定座標
	 */
	public Point2D getPoint() {
		if (!pointSet.get()) {
			throw new IllegalArgumentException("座標非固定駅の座標は取得できません。");
		}

		return point.get();
	}

	public void plusConnection(){
		stationConnection ++;
	}
	public void minusConnection(){
		stationConnection --;
	}
	public void setConnection(int c){
		stationConnection = c;
	}
	public int getConnection(){
		return stationConnection;
	}
	public void setInterPoint(double x, double y){
		pointSet.set(false);
		point.set(new Point2D(x, y));
	}

	public void setInterPoint(Point2D point) {
		pointSet.set(false);
		this.point.set(point);
	}

	public Point2D getInterPoint() {
		if (pointSet.get()) {
			throw new IllegalArgumentException("座標固定駅の座標は取得できません。");
		} else {
			return point.get();
		}
	}

	public void setTextLocation(int muki){
		this.textLocation.set(muki);
	}
	public IntegerProperty getTextLocationProperty(){
		return this.textLocation;
	}
	public int getTextLocation(){
		return textLocation.get();
	}
	public void setTategaki(boolean b) {
		tategaki.set(b);
	}
	public BooleanProperty getTategakiProperty() {
		return tategaki;
	}
	public boolean isTategaki() {
		return tategaki.get();
	}

	public Point2D getPointUS() {
		//isSetを考慮しません。使うのは全ての座標が決定した後にしましょう。
		return point.get();
	}
	public int getNameSize(){
		return size.get();
	}
	public IntegerProperty getNameSizeProperty(){
		return this.size;
	}
	public void setNameSize(int i){
		this.size.set(i);
	}
	public int getNameStyle(){
		return this.style.get();
	}
	public IntegerProperty getNameStyleProperty(){
		return this.style;
	}
	public void setNameStyle(int i){
		this.style.set(i);
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
	public boolean shiftBasedOnStation(){
		return this.shiftOnStation.get();
	}
	public BooleanProperty getShiftOnStationProperty(){
		return this.shiftOnStation;
	}
	public void setShiftBase(boolean b){
		this.shiftOnStation.set(b);
	}

	/**
	 * 平行移動を行います。
	 *
	 * @param x x方向の移動量
	 * @param y y方向の移動量
	 */
	public void translate(double x, double y) {
		point.set(point.get().add(x, y));
	}
}
