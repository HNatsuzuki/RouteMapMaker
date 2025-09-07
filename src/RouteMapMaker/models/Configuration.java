package RouteMapMaker.models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * ソフトウェアの環境設定を保持するクラスです。
 */
public class Configuration {
	//R_は路線編集モードで有効な項目。S_は運転系統編集モードで有効な項目
	private final BooleanProperty R_grid = new SimpleBooleanProperty(true);//グリッドを表示するか
	private final IntegerProperty R_gridInterval = new SimpleIntegerProperty(20);
	private final BooleanProperty R_bindToGridX = new SimpleBooleanProperty(true);
	private final BooleanProperty R_bindToGridY = new SimpleBooleanProperty(true);
	private final ObjectProperty<Color> fixedColor = new SimpleObjectProperty<>(Color.CORNFLOWERBLUE);
	private final ObjectProperty<Color> nonFixedColor = new SimpleObjectProperty<>(Color.BLACK);
	private final BooleanProperty triangleGrid = new SimpleBooleanProperty(false);
	private final BooleanProperty menubarMode = new SimpleBooleanProperty(true);
	private boolean no_alert = false;//起動時使用上の注意を表示しない

	private final StringProperty uiFont = new SimpleStringProperty("System");
	
	/**
	 * グリッドを表示するかどうかを表す値を取得します。
	 *
	 * @return グリッドを表示する場合は true
	 */
	public boolean getR_grid() { return this.R_grid.get(); }

	/**
	 * グリッドを表示するかどうかを表す値を取得します。
	 *
	 * @param b グリッドを表示する場合 true
	 */
	public void setR_grid(boolean b) { this.R_grid.set(b); }

	/**
	 * グリッド間隔を取得します。
	 * 
	 * @return グリッド間隔
	 */
	public int getR_gridInterval() { return this.R_gridInterval.get(); }

	/**
	 * グリッド間隔を設定します。
	 *
	 * @param b グリッド間隔
	 */
	public void setR_gridInterval(int b) { this.R_gridInterval.set(b); }

	/**
	 * X軸座標をグリッドに合わせるかどうかを表す値を取得します。
	 *
	 * @return グリッドに合わせる場合 true
	 */
	public boolean getR_bindToGridX() { return this.R_bindToGridX.get(); }

	/**
	 * X軸座標をグリッドに合わせるかを表す値を設定します。
	 *
	 * @param b グリッドに合わせる場合 true
	 */
	public void setR_bindToGridX(boolean b) { this.R_bindToGridX.set(b); }

	/**
	 * Y軸座標をグリッドに合わせるかどうかを表す値を取得します。
	 *
	 * @return グリッドに合わせる場合 true
	 */
	public boolean getR_bindToGridY() { return this.R_bindToGridY.get(); }

	/**
	 * Y軸座標をグリッドに合わせるかを表す値を設定します。
	 *
	 * @param b グリッドに合わせる場合 true
	 */
	public void setR_bindToGridY(boolean b) { this.R_bindToGridY.set(b); }

	/**
	 * 座標固定駅の点の表示色を取得します。
	 * 
	 * @return 座標固定駅の点の表示色
	 */
	public Color getFixedColor() { return this.fixedColor.get(); }

	/**
	 * 座標固定駅の点の表示色を設定します。
	 * 
	 * @param c 座標固定駅の点の表示色
	 */
	public void setFixedColor(Color c) { this.fixedColor.set(c); }

	/**
	 * 座標非固定駅の点の表示色を取得します。
	 * 
	 * @return 座標非固定駅の点の表示色
	 */
	public Color getNonFixedColor() { return this.nonFixedColor.get(); }

	/**
	 * 座標非固定駅の表示色を設定します。
	 * 
	 * @param c 座標非固定駅の点の表示色
	 */
	public void setNonFixedColor(Color c) { this.nonFixedColor.set(c); }

	/**
	 * OSのメニューバーを使用するかどうかを表す値を取得します。
	 *
	 * @return OSのメニューバーを使用する場合 truw
	 */
	public boolean getMenubarMode() { return this.menubarMode.get(); }

	/**
	 * OSのメニューバーを使用するかどうかを表す値を設定します。
	 *
	 * @param b OSのメニューバーを使用する場合 true
	 */
	public void setMenubarMode(boolean b) { this.menubarMode.set(b); }

