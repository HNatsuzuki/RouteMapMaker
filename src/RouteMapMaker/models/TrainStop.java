package RouteMapMaker.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

public class TrainStop {//系統毎に保持する必要がある駅に関する情報を駅オブジェクトと関連付けて保存。
	private Station station;//駅
	private IntegerProperty shiftX = new SimpleIntegerProperty();//停車駅印のシフト
	private IntegerProperty shiftY = new SimpleIntegerProperty();
	private ObjectProperty<StopMark> mark = new SimpleObjectProperty<>(StopMark.OBEY_LINE);//駅ごとの停車駅印。
	
	public TrainStop(Station s){//オブジェクトの生成時はstationを要求。
		this.station = s;
		shiftX.set(0);
		shiftY.set(0);
		if(s.getNameSize() == -1) mark.set(StopMark.NO_DRAW);//中継点な時はデフォルトでNO_DRAWを設定する。
	}
	public void setShiftX(int x){
		this.shiftX.set(x);
	}
	public void setShiftY(int y){
		this.shiftY.set(y);
	}
	public IntegerProperty getShiftXProperty(){
		return this.shiftX;
	}
	public IntegerProperty getShiftYProperty(){
		return this.shiftY;
	}
	public int[] getShift(){
		int s[] = {shiftX.get(), shiftY.get()};
		return s;
	}
	public Point2D getOffset() {
		return new Point2D(shiftX.get(), shiftY.get());
	}
	public Station getSta(){
		return this.station;
	}
	public void setSta(Station s) {
		station = s;
	}
	public ObjectProperty<StopMark> getMarkProperty() {
		return this.mark;
	}
	public void setMark(StopMark m){
		this.mark.set(m);
	}
	public StopMark getMark(){
		return this.mark.get();
	}
}
