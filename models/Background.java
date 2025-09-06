package RouteMapMaker;

import java.util.HashMap;
import java.util.Properties;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Background implements Cloneable{
	// Objectを複製してUndo/Redoを管理
	Color color;
	Image image;
	int x, y; // 原点座標
	int zoomRatio, opacity; // %単位

	public Color getColor() { return this.color; }
	public Image getImage() { return this.image; }
	public void setImage(Image image) { this.image = image; }
	public int getX() { return this.x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return this.y; }
	public void setY(int y) { this.y = y; }
	public int getZoomRatio() { return this.zoomRatio; }
	public void setZoomRatio(int zoomRatio) { this.zoomRatio = zoomRatio; }
	public int getOpacity() { return this.opacity; }
	public void setOpacity(int opacity) { this.opacity = opacity; }
	
	public Background() {
		color = Color.WHITESMOKE;
		image = null;
		x = y = opacity = 0;
		zoomRatio = 100;
	}
	
	@Override
	public Background clone() {
		Background c = new Background();
		c.copyParams(this);
		return c;
	}
	
	public void copyParams(Background ref) {
		this.color = ref.color;
		this.image = ref.image;
		this.x = ref.x;
		this.y = ref.y;
		this.zoomRatio = ref.zoomRatio;
		this.opacity = ref.opacity;
	}
	
	public void setColor(Color c) {
		image = null;
		color = c;
	}
	
	public void read(Properties p, HashMap<Integer,Image> imageMap) {
		
	}
	
	public void save(Properties p, HashMap<Integer,Image> imageMap) {
		
	}
}
