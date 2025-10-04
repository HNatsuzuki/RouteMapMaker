package RouteMapMaker.models;

/**
 * 平行移動のパラメータ用クラスです。
 */
public class TranslateParameters implements TransformParameters {
    private final double x;
    private final double y;
    private final boolean transformWithFreeItem;

    public TranslateParameters(double x, double y, boolean transformWithFreeItem) {
        this.x = x;
        this.y = y;
        this.transformWithFreeItem = transformWithFreeItem;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isTransformWithFreeItem() {
        return transformWithFreeItem;
    }
}
