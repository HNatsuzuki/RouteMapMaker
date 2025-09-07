package RouteMapMaker.commands;

/**
 * undo、redo が可能なコマンドであることを表すインターフェイスです。
 */
public interface Command {
    /**
     * コマンドを実行します。
     */
    void execute();

    /**
     * 実行した処理をもとに戻します。
     */
    void undo();

    /**
     * もとに戻した処理をやり直します。
     */
    void redo();
}
