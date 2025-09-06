package RouteMapMaker.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * 画像ファイル書き込み用のクラスです。
 */
public class ImageFileWriter implements AutoCloseable {
    private final OutputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 書き込むファイル
     * @throws FileNotFoundException ファイルが存在しない
     */
    public ImageFileWriter(File file) throws FileNotFoundException {
        this.stream = new FileOutputStream(file);
    }

    /**
     * コンストラクタ
     *
     * @param stream 書き込むストリーム
     */
    public ImageFileWriter(OutputStream stream) {
        this.stream = stream;
    }

    /**
     * 画像を書き込みます。
     *
     * @param image 書き込む画像
     * @throws IOException 画像読込中にエラーが発生した
     */
    public void write(Image image) throws IOException {
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", this.stream);
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
