package RouteMapMaker.commands;

import java.util.List;

/**
 * undo, redo が可能な、リストに値を追加するコマンドです。
 */
public class AddListItemCommand<T> implements Command {
    private final List<T> list;
    private final int index;
    private final T item;

    /**
     * コンストラクタ
     *
     * @param list 追加するリスト
     * @param item 追加する値
     */
    public AddListItemCommand(List<T> list, T item) {
        this.list = list;
        this.index = list.size();
        this.item = item;
    }

    /**
     * コンストラクタ
     *
     * @param list 追加するリスト
     * @param index 追加するリストのインデックス
     * @param item 追加する値
     */
    public AddListItemCommand(List<T> list, int index, T item) {
        this.list = list;
        this.index = index;
        this.item = item;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        this.list.add(index, item);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        this.list.remove(index);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
