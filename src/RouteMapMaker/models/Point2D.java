package RouteMapMaker.models;

import java.util.Objects;

/**
 * 2次元座標を表すクラスです。
 */
public class Point2D {
    private final double x;
    private final double y;
    private final int hash;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;

        // ハッシュ値をキャッシュしておく
        hash = Objects.hash(x, y);
    }

    /**
     * 2点の距離を計算します。
     *
     * @param other もう1点
     * @return 距離
     */
    public double distance(Point2D other) {
        double dx = other.x - x;
        double dy = other.y - y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 位置ベクトルとしてみたときの大きさを取得します。
     *
     * @return ベクトルの大きさ
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * 位置ベクトルを単位ベクトル化します。
     *
     * @return 単位ベクトル
     */
    public Point2D normalize() {
        double length = length();

        if (length == 0) {
            // 大きさがゼロのときはゼロを返す
            return new Point2D(0, 0);
        }

        return new Point2D(x / length, y / length);
    }

    /**
     * 加算します。
     *
     * @param other 加算する点
     * @return 加算後の点
     */
    public Point2D add(Point2D other) {
        return new Point2D(x + other.x, y + other.y);
    }

    /**
     * 加算します。
     *
     * @param x 加算するx値
     * @param y 加算するy値
     * @return 加算後の点
     */
    public Point2D add(double x, double y) {
        return new Point2D(this.x + x, this.y + y);
    }

    /**
     * 減算します。
     *
     * @param other 減算する点
     * @return 減算後の点
     */
    public Point2D subtract(Point2D other) {
        return new Point2D(x - other.x, y - other.y);
    }

    /**
     * 減算します。
     *
     * @param x 減算するx値
     * @param y 減算するy値
     * @return 減算後の点
     */
    public Point2D subtract(double x, double y) {
        return new Point2D(this.x - x, this.y - y);
    }

    /**
     * 係数を乗算します。
     *
     * @param factor 係数
     * @return 乗算後の点
     */
    public Point2D multiply(double factor) {
        return new Point2D(x * factor, y * factor);
    }

    /** X座標 */
    public double getX() {
        return x;
    }

    /** Y座標 */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            // インスタンスが同じ
            return true;
        } else if (!(other instanceof Point2D)) {
            // 型が異なる
            return false;
        } else {
            var point = (Point2D)other;

            // 座標の完全一致で判定する
            // 誤差を考慮する場合は distance で比較する
            return x == point.x && y == point.y;
        }
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
