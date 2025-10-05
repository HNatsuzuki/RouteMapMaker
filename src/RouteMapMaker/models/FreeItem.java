package RouteMapMaker.models;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class FreeItem implements Cloneable{//路線図上に自由挿入できるアイテムを保持するクラス。テキストと画像をサポート
	public static final int TEXT = 0;
	public static final int IMAGE = 1;
	
	private IntegerProperty type = new SimpleIntegerProperty();
	private Image image;
	private String text;//IMAGEなら画像の名前、TEXTなら文字列を格納する
	private DoubleProperty[] params;
	private DoubleBinding[] dragArea = new DoubleBinding[4];//ドラッグ有効エリアを定義する。
	private Color color = Color.BLACK;
	private String font;//フォントファミリ名
	//<dragArea>0:左上X,1:左上Y,2:右下X,3:右下Y
	/*
	 * IMAGE：image,{左上X,左上Y,描画幅,描画高さ,回転}
	 * TEXT：Color,text,{X,Y,文字サイズ,lineWidth,回転,文字style,strokeOrFill(0:Fill,1:Stroke),縦書き横書き(0:横)},FontName
	 */
	public FreeItem(int type){//一度タイプを設定したら変更できない仕様
		this.type.set(type);
		if(this.type.get() == TEXT){
			params = new DoubleProperty[8];
			for(int i = 0; i < 8; i++){
				params[i] = new SimpleDoubleProperty(0.0);
			}
		}else if(this.type.get() == IMAGE){
			params = new DoubleProperty[5];
			for(int i = 0; i < 5; i++){
				params[i] = new SimpleDoubleProperty(0.0);
			}
			//以下、dragAreaのBinding処理
			dragArea[0] = Bindings.add(0.0, params[0]);//そのまんま代入するていうのができないので0加算でごまかす
			dragArea[1] = Bindings.add(0.0, params[1]);
			dragArea[2] = (DoubleBinding) Bindings.add(params[0], params[2]);
			dragArea[3] = (DoubleBinding) Bindings.add(params[1], params[3]);
		}else{
			throw new IllegalArgumentException("typeがTEXTでもIMAGEでもありません！");
		}
	}
	public int getType(){
		return this.type.get();
	}
	public DoubleProperty[] getParams(){
		return this.params;
	}
	public void setImage(Image image){
		this.image = image;
	}
	public Image getImage(){
		return this.image;
	}
	public void setText(String text){
		this.text = text;
	}
	public String getText(){
		return this.text;
	}
	public void setColor(Color c){
		this.color = c;
	}
	public Color getColor(){
		return this.color;
	}
	public void setFontName(String n){
		this.font = n;
	}
	public String getFontName(){
		return this.font;
	}
	public DoubleBinding[] getDragArea(){
		return this.dragArea;
	}

	public double getX() {
		return this.params[0].doubleValue();
	}

	public void setX(double x) {
		this.params[0].set(x);
	}

	public double getY() {
		return this.params[1].doubleValue();
	}

	public void setY(double y) {
		this.params[1].set(y);
	}

	public double getWidth() {
		if (getType() != IMAGE) {
			throw new UnsupportedOperationException("画像以外では幅の取得はできません。");
		}

		return this.params[2].doubleValue();
	}

	public double getSize() {
		if (getType() != TEXT) {
			throw new UnsupportedOperationException("テキスト以外ではサイズの取得はできません。");
		}

		return this.params[2].doubleValue();
	}

	public double getHeight() {
		if (getType() != IMAGE) {
			throw new UnsupportedOperationException("画像以外では高さの取得はできません。");
		}

		return this.params[3].doubleValue();
	}

	public double getLineWidth() {
		if (getType() != TEXT) {
			throw new UnsupportedOperationException("テキスト以外では線幅の取得はできません。");
		}

		return this.params[3].doubleValue();
	}

	public double getRotation() {
		return this.params[4].doubleValue();
	}

	public FontStyle getFontStyle() {
		if (getType() != TEXT) {
			throw new UnsupportedOperationException("テキスト以外ではフォントスタイルの取得はできません。");
		}

		if (this.params[5].get() == 0) {
			return FontStyle.NORMAL;
		} else if (this.params[5].get() == 1) {
			return FontStyle.BOLD;
		} else if (this.params[5].get() == 2) {
			return FontStyle.ITALIC;
		} else if (this.params[5].get() == 3) {
			return FontStyle.BOLD_ITALIC;
		} else {
			throw new IllegalStateException("フォントスタイル設定が異常です: " + this.params[5].get());
		}
	}

	public PaintMode getPaintMode() {
		if (getType() != TEXT) {
			throw new UnsupportedOperationException("テキスト以外では描画方法の取得はできません。");
		}

		if (this.params[6].get() == 0) {
			return PaintMode.FILL;
		} else if (this.params[6].get() == 1) {
			return PaintMode.STROKE;
		} else {
			throw new IllegalStateException("描画モード設定が異常です: " + this.params[6].get());
		}
	}

	public boolean isVertical() {
		if (getType() != TEXT) {
			throw new UnsupportedOperationException("テキスト以外では縦書きかどうかの取得はできません。");
		}

		if (this.params[7].getValue() == 0) {
			return false;
		}
		if (this.params[7].getValue() == 1) {
			return true;
		} else {
			throw new IllegalStateException("縦書き設定が異常です: " + this.params[7].get());
		}
	}

	/**
	 * 平行移動を行います。
	 *
	 * @param x x方向の移動量
	 * @param y y方向の移動量
	 */
	public void translate(double x, double y) {
		this.params[0].set(this.params[0].get() + x);
		this.params[1].set(this.params[1].get() + y);
	}

	@Override
	public FreeItem clone(){
		FreeItem f = null;
		try {
			f = (FreeItem) super.clone();
			f.type = new SimpleIntegerProperty(this.getType());
			//imageはそのまま参照させる。
			f.params = new DoubleProperty[this.getParams().length];
			for(int i = 0; i < this.getParams().length; i++){
				f.params[i] = new SimpleDoubleProperty(this.getParams()[i].get());
			}
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
}
