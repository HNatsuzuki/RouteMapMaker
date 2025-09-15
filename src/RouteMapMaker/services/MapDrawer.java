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
import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.MvSta;
import RouteMapMaker.models.Station;
import RouteMapMaker.models.StopMark;
import RouteMapMaker.models.TextStyle;
import RouteMapMaker.models.Train;
import RouteMapMaker.models.TrainStop;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

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
    private Dimension2D canvasSize = new Dimension2D(200, 200);

    public MapDrawer(Configuration config, GraphicsContext gc, StringProperty fontFamily) {
        this.config = config;
        this.gc = gc;
        this.stationLabelFactory = new StationLabelFactory(fontFamily);
    }

    public Dimension2D getCanvasSize() {
        return this.canvasSize;
    }

    public Dimension2D getZoomedCanvasSize() {
        return new Dimension2D(this.canvasSize.getWidth() * this.zoomRatio, this.canvasSize.getHeight() * this.zoomRatio);
    }

    public void setCanvasSize(Dimension2D size) {
        this.canvasSize = size;
    }

    public void setCanvasSize(double[] size) {
        this.canvasSize = new Dimension2D(size[0], size[1]);
    }

    public double getZoomRatio() {
        return this.zoomRatio;
    }

    public void setZoomRatio(double ratio) {
        this.zoomRatio = ratio;
    }

    /**
     * 描画開始時処理を実行します。
     */
    public void beginDraw() {
        gc.restore();
        gc.setTransform(this.zoomRatio, 0, 0, this.zoomRatio, 0, 0);
        Dimension2D canvasSize = this.getZoomedCanvasSize();
        gc.clearRect(0, 0, canvasSize.getWidth(), canvasSize.getHeight());//はじめに全領域消去
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
        gc.fillRect(0, 0, canvasSize.getWidth(), canvasSize.getHeight());

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
     * 運転系統のすべての停車駅を描画します。
     *
     * @param train 運転系統
     * @param points 計算済駅座標
     */
    public void drawStopMarks(Train train, List<Point2D> points) {
        int markSize = train.getMarkSize();
        List<TrainStop> stops = train.getStops();

        for (int i = 0; i < stops.size(); i++) {
            TrainStop stop = stops.get(i);
            //どのmarkを使うのか決める。
            StopMark mark;

            if (stop.getMark() == StopMark.OBEY_LINE) {
                mark = train.getMark();
            } else {
                mark = stop.getMark();
            }

            //以下、それぞれのマークの処理
            if (mark == StopMark.CIRCLE) {
                this.drawCircleMark(train.getMarkColor(), markSize, stop.getSta().getShiftedPoint());
            } else if (mark == StopMark.NO_DRAW) {
                //NO_DRAWなのでなにもしない。
            } else {
                //カスタムマーク
                //回転するか？
                double theta = 0;

                if (mark.isRotated()) {
                    //回転角度を計算する
                    int idx = i == 0 ? i + 1 : i;
                    Point2D d = points.get(idx).subtract(points.get(idx - 1));
                    theta = Math.atan2(d.getY(), d.getX());
                }

                this.drawCustomMark(mark, markSize, stop.getSta().getShiftedPoint(), theta);
            }
        }
    }

    /**
     * 円形の停車マークを描画します
     *
     * @param color マーク色
     * @param size 描画サイズ (正方形の一辺の長さ = 円の直径)
     * @param position 描画位置
     */
    public void drawCircleMark(Color color, double size, Point2D position) {
        gc.setFill(color);
        // 引数の position は円の中心で、fillOval に渡す位置は左上の座標のため、半径分だけシフトする
        Point2D drawPosition = position.subtract(size / 2, size / 2);
        gc.fillOval(drawPosition.getX(), drawPosition.getY(), size, size);
    }

    /**
     * カスタム停車マークを描画します。
     *
     * @param mark 描画するマーク
     * @param size 描画サイズ (正方形の一辺の長さ)
     * @param position 描画位置
     * @param angle 描画角度 (rad)
     */
    public void drawCustomMark(StopMark mark, double size, Point2D position, double angle) {
        gc.save();
        gc.translate(position.getX(), position.getY());
        gc.rotate(angle * 180 / Math.PI);
        gc.translate(size * -0.5, size * -0.5);

        for (int i = mark.getLayers().size() - 1; 0 <= i; i--) {
            MarkLayer layer = mark.getLayers().get(i);

            switch (layer.getType()) {
                case MarkLayer.OVAL:
                    this.drawOvalLayer(layer, size);
                    break;
                case MarkLayer.RECT:
                    this.drawRectangleLayer(layer, size);
                    break;
                case MarkLayer.LINE:
                    this.drawLineLayer(layer, size);
                    break;
                case MarkLayer.ARC:
                    this.drawArcLayer(layer, size);
                    break;
                case MarkLayer.TEXT:
                    this.drawTextLayer(layer, size);
                    break;
                case MarkLayer.IMAGE:
                    this.drawImageLayer(layer, size);
                    break;
                default:
                    throw new IllegalArgumentException("不正なレイヤーです。");
            }
        }

        gc.restore();
    }

    /**
     * 楕円レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawOvalLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.OVAL) {
            throw new IllegalArgumentException("楕円レイヤーではありません。");
        }

        double[] params = layer.getParamProperty().stream().limit(5).mapToDouble(p -> p.get() * size).toArray();

        switch (layer.getPaint()) {
            case MarkLayer.FILL:
                gc.setFill(layer.getColor());
                gc.fillOval(params[0], params[1], params[2], params[3]);
                break;
            case MarkLayer.STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(params[4]);
                gc.fillOval(params[0], params[1], params[2], params[3]);
                break;
            default:
                throw new IllegalArgumentException("不正なレイヤーです。");
        }
    }

    /**
     * 矩形レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawRectangleLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.RECT) {
            throw new IllegalArgumentException("矩形レイヤーではありません。");
        }

        double[] params = layer.getParamProperty().stream().limit(7).mapToDouble(p -> p.get() * size).toArray();

        switch (layer.getPaint()) {
            case MarkLayer.FILL:
                gc.setFill(layer.getColor());
                gc.fillRoundRect(params[0], params[1], params[2], params[3], params[4], params[5]);
                break;
            case MarkLayer.STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(params[6]);
                gc.fillRoundRect(params[0], params[1], params[2], params[3], params[4], params[5]);
                break;
            default:
                throw new IllegalArgumentException("不正なレイヤーです。");
        }
    }

    /**
     * 線分レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawLineLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.LINE) {
            throw new IllegalArgumentException("線分レイヤーではありません。");
        }

        double[] params = layer.getParamProperty().stream().limit(5).mapToDouble(p -> p.get() * size).toArray();
        gc.setStroke(layer.getColor());
        gc.setLineWidth(params[4]);
        gc.setLineCap((int)layer.getParam(5) == 1 ? StrokeLineCap.ROUND : StrokeLineCap.SQUARE);
        gc.strokeLine(params[0], params[1], params[2], params[3]);
    }

    /**
     * 円弧レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawArcLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.ARC) {
            throw new IllegalArgumentException("円弧レイヤーではありません。");
        }

        double x = layer.getParam(0) * size;
        double y = layer.getParam(1) * size;
        double width = layer.getParam(2) * size;
        double height = layer.getParam(3) * size;
        double startAngle = layer.getParam(4);
        double extent = layer.getParam(5);
        double lineWidth = layer.getParam(6) * size;

        ArcType arcType;

        switch ((int)layer.getParam(7)) {
            case 1:
                arcType = ArcType.OPEN;
                break;
            case 2:
                arcType = ArcType.ROUND;
                break;
            default:
                arcType = ArcType.CHORD;
                break;
        }

        switch (layer.getPaint()) {
            case MarkLayer.FILL:
                gc.setFill(layer.getColor());
                gc.fillArc(x, y, width, height, startAngle, extent, arcType);
                break;
            case MarkLayer.STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(lineWidth);
                gc.strokeArc(x, y, width, lineWidth, startAngle, extent, arcType);
                break;
            default:
                throw new IllegalArgumentException("不正なレイヤーです。");
        }
    }

    /**
     * 文字列レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawTextLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.TEXT) {
            throw new IllegalArgumentException("文字列レイヤーではありません。");
        }

        double[] params = layer.getParamProperty().stream().limit(4).mapToDouble(p -> p.get() * size).toArray();

        Font font;

        switch ((int)layer.getParam(4)) {
            case 0:
                //NORMAL
                font = Font.font(layer.getFontName(), FontWeight.NORMAL, FontPosture.REGULAR, params[2]);
                break;
            case 1:
                //BOLD
                font = Font.font(layer.getFontName(), FontWeight.BOLD, FontPosture.REGULAR, params[2]);
                break;
            case 2: 
                //ITALIC
                font = Font.font(layer.getFontName(), FontWeight.NORMAL, FontPosture.ITALIC, params[2]);
                break;
            case 3:
                //BOLD_ITALIC
                font = Font.font(layer.getFontName(), FontWeight.BOLD, FontPosture.ITALIC, params[2]);
                break;
            default:
                font = Font.getDefault();
                break;
        }

        switch (layer.getPaint()) {
            case MarkLayer.FILL:
                gc.setFill(layer.getColor());
                gc.setFont(font);
                gc.fillText(layer.getText(), params[0], params[1]);
                break;
            case MarkLayer.STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(params[3]);
                gc.setFont(font);
                gc.strokeText(layer.getText(), params[0], params[1]);
                break;
            default:
                throw new IllegalArgumentException("不正なレイヤーです。");
        }
    }

    /**
     * 画像レイヤーを描画します。
     *
     * @param layer レイヤー
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    private void drawImageLayer(MarkLayer layer, double size) {
        if (layer.getType() != MarkLayer.IMAGE) {
            throw new IllegalArgumentException("画像レイヤーではありません。");
        }

        double[] params = layer.getParamProperty().stream().limit(4).mapToDouble(p -> p.get() * size).toArray();
        gc.drawImage(layer.getImage(), params[0], params[1], params[2], params[3]);
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
        double height = canvasSize.getHeight();

        for (int y = 0; y * intervalY < height; ++y) {
            //横線
            gc.strokeLine(0, y * intervalY, canvasSize.getWidth(), y * intervalY);
        }

        int start_idx = (int)(Math.ceil(height / interval / sqrt3));
        // 斜め 傾き負線
        for (double x = -1 * start_idx * interval; x < canvasSize.getWidth(); x += interval) {
            gc.strokeLine(x, 0, x + height / sqrt3, height);
        }
        // 斜め 傾き正線
        for (double x = 0; x < canvasSize.getWidth() + height / sqrt3; x += interval) {
            gc.strokeLine(x - height / sqrt3, height, x , 0);
        }
    }

    /**
     * 四角形グリッドを描画します。
     *
     * @param interval グリッド間隔
     */
    private void drawRectangleGrid(int interval) {
        for (int y = 0; y < canvasSize.getHeight(); y += interval) {
            //横線
            gc.strokeLine(0, y, canvasSize.getWidth(), y);
        }

        for (int x = 0; x < canvasSize.getWidth(); x += interval) {
            //縦線
            gc.strokeLine(x, 0, x, canvasSize.getHeight());
        }
    }
}
