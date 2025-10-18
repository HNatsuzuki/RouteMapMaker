package RouteMapMaker.models;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import RouteMapMaker.factories.LineFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 路線のリストを表すクラスです。
 */
public class LineList implements List<Line> {
    /** 新規追加時のX方向オフセット */
    public static final double INITIAL_LINE_OFFSET_X = 50;
    /** 新規追加時のY方向オフセット */
    public static final double INITIAL_LINE_OFFSET_Y = 50;
    private final ObservableList<Line> lines;

    /**
     * コンストラクタ
     */
    public LineList() {
        this.lines = FXCollections.observableArrayList();
    }

    /**
     * コンストラクタ
     *
     * @param lines リストに追加する路線
     */
    public LineList(Collection<? extends Line> lines) {
        this.lines = FXCollections.observableArrayList(lines);
    }

    /**
     * 変更通知可能なリストを返します。
     *
     * @return 変更通知可能なリスト
     */
    public ObservableList<Line> asObservableList() {
        return FXCollections.unmodifiableObservableList(this.lines);
    }

    /**
     * 新たに路線を作成し、リストに追加します。
     *
     * @param name 路線名
     * @return 新たに作成した路線
     */
    public Line createAndAddLine(String name) {
        Point2D maxPoint = getMaxPoint();
        // 基準位置はX座標は固定、Y座標はすべての位置の最大値からオフセット分ずらす
        Point2D base = new Point2D(INITIAL_LINE_OFFSET_X, maxPoint.getY() + INITIAL_LINE_OFFSET_Y);
        Line line = LineFactory.create(name, base.getX(), base.getY());
        this.add(line);

        return line;
    }

    /**
     * 指定した名称の駅が存在するかどうか判定します。
     *
     * @param name 駅名
     * @return 指定した名称の駅が存在する場合 true
     */
    public boolean hasStationWithName(String name) {
        return this.lines.stream()
            .flatMap(l -> l.getStations().stream())
            .anyMatch(s -> s.getName().equals(name));
    }

    /**
     * 名称が一致する駅を探します。
     *
     * @param name 検索する駅名
     * @return 指定した駅名に一致する駅。
     */
    public Optional<Station> findStationByName(String name) {
        return this.lines.stream()
            .flatMap(l -> l.getStations().stream())
            .filter(s -> s.getName().equals(name))
            .findFirst();
    }

    /**
     * 指定した範囲内にある駅を探します。
     *
     * @param x 左上のx座標
     * @param y 左上のy座標
     * @param width エリアの幅
     * @param height エリアの高さ
     * @return 範囲内に含まれる駅のリスト
     */
    public List<Station> findStationsByArea(double x, double y, double width, double height) {
        double right = x + width;
        double bottom = y + height;

        return getStations().stream()
            .filter(s -> s.isSet())
            .filter(s -> {
                var p = s.getPoint2D();
                return p.getX() >= x && p.getX() <= right && p.getY() >= y && p.getY() <= bottom;
            }).collect(Collectors.toList());
    }

    /**
     * すべての駅を取得します。
     *
     * @return 駅のリスト
     */
    public List<Station> getStations() {
        return lines.stream().flatMap(l -> l.getStations().stream()).distinct().collect(Collectors.toList());
    }

    /**
     * 全路線に含まれる駅の最大の座標を取得します。
     *
     * @return 最大の座標。一つも存在しない場合は (0, 0) を返します。
     */
    public Point2D getMaxPoint() {
        Point2D point;
        List<Station> stations = this.lines.stream().flatMap(l -> l.getStations().stream()).filter(Station::isSet).collect(Collectors.toList());
        OptionalDouble maxX = stations.stream().mapToDouble(s -> s.getPoint()[0]).max();
        OptionalDouble maxY = stations.stream().mapToDouble(s -> s.getPoint()[1]).max();

        if (maxX.isPresent() && maxY.isPresent()) {
            point = new Point2D(maxX.getAsDouble(), maxY.getAsDouble());
        } else {
            point = new Point2D(0, 0);
        }

        return point;
    }

    @Override
    public boolean add(Line line) {
        return this.lines.add(line);
    }

    @Override
    public void add(int index, Line line) {
        this.lines.add(index, line);
    }

    @Override
    public boolean addAll(Collection<? extends Line> lines) {
        return this.lines.addAll(lines);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Line> lines) {
        return this.lines.addAll(index, lines);
    }

    @Override
    public void clear() {
        this.lines.clear();
    }

    @Override
    public boolean contains(Object other) {
        return this.lines.contains(other);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.lines.containsAll(collection);
    }

    @Override
    public boolean equals(Object object) {
        return this.lines.equals(object);
    }

    @Override
    public void forEach(Consumer<? super Line> action) {
        this.lines.forEach(action);
    }

    @Override
    public Line get(int index) {
        return this.lines.get(index);
    }

    @Override
    public int hashCode() {
        return this.lines.hashCode();
    }

    @Override
    public int indexOf(Object object) {
        return this.lines.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    @Override
    public Iterator<Line> iterator() {
        return this.lines.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return this.lines.lastIndexOf(object);
    }

    @Override
    public ListIterator<Line> listIterator() {
        return this.lines.listIterator();
    }

    @Override
    public ListIterator<Line> listIterator(int index) {
        return this.lines.listIterator(index);
    }

    @Override
    public Stream<Line> parallelStream() {
        return this.lines.parallelStream();
    }

    @Override
    public Line remove(int index) {
        return this.lines.remove(index);
    }

    @Override
    public boolean remove(Object object) {
        return this.lines.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.lines.removeAll(collection);
    }

    @Override
    public boolean removeIf(Predicate<? super Line> filter) {
        return this.lines.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.lines.retainAll(collection);
    }

    @Override
    public Line set(int index, Line line) {
        return this.lines.set(index, line);
    }

    @Override
    public int size() {
        return this.lines.size();
    }

    @Override
    public Stream<Line> stream() {
        return this.lines.stream();
    }

    @Override
    public List<Line> subList(int fromIndex, int toIndex) {
        return this.lines.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.lines.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return this.lines.toArray(array);
    }
}
