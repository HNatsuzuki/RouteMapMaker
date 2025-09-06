package RouteMapMaker.models;

public class MvSta {//駅座標の移動に関する情報を保持するクラス
	Station sta;
	double[] start = new double[2];
	boolean isSet;//最初固定点だったか非固定点だったか
	public MvSta(Station sta){
		this.sta = sta;
		this.start[0] = sta.getPointUS()[0];//後からの値の変更を防ぐ
		this.start[1] = sta.getPointUS()[1];
		this.isSet = sta.isSet();
	}

	public void setStart(double[] start) {
		this.start[0] = start[0];
		this.start[1] = start[1];
	}

	public Station getStation() {
		return this.sta;
	}

	public double[] getStart() {
		return this.start;
	}

	public boolean getIsSet() {
		return this.isSet;
	}
}
