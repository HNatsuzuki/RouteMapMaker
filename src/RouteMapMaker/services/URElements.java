package RouteMapMaker.services;

import java.util.ArrayDeque;
import java.util.Deque;

import RouteMapMaker.commands.Command;
import RouteMapMaker.commands.ValueSetCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;

public class URElements {//undo_redoのための情報を保持する。
	protected BooleanProperty canUndo = new SimpleBooleanProperty(false);//undoできるか否か
	protected BooleanProperty canRedo = new SimpleBooleanProperty(false);//redoできるか否か
	protected int prevUndoStackSize = 0; //前回のセーブ時のUndoTypeStackのサイズ．終了の警告に使う

	protected Deque<Command> undoStack = new ArrayDeque<>();
	protected Deque<Command> redoStack = new ArrayDeque<>();
	
	public void addObserve(IntegerProperty... iops){//監視対象を追加する。
		for(IntegerProperty iop: iops){
			iop.addListener((ov,oldVal,newVal) ->{//値が変更されたら自動的にpushするようにする。
				push(new ValueSetCommand<>(iop, oldVal.intValue(), newVal.intValue()));
			});
		}
	}
	public void addObserve(DoubleProperty... dops){
		for(DoubleProperty dop: dops){
			dop.addListener((ov,oldVal,newVal) ->{
				push(new ValueSetCommand<>(dop, oldVal.doubleValue(), newVal.doubleValue()));
			});
		}
	}
	public void addObserve(StringProperty... sops){
		for(StringProperty sop: sops){
			sop.addListener((ov,oldVal,newVal) ->{
				push(new ValueSetCommand<>(sop, oldVal, newVal));
			});
		}
	}

	public void push(Command command) {
		undoStack.push(command);
		redoStack.clear();
		canUndo.set(true);
		canRedo.set(false);
	}
	
	public void undo(){
		Command command = undoStack.pop();

		if (command == null) {
			throw new IllegalStateException("cannot execute undo.");
		}

		command.undo();

		if (undoStack.size() == 0) {
			canUndo.set(false);
		}

		redoStack.push(command);
		canRedo.set(true);
	}
	public void redo(){
		Command command = redoStack.pop();

		if (command == null) {
			throw new IllegalStateException("cannot execute redo.");
		}

		command.redo();

		if (redoStack.size() == 0) {
			canRedo.set(false);
		}

		undoStack.push(command);
		canUndo.set(true);
	}
	public BooleanProperty getUndoableProperty(){
		return this.canUndo;
	}
	public BooleanProperty getRedoableProperty(){
		return this.canRedo;
	}
	public void clear(){//蓄積されたundo/redoをクリアする。
		undoStack.clear();
		redoStack.clear();
		canUndo.set(false);
		canRedo.set(false);
		prevUndoStackSize = 0;
	}
	public void saveUndoStackSize() {
		prevUndoStackSize = undoStack.size();
	}
	public boolean isSaveNeeded() {
		return undoStack.size() != prevUndoStackSize;
	}
	public int getStackCount() {
		return undoStack.size();
	}
}
