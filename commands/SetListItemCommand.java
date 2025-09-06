package RouteMapMaker.commands;

import java.util.List;

/**
 * undo, redo が可能な、リストの要素に値を設定するコマンドです。
 */
public class SetListItemCommand<T> implements Command {
    private final List<T> list;
    private final int index;
    private final T oldValue;
    private final T newValue;

    /**
     * コンストラクタ
     *
     * @param list 変更するリスト
     * @param index 変更するリストのインデックス
     * @param oldValue 変更前の値
     * @param newValue 変更後の値
     */
    public SetListItemCommand(List<T> list, int index, T newValue) {
        this(list, index, list.get(index), newValue);
    }

    /**
     * コンストラクタ
     *
     * @param list 変更するリスト
     * @param index 変更するリストのインデックス
     * @param oldValue 変更前の値
     * @param newValue 変更後の値
     */
    public SetListItemCommand(List<T> list, int index, T oldValue, T newValue) {
        this.list = list;
        this.index = index;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        this.list.set(index, newValue);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        this.list.set(index, oldValue);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
