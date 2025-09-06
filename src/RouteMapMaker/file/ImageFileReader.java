package RouteMapMaker.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * 画像ファイル読み込み用のクラスです。
 */
public class ImageFileReader implements AutoCloseable {
    private final InputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 読み込むファイル
     * @throws FileNotFoundException ファイルが存在しない
     */
    public ImageFileReader(File file) throws FileNotFoundException {
        this.stream = new FileInputStream(file);
    }

    /**
     * コンストラクタ
     *
     * @param stream 読み込むストリーム
     */
    public ImageFileReader(InputStream stream) {
        this.stream = stream;
    }

    /**
     * 画像を読み込みます。
     *
     * @return 読み込んだ画像
     * @throws IOException 画像読込中にエラーが発生した
     */
    public Image read() throws IOException {
        return SwingFXUtils.toFXImage(ImageIO.read(this.stream), null);
    }

    /**
     * クローズします。
     * 
     * @throws IOException クローズ中にエラーが発生した
     */
    public void close() throws IOException {
        this.stream.close();
    }
}
