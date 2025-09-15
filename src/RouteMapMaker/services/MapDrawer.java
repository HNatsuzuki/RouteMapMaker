package RouteMapMaker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import RouteMapMaker.factories.StationLabelFactory;
import RouteMapMaker.models.Background;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.LineList;
import RouteMapMaker.models.LineSegment;
import RouteMapMaker.models.MvSta;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.TextStyle;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 描画系処理をまとめたクラスです。
 */
public class MapDrawer {
    private static final Color GRID_COLOR = Color.LAVENDER;
    private static final double GRID_STROKE_WIDTH = 1;
    private static final Color SELECTED_STATION_POINT_COLOR = Color.RED;
    private static final double STATION_POINT_RADIUS = 3;
    private static final double LINE_WIDTH = 2;
    private static final Color LINE_COLOR = Color.BLACK;
    private static final Color SELECTED_LINE_COLOR = Color.PERU;
    private final Configuration config;
    private final GraphicsContext gc;
    private final StationLabelFactory stationLabelFactory;
    private double zoomRatio = 1.0;
    private Point2D canvasSize = new Point2D(200, 200);

    public MapDrawer(Configuration config, GraphicsContext gc, StringProperty fontFamily) {
        this.config = config;
        this.gc = gc;
        this.stationLabelFactory = new StationLabelFactory(fontFamily);
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
     * 初期化処理を実行します。
     */
    public void initialize() {
        gc.save();
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
     * 編集モードでの路線を結ぶ線を描画します。
     *
     * @param lineList 路線のリスト
     * @param selectedLine 選択中の路線
     */
    public void drawLinesInEditMode(LineList lineList, Line selectedLine) {
        gc.setLineWidth(LINE_WIDTH);

        for (Line line : lineList) {
            Point2D startP;
            Point2D endP;
            //選択中の路線だけ色を変える
            gc.setStroke(line == selectedLine ? SELECTED_LINE_COLOR : LINE_COLOR);
            line.interpolateIntermediatePoints();
            List<Station> stations = line.getStations();
            //まずは始点での処理。
            List<Line.Connection> fixedStations = line.getConnections().stream().
                    filter(c -> c.getStation().isSet()).collect(Collectors.toList());
            startP = stations.get(0).getPoint2D();
            gc.beginPath();
            gc.moveTo(startP.getX(), startP.getY());

            for (int i = 1; i < stations.size(); i++) {
                if (!stations.get(i).isSet()) {
                    // 座標非固定点はスキップ
                    continue;
                }

                endP = stations.get(i).getPoint2D();

                if (line.getCurveConnection(i) && line.isCurvable(i)) {
                    // ベジエ曲線での接続
                    int idx = fixedStations.indexOf(line.getConnections().get(i));
                    LineSegment l1 = new LineSegment(fixedStations.get(idx - 2).getStation().getPoint2D(), startP);
                    LineSegment l2 = new LineSegment(endP, fixedStations.get(idx + 1).getStation().getPoint2D());
                    Point2D cp = l1.getIntersection(l2); //control point
                    gc.quadraticCurveTo(cp.getX(), cp.getY(), endP.getX(), endP.getY());
                } else {
                    // 直線での接続
                    gc.lineTo(endP.getX(), endP.getY());
                }

                startP = endP;
            }

            gc.stroke();
        }
    }

    /**
     * 路線に含まれるすべての駅名を描画します。
     *
     * @param lineList 路線リスト
     * @param isEditMode 編集モードかどうか
     */
    public void drawStationNames(LineList lineList, boolean isEditMode) {
        Set<Station> drawnStations = new HashSet<>();
        //駅名描画
        for (Line l : lineList) {
            for (Station station : l.getStations()) {
                if (drawnStations.contains(station)) {
                    // すでに描画されている場合は描画しない。
                    continue;
                }

                stationLabelFactory.createLabel(station, l, isEditMode)
                    .ifPresent(label -> {
                        drawText(label.getText(), label.getPosition(), label.getStyle());
                        drawnStations.add(station);
                    });
            }
        }
    }

    /**
     * 路線に含まれるすべての駅の点を描画します。
     *
     * @param lineList 路線リスト
     * @param movingStations 移動中の駅
     */
    public void drawStationPoints(LineList lineList, List<MvSta> movingStations) {
        for (Line line : lineList) {
            for (Station station : line.getStations()) {
                boolean isSelected = movingStations.stream().map(MvSta::getStation).anyMatch(s -> s == station);

                if(isSelected) {
                    // 選択中
                    gc.setFill(SELECTED_STATION_POINT_COLOR);
                } else if(station.isSet()) {
                    //座標固定されている
                    gc.setFill(config.getFixedColor());
                } else {
                    //座標固定されていない
                    gc.setFill(config.getNonFixedColor());
                }

                double[] point = station.getPointUS();
                gc.fillOval(point[0] - STATION_POINT_RADIUS, point[1] - STATION_POINT_RADIUS, STATION_POINT_RADIUS * 2, STATION_POINT_RADIUS * 2);
            }
        }
    }

    /**
     * 文字列を描画します。
     *
     * @param text 文字列
     * @param position 位置
     * @param style スタイル
     */
    public void drawText(String text, Point2D position, TextStyle style) {
        gc.setFont(style.getFont());
        gc.setFill(style.getColor());
        gc.setTextAlign(style.getTextAlignment());
        gc.setTextBaseline(style.getVPos());
        gc.fillText(text, position.getX() + style.getHorizontalOffset(), position.getY());
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