	/**
	 * 起動時に使用上の注意を表示するかどうかを表す値を取得します。
	 *
	 * @return 起動時に使用上の注意を表示する場合 true
	 */
	public boolean getNoAlert() { return this.no_alert; }

	/**
	 * 起動時に使用上の注意を表示するかどうかを表す値を設定します。
	 * 
	 * @param b 起動時に使用上の注意を表示する場合 true
	 */
	public void setNoAlert(boolean b) { this.no_alert = b; }

	/**
	 * グリッドを三角形にするかどうかを表す値を取得します。
	 *
	 * @return グリッドを三角形にする場合 true
	 */
	public boolean isGridTriangle() { return this.triangleGrid.get(); }

	/**
	 * グリッドを三角形にするかどうかを表す値を設定します。
	 *
	 * @param b グリッドを三角形にする場合 true
	 */
	public void setGridTriangle(boolean b) { this.triangleGrid.set(b); }

	/**
	 * UIフォントを取得します。
	 * 
	 * @return UIフォント
	 */
	public String getUiFont() { return this.uiFont.get(); }

	/**
	 * UIフォントを設定します。
	 *
	 * @param s UIフォント
	 */
	public void setUiFont(String s) { this.uiFont.set(s); }

	public BooleanProperty getR_gridProperty() { return this.R_grid; }
	public IntegerProperty getR_gridIntervalProperty() { return this.R_gridInterval; }
	public BooleanProperty getR_bindToGridXProperty() { return this.R_bindToGridX; }
	public BooleanProperty getR_bindToGridYProperty() { return this.R_bindToGridY; }
	public ObjectProperty<Color> getFixedColorProperty() { return this.fixedColor; }
	public ObjectProperty<Color> getNonFixedColorProperty() { return this.nonFixedColor; }
	public BooleanProperty getTriangleGridProperty() { return this.triangleGrid; }
	public BooleanProperty getMenubarModeProperty() { return this.menubarMode; }
	public StringProperty getUiFontProperty() { return this.uiFont; }
	
	public void read(){
		File file = new File("config.properties");
		if(file.exists()){
			Properties p = new Properties();
			try {
				FileReader fr = new FileReader(file);
				p.load(fr);
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//以下各項目の設定
			R_grid.set(Boolean.valueOf(p.getProperty("R_grid", "true")));
			R_gridInterval.set(Integer.valueOf(p.getProperty("R_gridInterval", "20")));
			R_bindToGridX.set(Boolean.valueOf(p.getProperty("R_bindToGridX", "true")));
			R_bindToGridY.set(Boolean.valueOf(p.getProperty("R_bindToGridY", "true")));
			fixedColor.set(Color.valueOf(p.getProperty("fixedColor",Color.CORNFLOWERBLUE.toString())));
			nonFixedColor.set(Color.valueOf(p.getProperty("nonFixedColor",Color.BLACK.toString())));
			menubarMode.set(Boolean.valueOf(p.getProperty("menubarMode", "true")));
			no_alert = Boolean.valueOf(p.getProperty("no_alert", "false"));
			triangleGrid.set(Boolean.valueOf(p.getProperty("triangleGrid", "false")));
			uiFont.set(String.valueOf(p.getProperty("uiFont", "System")));
		}
	}
	public void save(){
		try {
			Properties p = new Properties();
			p.setProperty("R_grid", String.valueOf(R_grid.get()));
			p.setProperty("R_gridInterval", String.valueOf(R_gridInterval.get()));
			p.setProperty("R_bindToGridX", String.valueOf(R_bindToGridX.get()));
			p.setProperty("R_bindToGridY", String.valueOf(R_bindToGridY.get()));
			p.setProperty("menubarMode", String.valueOf(menubarMode.get()));
			p.setProperty("fixedColor", String.valueOf(fixedColor.get()));
			p.setProperty("nonFixedColor", String.valueOf(nonFixedColor.get()));
			p.setProperty("no_alert", String.valueOf(no_alert));
			p.setProperty("triangleGrid", String.valueOf(triangleGrid));
			p.setProperty("uiFont", uiFont.get());
			FileWriter fw = new FileWriter("config.properties");
			p.store(fw, null);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
