package RouteMapMaker.Factories;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import RouteMapMaker.Configuration;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * FileChooser のファクトリクラスです。
 */
public class FileChooserFactory {
    private final Configuration config;
    private final ExtensionFilter ermFilter = new ExtensionFilter("路線図メーカーテキスト形式（*.erm）", "*.erm");
    private final ExtensionFilter rmmFilter = new ExtensionFilter("路線図メーカー形式（*.rmm）", "*.rmm");
    private final ExtensionFilter pngFilter = new ExtensionFilter("PNG形式（*.png）", "*.png");
    private final ExtensionFilter jpgFilter = new ExtensionFilter("JPEG形式（*.jpg）", "*.jpg");
    private final ExtensionFilter bmpFilter = new ExtensionFilter("Bitmap形式（*.bmp）", "*.bmp");
    private final ExtensionFilter pdfFilter = new ExtensionFilter("PDF形式（*.pdf）", "*.pdf");
    private final ExtensionFilter imgFilter = new ExtensionFilter(
        "Image Files(jpg,png,gif,bmp)", 
        "*.png", "*.jpg", "*.jpeg", "*.gif","*.bmp", "*.PNG", "*.JPG", "*.JPEG", "*.GIF","*.BMP");
    private final ExtensionFilter txtFilter = new ExtensionFilter("テキストファイル（*.txt）", "*.txt");
    private final List<ExtensionFilter> saveFileFilters = Arrays.asList(rmmFilter, ermFilter);
    private final List<ExtensionFilter> exportImageFilters = Arrays.asList(pngFilter, jpgFilter, bmpFilter, pdfFilter);
    
    public ExtensionFilter getErmFilter() {
        return this.ermFilter;
    }

    public ExtensionFilter getRmmFilter() {
        return this.rmmFilter;
    }

    public ExtensionFilter getTxtFilter() {
        return this.txtFilter;
    }

    public ExtensionFilter[] getExportImageFilters() {
        return exportImageFilters.toArray(new ExtensionFilter[0]);
    }

    public FileChooserFactory(Configuration config) {
        this.config = config;
    }

    /**
     * 画像出力用の FileChooser を作成します。
     *
     * @return 画像出力用の FileChooser
     */
    public FileChooser createExportImageFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(exportImageFilters.get(0));
        // chooser.getExtensionFilters().add(exportImageFilters.get(1));
        // chooser.getExtensionFilters().add(exportImageFilters.get(2));
        // chooser.getExtensionFilters().add(exportImageFilters.get(3));
        File dir = new File(config.getImageFileDir());

        if (dir.exists()) {
            chooser.setInitialDirectory(dir);
        }

        return chooser;
    }

    /**
     * 画像入力用の FileChooser を作成します。
     *
     * @return 画像入力用の FileChooser
     */
    public FileChooser createImportImageFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(imgFilter);
        chooser.setTitle("画像ファイルを選択してください。");
        File dir = new File(config.getImageFileDir());

        if (dir.exists()) {
            chooser.setInitialDirectory(dir);
        }

        return chooser;
    }

    /**
     * 保存データ形式 (rmm, erm) 用の FileChooser を作成します。
     * 
     * @return 保存データ形式 (rmm, erm) 用の FileChooser
     */
    public FileChooser createSaveFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(saveFileFilters);
        File dir = new File(config.getSaveFileDir());
        
        if (dir.exists()) {
            chooser.setInitialDirectory(dir);
        }

        return chooser;
    }

    /**
     * テキストファイル用の FileChooser を作成します。
     *
     * @return テキストファイル用の FileChooser
     */
    public FileChooser createTextFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(txtFilter);
        File dir = new File(config.getTextFileDir());

        if (dir.exists()) {
            chooser.setInitialDirectory(dir);
        }

        return chooser;
    }
}
