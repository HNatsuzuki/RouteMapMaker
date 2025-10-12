package RouteMapMaker.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class MarkLayer implements Cloneable{//マーク編集における各レイヤーを保持するクラス。
	public static final int FILL = -1;//FILL属性
	public static final int STROKE = -2;//STROKE属性
	public static final int OVAL = 0;//楕円。円を含む。
	public static final int ARC = 1;//円弧。通常の円は楕円を使う。
	public static final int RECT = 2;//角の丸い長方形。ただの長方形含む。shapeとcanvasでパラメーターが全然違うので注意！
	public static final int POLYGON = 3;//多角形
	public static final int LINE = 4;//直線
	public static final int TEXT = 5;//文字列
	public static final int IMAGE = 6;//外部画像
	/*
	 * マークは基本図形の重ねあわせで実装する。レイヤーごとに一つのオブジェクトが用意される。
	 * shapeを使うかcanvasを使うかで実装を分ける必要がある。
	 * サポートする基本図形のパラメーターは以下のとおり
	 * OVAL：Color,paint,{左上X,左上Y,直径X,直径Y,lineWidth}
	 * ARC：Color,paint,{X,Y,幅,高さ,始角,角の大きさ,lineWidth,閉じタイプ（0:CHORD,1:OPEN,2:ROUND）
	 * RECT：Color,paint,{左上X,左上Y,幅,高さ,円弧幅,円弧高さ,lineWidth}
	 * POLYGON
	 * LINE：Color,paint,{始点X,始点Y,終点X,終点Y,lineWidth}
	 * TEXT：Color,paint,text,{X,Y,文字サイズ,lineWidth,文字タイプ}
	 * IMAGE：image,text,{左上X,左上Y,描画幅,描画高さ}
	 */
	private int type;
	//fillかstrokeか。デフォルトはfill。
	private final ObjectProperty<PaintMode> paintModeProperty = new SimpleObjectProperty<>(PaintMode.FILL);
	private ObservableList<DoubleProperty> params = FXCollections.observableArrayList();//パラメーターを保持
	private final boolean[] paramsProportion;//格納されているパラメーターはマークの大きさに依存するか。
	private StringProperty text = new SimpleStringProperty();//文字列だった場合にはtextを保持。IMAGEの場合は画像名
	private StringProperty fontName = new SimpleStringProperty();//文字列だった場合にfontNameを保持
	private ObjectProperty<Image> imageWrapper = new SimpleObjectProperty<>();//IMAGEだった場合には内容を保持
	private ObjectProperty<Color> color = new SimpleObjectProperty<>();//図形の色を保持
	
	//typeは始めに設定し、設定したらもう変更できない仕様にする。
	public MarkLayer(int type){
		this.type = type;
		switch(this.type){//パラメーターがマークサイズに比例するか否か
		case OVAL:
			boolean [] b = {true,true,true,true,true};
			paramsProportion = b;
			break;
		case ARC:
			boolean [] b1 = {true,true,true,true,false,false,true,false};
			paramsProportion = b1;
			break;
		case RECT:
			boolean[] b2 = {true,true,true,true,true,true,true};
			paramsProportion = b2;
			break;
		case LINE:
			boolean[] b5 = {true,true,true,true,true,false};
			paramsProportion = b5;
			break;
		case TEXT:
			boolean[] b3 = {true,true,true,true,false};
			paramsProportion = b3;
			break;
		case IMAGE:
			boolean[] b4 = {true,true,true,true};
			paramsProportion = b4;
			break;
		default:
			paramsProportion = null;
		}
	}

	/**
	 * 楕円レイヤーを作成します。
	 *
	 * @return 楕円レイヤー
	 */
	public static MarkLayer createOvalLayer() {
		MarkLayer layer = new MarkLayer(MarkLayer.OVAL);
		//初期値投入
		layer.addParam(0.0);//左上X
		layer.addParam(0.0);//左上Y
		layer.addParam(1.0);//横直径
		layer.addParam(1.0);//縦直径
		layer.addParam(0.05);//デフォの線の太さ2/40（あくまでも相対比なのでprevSizeが変わってもここは問題ない）
		layer.setPaintMode(PaintMode.FILL);
		layer.setColor(Color.WHITE);

		return layer;
	}

	/**
	 * 矩形レイヤーを作成します。
	 *
	 * @return 矩形レイヤー
	 */
	public static MarkLayer createRectangleLayer() {
		MarkLayer layer = new MarkLayer(MarkLayer.RECT);
		//初期値投入
		layer.addParam(0.0);//左上X
		layer.addParam(0.0);//左上Y
		layer.addParam(1.0);//幅
		layer.addParam(1.0);//高さ
		layer.addParam(0.0);//円弧幅
		layer.addParam(0.0);//円弧高さ
		layer.addParam(0.05);//lineWidth
		layer.setPaintMode(PaintMode.FILL);
		layer.setColor(Color.WHITE);

		return layer;
	}

	/**
	 * 直線レイヤーを作成します。
	 *
	 * @return 直線レイヤー
	 */
	public static MarkLayer createLineLayer() {
		MarkLayer layer = new MarkLayer(MarkLayer.LINE);
		layer.addParam(0.0);//始点X
		layer.addParam(0.0);//始点Y
		layer.addParam(1.0);//終点X
		layer.addParam(1.0);//終点Y
		layer.addParam(0.05);//lineWidth
		layer.addParam(0);//端はSQUARE
		layer.setColor(Color.WHITE);

		return layer;
	}

	/**
	 * 円弧レイヤーを作成します。
	 *
	 * @return 円弧レイヤー
	 */
	public static MarkLayer createArcLayer() {
		MarkLayer layer = new MarkLayer(MarkLayer.ARC);
		//初期値投入
		layer.addParam(0.0);//X
		layer.addParam(0.0);//Y
		layer.addParam(1.0);//幅
		layer.addParam(1.0);//高さ
		layer.addParam(0.0);//始角
		layer.addParam(120.0);//角の大きさ
		layer.addParam(0.05);//lineWidth
		layer.addParam(2.0);//closure
		layer.setPaintMode(PaintMode.FILL);
		layer.setColor(Color.WHITE);

		return layer;
	}

	/**
	 * 文字列レイヤーを作成します。
	 *
	 * @return 文字列レイヤー
	 */
	public static MarkLayer createTextLayer() {
		MarkLayer layer = new MarkLayer(MarkLayer.TEXT);
		//初期値投入
		layer.addParam(0.0);//X
		layer.addParam(1.0);//Y
		layer.addParam(1.0);//size
		layer.addParam(0.05);//lineWidth
		layer.addParam(0.0);//type
		layer.setPaintMode(PaintMode.FILL);
		layer.setColor(Color.WHITE);
		layer.setText("※");
		layer.setFontName("System");

		return layer;
	}

	/**
	 * 画像レイヤーを作成します。
	 *
	 * @param image 画像
	 * @param name 画像の名称
	 * @return 画像レイヤー
	 */
	public static MarkLayer createImageLayer(Image image, String name) {
		MarkLayer layer = new MarkLayer(MarkLayer.IMAGE);
		layer.setImage(image);
		layer.setText(name);
		//初期値設定
		double h = layer.getImage().getHeight();
		double w = layer.getImage().getWidth();
		if (w < h) {//縦長
			layer.addParam((1 - w / h) / 2);//X
			layer.addParam(0.0);//Y
			layer.addParam(w / h);//幅
			layer.addParam(1.0);//高さ
		} else {//横長
			layer.addParam(0.0);//X
			layer.addParam((1 - h / w) / 2);//Y
			layer.addParam(1.0);//幅
			layer.addParam(h / w);//高さ
		}

		return layer;
	}

	public int getType(){
		return this.type;
	}
	public boolean[] getParamsProportion(){
		return this.paramsProportion;
	}

	/** レイヤーの図形描画方法を取得します。これは従来との互換性のために残されています。 */
	public int getPaint() {
		switch (paintModeProperty.get()) {
			case FILL:
				return FILL;
			case STROKE:
				return STROKE;
			default:
				throw new UnsupportedOperationException("内部状態が不正です。");
		}
	}

	/** レイヤーの図形描画方法を設定します。これは従来との互換性のために残されています。 */
	public void setPaint(int paint) {
		switch (paint) {
			case FILL:
				paintModeProperty.set(PaintMode.FILL);
				break;
			case STROKE:
				paintModeProperty.set(PaintMode.STROKE);
				break;
			default:
				throw new IllegalArgumentException("不正なパラメータです。");
		}
	}

	/** レイヤーの図形描画方法を取得します。 */
	public PaintMode getPaintMode() {
		return paintModeProperty.get();
	}

	/** レイヤーの図形描画方法を設定します。 */
	public void setPaintMode(PaintMode paintMode) {
		this.paintModeProperty.set(paintMode);
	}

	public ObjectProperty<PaintMode> paintModeProperty() {
		return paintModeProperty;
	}

	/** 図形描画方法を保持するかどうか */
	public boolean hasPaintMode() {
		switch (getType()) {
			case OVAL:
			case RECT:
			case ARC:
			case TEXT:
				return true;
			case LINE:
			case IMAGE:
				return false;		
			default:
				throw new UnsupportedOperationException(getType() + " に対して未実装です。");
		}
	}

	public ObservableList<DoubleProperty> getParamProperty(){
		return this.params;
	}
	public double getParam(int index){
		return this.params.get(index).get();
	}
	public void addParam(double d){
		this.params.add(new SimpleDoubleProperty(d));
	}
	public void setParam(int index, double d){
		this.params.get(index).set(d);
	}
	public String getText(){
		return this.text.get();
	}
	public StringProperty getTextProperty(){
		return this.text;
	}
	public void setText(String text){
		this.text.set(text);
	}

	/** テキストを保持するかどうか */
	public boolean hasText() {
		switch (getType()) {
			case TEXT:
				return true;
			case OVAL:
			case ARC:
			case RECT:
			case LINE:
			case IMAGE:
				return false;		
			default:
				throw new UnsupportedOperationException(getType() + " に対して未実装です。");
		}
	}

	public String getFontName(){
		return this.fontName.get();
	}
	public StringProperty getFontNameProperty(){
		return this.fontName;
	}
	public void setFontName(String name){
		this.fontName.set(name);
	}
	public Color getColor(){
		return this.color.get();
	}
	public ObjectProperty<Color> getColorProperty(){
		return this.color;
	}
	public void setColor(Color c){
		this.color.set(c);
	}
	public Image getImage(){
		return this.imageWrapper.get();
	}
	public ObjectProperty<Image> getImageProperty(){
		return this.imageWrapper;
	}
	public void setImage(Image image){
		this.imageWrapper.set(image);
	}
	@Override
	public MarkLayer clone(){
		MarkLayer t = null;
		try {
			t = (MarkLayer)super.clone();
			//参照型の変数は全てクローンしてください。
			t.params = FXCollections.observableArrayList();
			for(int i = 0;i < this.params.size(); i++) t.params.add(new SimpleDoubleProperty(this.params.get(i).get()));
			t.paintModeProperty.set(paintModeProperty.get());
			t.text = new SimpleStringProperty(this.text.get());
			t.fontName = new SimpleStringProperty(this.fontName.get());
			t.color = new SimpleObjectProperty<>(this.color.get());
			t.imageWrapper = new SimpleObjectProperty<>(this.imageWrapper.get());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

}
