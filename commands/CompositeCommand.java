package RouteMapMaker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * undo, redo が可能な、複数のコマンドを表すクラスです。
 */
public class CompositeCommand implements Command {
    private final List<Command> commands = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public CompositeCommand() {

    }

    /**
     * コンストラクタ
     *
     * @param commands 実行するコマンド群
     */
    public CompositeCommand(List<Command> commands) {
        this.commands.addAll(commands);
    }

    /**
     * コマンドを追加します。
     *
     * @param command 追加するコマンド
     */
    public void addCommand(Command command) {
        this.commands.add(command);
    }

    /**
     * コマンドを追加します。
     *
     * @param commands 追加するコマンド
     */
    public void addAll(List<Command> commands) {
        this.commands.addAll(commands);
    }

    /**
     * コマンドを実行します。
     */
    @Override
    public void execute() {
        for (Command command : this.commands) {
            command.execute();
        }
    }

    /**
     * 実行した処理をもとに戻します。
     */
    @Override
    public void undo() {
        List<Command> reverse = new ArrayList<>(commands);
        Collections.reverse(reverse);

        for (Command command : reverse) {
            command.undo();
        }
    }

    /**
     * もとに戻した処理をやり直します。
     */
    @Override
    public void redo() {
        for (Command command : this.commands) {
            command.redo();
        }
    }
}
