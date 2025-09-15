package RouteMapMaker.models;

import javafx.geometry.Point2D;

/**
 * 線分を表すクラスです。
 */
public class LineSegment {
    private static final double EPSILON = 1e-5;
    private final Point2D start;
    private final Point2D end;

    /**
     * コンストラクタ
     *
     * @param start 線分の開始座標
     * @param end 線分の終了座標
     */
    public LineSegment(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }

    public Point2D getStart() {
        return this.start;
    }

    public Point2D getEnd() {
        return this.end;
    }

    /**
     * 線分の長さを取得します。
     *
     * @return 線分の長さ
     */
    public double length() {
        return this.start.distance(end);
    }

    /**
     * オフセット分シフトした線分を作成します。
     *
     * @param start 線分の開始座標
     * @param end 線分の終了座標
     * @param offset オフセット
     * @return オフセット分シフトした線分
     */
    public static LineSegment createShifted(Point2D start, Point2D end, double offset) {
        return new LineSegment(start, end).getShiftedSegment(offset);
    }

    /**
     * 線分の交点を取得します。見つからない場合は自身の終点を返します。
     *
     * @param other もう1つの線分
     * @return 交点
     */
    public Point2D getIntersection(LineSegment other) {
        double x;
        double y;

        if (Math.abs(this.end.getX() - this.start.getX()) < EPSILON) {
            // 自身が縦線。y=Constの形
            x = this.end.getX();//x座標は決まりました。

            if (Math.abs(other.end.getX() - other.start.getX()) < EPSILON) {
                // もう一方も縦線 → 交点なし
                y = this.end.getY();
            } else {
                double tangent = other.getTangent();
                double intercept = other.start.getY() - other.start.getX() * tangent;
                y = tangent * x + intercept;
            }
        } else if(Math.abs(other.end.getX() - other.start.getX()) < EPSILON) {
            // もう一方が縦線。
            x = other.start.getX();//x座標は決まりました。
            double tangent = this.getTangent();
            double intercept = this.start.getY() - this.start.getX() * tangent;
            y = tangent * x + intercept;
        } else {
            //両方共縦線じゃない
            double tangentA = this.getTangent();
            double tangentB = other.getTangent();
            double interceptA = this.end.getY() - this.end.getX() * tangentA;
            double interceptB = other.start.getY() - other.start.getX() * tangentB;
            if (Math.abs(tangentA - tangentB) < EPSILON) {
                //２つの直線が一直線上にある。解が無数に存在してしまう場合
                //計算する意味がないのでそのまんまshiftされた値を使うだけ
                x = this.end.getX();
                y = this.end.getY();
            } else {
                x = (interceptB - interceptA) / (tangentB - tangentA) * -1;
                y = tangentA * x + interceptA;
            }
        }
        return new Point2D(x, y);
    }

    /**
     * オフセット分だけシフトした線分を取得します。
     *
     * @param offset オフセット
     * @return シフト後の線分
     */
    public LineSegment getShiftedSegment(double offset) {
        double dx = this.end.getX() - this.start.getX();
        double dy = this.end.getY() - this.start.getY();
        double length = length();
        double offsetX = offset * dy / length;
        double offsetY = -offset * dx / length;
        Point2D shiftedStart = this.start.add(offsetX, offsetY);
        Point2D shiftedEnd = this.end.add(offsetX, offsetY);

        return new LineSegment(shiftedStart, shiftedEnd);
    }

    /**
     * この線分の方向の単位ベクトルを取得します。
     *
     * @return 単位ベクトル
     */
    public Point2D getUnitVector() {
        return this.end.subtract(start).normalize();
    }

    /**
     * 線分の正接を取得します。
     *
     * @return 線分の正接
     */
    private double getTangent() {
        return (this.end.getY() - this.start.getY()) / (this.end.getX() - this.start.getX());
    }
}
