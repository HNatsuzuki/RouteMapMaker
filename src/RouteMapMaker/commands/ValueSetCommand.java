package RouteMapMaker.commands;

import javafx.beans.value.WritableValue;

/**
 * undo, redo が可能な値設定のコマンドです。
 */
public class ValueSetCommand<T> implements Command {
    private final WritableValue<T> writableObject;
    private final T oldValue;
    private final T newValue;

    /**
     * コンストラクタ
     *
     * @param writableObject 値変更するオブジェクト
     * @param newValue 変更後の値
     */
    public ValueSetCommand(WritableValue<T> writableObject, T newValue) {
        this(writableObject, writableObject.getValue(), newValue);
    }

    /**
     * コンストラクタ
     *
     * @param writableObject 値変更するオブジェクト
     * @param oldValue 変更前の値
     * @param newValue 変更後の値
     */
    public ValueSetCommand(WritableValue<T> writableObject, T oldValue, T newValue) {
        this.writableObject = writableObject;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        this.writableObject.setValue(newValue);
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        this.writableObject.setValue(oldValue);
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        this.execute();
    }
}
