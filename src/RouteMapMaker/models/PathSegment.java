package RouteMapMaker.models;

import javafx.geometry.Point2D;

/**
 * 図形を描くためのセグメントを表すクラスです。
 */
public class PathSegment {
    private final Point2D point;
    private final Point2D controlPoint;

    /**
     * コンストラクタ
     *
     * @param point 描画点
     * @param controlPoint 2次ベジェ曲線の制御点
     */
    public PathSegment(Point2D point, Point2D controlPoint) {
        this.point = point;
        this.controlPoint = controlPoint;
    }

    /** 描画点 */
    public Point2D getPoint() {
        return this.point;
    }

    /** 2次ベジェ曲線の制御点 (曲線ではない場合はnull) */
    public Point2D getControlPoint() {
        return this.controlPoint;
    }
}
