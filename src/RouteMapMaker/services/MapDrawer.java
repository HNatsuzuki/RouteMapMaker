package RouteMapMaker.services;

import RouteMapMaker.models.Background;
import RouteMapMaker.models.Configuration;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * 描画系処理をまとめたクラスです。
 */
public class MapDrawer {
    private static final Color GRID_COLOR = Color.LAVENDER;
    private static final double GRID_STROKE_WIDTH = 1;
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

    /**
     * グリッド線を描画します。
     */
    public void drawGrid() {
        // グリッド表示OFF→return
        if (!config.getR_grid()) {
            return;
        }

        int interval = config.getR_gridInterval();
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(GRID_STROKE_WIDTH);

        if (config.isGridTriangle()) {
            // 三角形グリッド
            drawTriangleGrid(interval);
        } else {
            // 四角形グリッド
            drawRectangleGrid(interval);
        }
    }

    /**
     * 三角形のグリッドを描画します。
     *
     * @param interval グリッド間隔
     */
    private void drawTriangleGrid(int interval) {
        double sqrt3 = Math.sqrt(3);
        double intervalY = interval * sqrt3 / 2;
        double height = canvasSize.getY();

        for (int y = 0; y * intervalY < height; ++y) {
            //横線
            gc.strokeLine(0, y * intervalY, canvasSize.getX(), y * intervalY);
        }

        int start_idx = (int)(Math.ceil(height / interval / sqrt3));
        // 斜め 傾き負線
        for (double x = -1 * start_idx * interval; x < canvasSize.getX(); x += interval) {
            gc.strokeLine(x, 0, x + height / sqrt3, height);
        }
        // 斜め 傾き正線
        for (double x = 0; x < canvasSize.getX() + height / sqrt3; x += interval) {
            gc.strokeLine(x - height / sqrt3, height, x , 0);
        }
    }

    /**
     * 四角形グリッドを描画します。
     *
     * @param interval グリッド間隔
     */
    private void drawRectangleGrid(int interval) {
        for (int y = 0; y < canvasSize.getY(); y += interval) {
            //横線
            gc.strokeLine(0, y, canvasSize.getX(), y);
        }

        for (int x = 0; x < canvasSize.getX(); x += interval) {
            //縦線
            gc.strokeLine(x, 0, x, canvasSize.getY());
        }
    }
}
