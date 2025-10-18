package RouteMapMaker.models;

public class MvSta {//駅座標の移動に関する情報を保持するクラス
	Station sta;
	Point2D start;
	boolean isSet;//最初固定点だったか非固定点だったか
	public MvSta(Station sta){
		this.sta = sta;
		this.start = sta.getPointUS();
		this.isSet = sta.isSet();
	}

	public void setStart(Point2D start) {
		this.start = start;
	}

	public Station getStation() {
		return this.sta;
	}

	public Point2D getStart() {
		return this.start;
	}

	public boolean getIsSet() {
		return this.isSet;
	}
}
