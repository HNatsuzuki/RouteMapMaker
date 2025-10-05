package RouteMapMaker.commands;

import java.util.HashSet;
import java.util.Set;

import RouteMapMaker.models.Line;
import RouteMapMaker.models.LineList;
import RouteMapMaker.models.Station;

/**
 * undo, redo が可能な、路線全体を移動するコマンドです。
 */
public class TranslateLineStationsCommand implements Command {
    private final LineList lineList;
    private final double translateX;
    private final double translateY;

    public TranslateLineStationsCommand(LineList lineList, double translateX, double translateY) {
        this.lineList = lineList;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    /**
     * 平行移動を行います。
     *
     * @param x x方向の移動量
     * @param y y方向の移動量
     */
    private void translate(double x, double y) {
        Set<Station> translatedStations = new HashSet<>();

        for (Line line : lineList) {
            for (Station station: line.getStations()) {
                if (translatedStations.contains(station)) {
                    // 移動済みは処理しない
                    continue;
                }

                station.translate(x, y);
                translatedStations.add(station);
            }
        }
    }
    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        translate(translateX, translateY);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        translate(-translateX, -translateY);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
