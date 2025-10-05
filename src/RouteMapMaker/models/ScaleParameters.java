package RouteMapMaker.models;

/**
 * 拡大縮小のパラメータ用クラスです。
 */
public class ScaleParameters implements TransformParameters {
    private final double scaleX;
    private final double scaleY;
    private final double pivotX;
    private final double pivotY;
    private final boolean transformWithFreeItem;

    public ScaleParameters(double scaleX, double scaleY, double pivotX, double pivotY, boolean transformWithFreeItem) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.transformWithFreeItem = transformWithFreeItem;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public double getPivotX() { 
        return pivotX;
    }

    public double getPivotY() {
        return pivotY;
    }

    public boolean isTransformWithFreeItem() {
        return transformWithFreeItem;
    }
}
