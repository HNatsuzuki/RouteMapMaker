package RouteMapMaker.models;

import RouteMapMaker.URElements;
import RouteMapMaker.commands.AddListItemCommand;
import RouteMapMaker.commands.Command;
import RouteMapMaker.commands.RemoveListItemCommand;
import RouteMapMaker.commands.SetListItemCommand;
import javafx.beans.property.SimpleListProperty;

public class NotifiableList<E> extends SimpleListProperty<E> {//これを使うとURElementsへの通知を自動でやります。
	/*
	 * Undoが反映されるメソッド：add(E e),add(int index, E element),set(int index, E element),remove(int index),remove(Object o)
	 */

	URElements ure;
	
	public NotifiableList(URElements ure){
		super();
		this.ure = ure;
	}
	public NotifiableList(){
		super();
	}
	public void setUR(URElements ure){
		this.ure = ure;
	}
	@Override
	public boolean add(E e){
		ure.push(new AddListItemCommand<>(this, e));
		super.add(e);
		return true;
	}
	@Override
	public void add(int index, E e){
		ure.push(new AddListItemCommand<>(this, index, e));
		super.add(index, e);
	}
	@Override
	public E set(int index, E e){
		E prevItem = this.get(index);
		Command command = new SetListItemCommand<>(this, index, prevItem, e);
		ure.push(command);

		return super.set(index, e);
	}
	@Override
	public E remove(int index){
		E removeItem = super.remove(index);
		ure.push(new RemoveListItemCommand<>(this, index, removeItem));
		return removeItem;
	}
	@Override
	public boolean remove(Object o){
		int index = super.indexOf(o);
		if(index == -1){
			return false;//見つからない場合は通知しない
		}else{
			ure.push(new RemoveListItemCommand<>(this, index));
			return super.remove(o);
		}
	}
}
