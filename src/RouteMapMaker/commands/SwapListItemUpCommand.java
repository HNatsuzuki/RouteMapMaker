package RouteMapMaker.commands;

import java.util.List;

/**
 * 上の要素と入れ替えるコマンドです。
 */
public class SwapListItemUpCommand<T> implements Command {
    private final List<T> list;
    private final int index;

    /**
     * コンストラクタ
     *
     * @param list リスト
     * @param index 入れ替えたい要素のインデックス
     */
    public SwapListItemUpCommand(List<T> list, int index) {
        this.list = list;
        this.index = index;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        T currentItem = this.list.get(index);
        T previousItem = this.list.get(index - 1);
        this.list.set(index, previousItem);
        this.list.set(index - 1, currentItem);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        // 一つ前の要素と入れ替えるだけのため、結果的に undo と redo の処理は同じになる
        this.execute();
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
