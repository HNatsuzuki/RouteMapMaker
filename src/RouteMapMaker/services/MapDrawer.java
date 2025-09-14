package RouteMapMaker.services;

import RouteMapMaker.models.Background;
import RouteMapMaker.models.Configuration;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 描画系処理をまとめたクラスです。
 */
public class MapDrawer {
    private final Configuration config;
    private final GraphicsContext gc;
    private double zoomRatio = 1.0;
    private Point2D canvasSize = new Point2D(200, 200);

    public MapDrawer(Configuration config, GraphicsContext gc) {
        this.config = config;
        this.gc = gc;
    }

    public Point2D getCanvasSize() {
        return this.canvasSize;
    }

    public void setCanvasSize(Point2D size) {
        this.canvasSize = size;
    }

    public void setCanvasSize(double[] size) {
        this.canvasSize = new Point2D(size[0], size[1]);
    }

    public double getZoomRatio() {
        return this.zoomRatio;
    }

    public void setZoomRatio(double ratio) {
        this.zoomRatio = ratio;
    }

    /**
     * 背景を描画します。
     *
     * @param background 描画する背景
     */
    public void drawBackground(Background background) {
        // 画像あるナシに関わらず背景色を設定
        gc.setFill(background.getColor());
        gc.fillRect(0, 0, canvasSize.getX(), canvasSize.getY());

        if (background.getImage() != null) {
            // 背景画像
            double r = zoomRatio * background.getZoomRatio() / 100;
            gc.setTransform(r, 0, 0, r, background.getX() * zoomRatio, background.getY() * zoomRatio);
            gc.setGlobalAlpha(1 - background.getOpacity() / 100.0);
            gc.drawImage(background.getImage(), 0, 0);
            gc.setTransform(zoomRatio, 0, 0, zoomRatio, 0, 0);
            gc.setGlobalAlpha(1.0);
        }
    }
}
