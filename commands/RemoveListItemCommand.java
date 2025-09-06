package RouteMapMaker.commands;

import java.util.List;

/**
 * undo, redo が可能な、リストから値を削除するコマンドです。
 */
public class RemoveListItemCommand<T> implements Command {
    private final List<T> list;
    private final int index;
    private final T item;

    /**
     * コンストラクタ
     *
     * @param list 削除するリスト
     * @param index 削除するリストのインデックス
     */
    public RemoveListItemCommand(List<T> list, int index) {
        this(list, index, list.get(index));
    }

    /**
     * コンストラクタ
     *
     * @param list 削除するリスト
     * @param index 削除するリストのインデックス
     * @param item 削除する値
     */
    public RemoveListItemCommand(List<T> list, int index, T item) {
        this.list = list;
        this.index = index;
        this.item = item;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        this.list.remove(index);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        this.list.add(index, item);;
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
