package RouteMapMaker.services;

import RouteMapMaker.models.MarkLayer;
import RouteMapMaker.models.StopMark;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * カスタム停車マーク描画用クラスです。
 */
public class CustomMarkDrawer {
    private final GraphicsContext gc;

    public CustomMarkDrawer(GraphicsContext gc) {
        this.gc = gc;
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
     * カスタム停車マークをプレビュー用に描画します。
     *
     * @param mark 描画するマーク
     * @param size 描画サイズ (正方形の一辺の長さ)
     */
    public void drawCustomMarkPreview(StopMark mark, double size) {
        // はじめに全領域消去
        gc.clearRect(0, 0, size, size);
        // 描画サイズの中心を描画位置にする
        Point2D position = new Point2D(size / 2, size / 2);
        this.drawCustomMark(mark, size, position, 0);
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

        switch (layer.getPaintMode()) {
            case FILL:
                gc.setFill(layer.getColor());
                gc.fillOval(params[0], params[1], params[2], params[3]);
                break;
            case STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(params[4]);
                gc.strokeOval(params[0], params[1], params[2], params[3]);
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

        switch (layer.getPaintMode()) {
            case FILL:
                gc.setFill(layer.getColor());
                gc.fillRoundRect(params[0], params[1], params[2], params[3], params[4], params[5]);
                break;
            case STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(params[6]);
                gc.strokeRoundRect(params[0], params[1], params[2], params[3], params[4], params[5]);
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

        switch (layer.getPaintMode()) {
            case FILL:
                gc.setFill(layer.getColor());
                gc.fillArc(x, y, width, height, startAngle, extent, arcType);
                break;
            case STROKE:
                gc.setStroke(layer.getColor());
                gc.setLineWidth(lineWidth);
                gc.strokeArc(x, y, width, height, startAngle, extent, arcType);
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

        switch (layer.getPaintMode()) {
            case FILL:
                gc.setFill(layer.getColor());
                gc.setFont(font);
                gc.fillText(layer.getText(), params[0], params[1]);
                break;
            case STROKE:
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

}
