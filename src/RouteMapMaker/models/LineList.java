package RouteMapMaker.models;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 路線のリストを表すクラスです。
 */
public class LineList implements List<Line> {
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
