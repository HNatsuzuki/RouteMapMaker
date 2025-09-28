package RouteMapMaker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import RouteMapMaker.factories.StationLabelFactory;
import RouteMapMaker.models.Background;
import RouteMapMaker.models.Configuration;
import RouteMapMaker.models.FreeItem;
import RouteMapMaker.models.Line;
import RouteMapMaker.models.LineList;
import RouteMapMaker.models.LineSegment;
import RouteMapMaker.models.MvSta;
import RouteMapMaker.models.PaintMode;
import RouteMapMaker.models.PathSegment;
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
    private final CustomMarkDrawer customMarkDrawer;
    private double zoomRatio = 1.0;
    private Dimension2D canvasSize = new Dimension2D(200, 200);
    private final TrainPathCalculator trainPathCalculator = new TrainPathCalculator();

    public MapDrawer(Configuration config, GraphicsContext gc, StringProperty fontFamily) {
        this.config = config;
        this.gc = gc;
        this.stationLabelFactory = new StationLabelFactory(fontFamily);
        this.customMarkDrawer = new CustomMarkDrawer(gc);
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
     */
    public void drawStopMarks(Train train) {
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
                this.drawCircleMark(train.getMarkColor(), markSize, stop.getPosition());
            } else if (mark == StopMark.NO_DRAW) {
                //NO_DRAWなのでなにもしない。
            } else {
                //カスタムマーク
                //回転するか？
                double theta = mark.isRotated() ? stop.getAngle() : 0;
                this.customMarkDrawer.drawCustomMark(mark, markSize, stop.getPosition(), theta);
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
     * すべての FreeItem を描画します。
     *
     * @param freeItems 描画する FreeItem のリスト
     */
    public void drawFreeItems(List<FreeItem> freeItems) {
        //下から順番に。
        for (int i = freeItems.size() - 1; i >= 0; --i){
            FreeItem item = freeItems.get(i);

            //freeItemでは回転をリストアしないと前の回転がどんどん溜まっていく。zoomの再設定も必要。
            gc.restore();
            gc.setTransform(getZoomRatio(), 0, 0, getZoomRatio(), 0, 0);
            rotate(item.getRotation(), item.getX(), item.getY());

            switch (item.getType()) {
                case FreeItem.IMAGE:
                    drawFreeImageItem(item);
                    break;
                case FreeItem.TEXT:
                    drawFreeTextItem(item);
                    break;
                default:
                    throw new UnsupportedOperationException("タイプ " + item.getType() + " の描画処理が実装されていません。");
            }
        }
    }

    /**
     * 画像の FreeItem を描画します。
     *
     * @param item 描画するアイテム
     */
    private void drawFreeImageItem(FreeItem item) {
        if (item.getType() != FreeItem.IMAGE) {
            throw new IllegalArgumentException("画像の FreeItem ではありません。");
        }

        gc.drawImage(item.getImage(), item.getX(), item.getY(), item.getWidth(), item.getHeight());
    }

    /**
     * テキストの FreeItem を描画します。
     *
     * @param item 描画するアイテム
     */
    private void drawFreeTextItem(FreeItem item) {
        if (item.getType() != FreeItem.TEXT) {
            throw new IllegalArgumentException("テキストの FreeItem ではありません。");
        }

        //文字スタイルの設定
        switch (item.getFontStyle()) {
            case NORMAL:
                gc.setFont(Font.font(item.getFontName(), FontWeight.NORMAL, FontPosture.REGULAR, item.getSize()));
                break;
            case BOLD:
                gc.setFont(Font.font(item.getFontName(), FontWeight.BOLD, FontPosture.REGULAR, item.getSize()));
                break;
            case ITALIC:
                gc.setFont(Font.font(item.getFontName(), FontWeight.NORMAL, FontPosture.ITALIC, item.getSize()));
                break;
            case BOLD_ITALIC:
                gc.setFont(Font.font(item.getFontName(), FontWeight.BOLD, FontPosture.ITALIC, item.getSize()));
                break;
        }

        String text = item.isVertical()
            ? item.getText().chars().mapToObj(c -> String.valueOf((char)c)).collect(Collectors.joining("\n"))
            : item.getText();

        if (item.getPaintMode() == PaintMode.FILL) {
            gc.setFill(item.getColor());
            gc.fillText(text, item.getX(), item.getY());
        } else if (item.getPaintMode() == PaintMode.STROKE){
            gc.setStroke(item.getColor());
            gc.setLineWidth(item.getLineWidth());
            gc.strokeText(text, item.getX(), item.getY());
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
     * 路線図モード (運転系統編集モード) での路線を結ぶ線を描画します。
     *
     * @param lineList 路線のリスト
     */
    public void drawLinesInMapMode(LineList lineList) {
        for (int i = lineList.size() - 1; i >= 0; --i) {
            //路線ごとに処理
            Line line = lineList.get(i);
            List<Train> trains = line.getTrains();

            for (int j = trains.size() - 1; j >= 0; --j) {
                //系統ごとに処理。降順に処理していく。
                Train train = trains.get(j);
                List<TrainStop> stops = train.getStops();

                if(stops.size() < 2) {
                    // 0駅もしくは1駅しか登録されてない系統は無視 (2点以上ないと線にならない)
                    continue;
                }

                List<PathSegment> stationPoints = trainPathCalculator.calculate(line, train);
                //線の描画処理
                drawTrainPath(train, stationPoints);
                //上書きの問題があってやはりmarkは線を書き終わってからにしよう。
                drawStopMarks(train);
            }
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
     * 運転系統の線を描画します。
     *
     * @param train 運転系統
     * @param segments 描画するセグメントのリスト
     */
    public void drawTrainPath(Train train, List<PathSegment> segments) {
        //線の描画処理
        gc.setStroke(train.getLineColor());
        gc.setLineWidth(train.getLineWidth());
        gc.setLineDashes(train.getLineDash().get());
        gc.setFill(train.getMarkColor());
        gc.beginPath();

        for (int i = 0; i < segments.size(); ++i) {
            Point2D p = segments.get(i).getPoint();
            Point2D cp = segments.get(i).getControlPoint();

            if (i == 0) {
                //始点
                gc.moveTo(p.getX(), p.getY());
            } else if(cp != null) {
                // 曲線
                gc.quadraticCurveTo(cp.getX(), cp.getY(), p.getX(), p.getY());
            } else {
                // 直線での接続
                gc.lineTo(p.getX(), p.getY());
            }
        }

        gc.stroke();
        //破線設定の後処理
        gc.setLineDashes(null);
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

    /**
     * 回転します。
     *
     * @param angle 回転角度 (degree)
     * @param x 回転中心のx座標
     * @param y 回転中心のy座標
     */
    private void rotate(double angle, double x, double y) {
        //アフィン変換で回転。そのまま回転だと原点中心になっちゃうので行列計算。
        double r = Math.toRadians(angle);
        double st = Math.sin(r);
        double ct = Math.cos(r);
        gc.transform(ct, st, -st, ct,
                x - x * ct + y * st,
                y - x * st - y * ct);
    }
}
