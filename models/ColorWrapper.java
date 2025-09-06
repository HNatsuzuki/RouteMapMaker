package RouteMapMaker.models;

import javafx.beans.value.WritableValue;
import javafx.scene.paint.Color;

public class ColorWrapper implements WritableValue<Color> {//javafxのColorをラップするだけ。こうすることでredo/undoに対応する。
	private Color color = Color.BLACK;
	public ColorWrapper(){
		
	}
	public ColorWrapper(Color c){
		this.color = c;
	}
	public Color get(){
		return this.color;
	}
	public void set(Color c){
		this.color = c;
	}

	public Color getValue() {
		return this.get();
	}

	public void setValue (Color color) {
		this.set(color);
	}
}
