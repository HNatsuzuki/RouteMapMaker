package RouteMapMaker.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javafx.scene.image.Image;

/**
 * ERM ファイル読み込み用クラスです。
 */
public class ErmFileReader implements AutoCloseable {
    private final File file;
    private final InputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 読み込むファイル
     * @throws FileNotFoundException 読み込むファイルが存在しない場合にスローされます。
     */
    public ErmFileReader(File file) throws FileNotFoundException {
        this.file = file;
        this.stream = new FileInputStream(file);
    }

    /**
     * ファイル読み込み処理を行います。
     *
     * @return 読み込んだデータ
     * @throws IOException 読み込み時にエラーが発生した場合にスローされます。
     */
    public SaveData read() throws IOException {
        Properties properties = new Properties();
        Map<Integer, Image> images = new HashMap<>();

        try (InputStreamReader isr = new InputStreamReader(this.stream, "UTF-8")) {
            properties.load(isr);
        }

        // 画像の読み込み．同ディレクトリの全pngを対象にする．
        for (File f: new File(file.getParent()).listFiles()) {
            System.out.println(f.getParent() + " -> " + f.getName());
            if (FileUtils.getExtension(f).equals(".png")) {
                System.out.println("read.");

                try {
                    int index = Integer.valueOf(FileUtils.getFileNameWithoutExtension(f));

                    try (ImageFileReader imageFileReader = new ImageFileReader(f)) {
                        Image image = imageFileReader.read();
                        images.put(index, image);
                    }
                } catch (NumberFormatException e) {
                    // ファイル名が数字でない場合は読み込み処理を行わない．
                    System.out.println("skipped reading: " + f.getName());
                }
            }
        }

        return new SaveData(properties, images);
    }

    /**
     * ファイルをクローズします。
     */
    public void close() throws IOException {
        this.stream.close();
    }
}
