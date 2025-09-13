package RouteMapMaker.models;

/**
 * 破線パターンを表すクラスです。
 */
public class LineDash {
	private double[] lineDashPattern;

	public LineDash(double[] pattern){
		lineDashPattern = pattern;
	}

	public void set(double[] pattern){
		lineDashPattern = pattern;
	}

	public double[] get(){
		return lineDashPattern;
	}
}
