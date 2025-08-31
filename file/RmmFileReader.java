package RouteMapMaker.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.scene.image.Image;

/**
 * RMM ファイル読み込み用クラスです。
 */
public class RmmFileReader implements AutoCloseable {
    private final ZipInputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 読み込むファイル
     * @throws FileNotFoundException 読み込むファイルが存在しない場合にスローされます。
     */
    public RmmFileReader(File file) throws FileNotFoundException {
        this.stream = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)), Charset.forName("UTF-8"));
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

        ZipEntry entry = null;
        File mainFile = null;

        while ((entry = stream.getNextEntry()) != null ) {
            if (entry.getName().equals("main.properties")) {
                //mainの読み込み
                mainFile = new File(entry.getName());
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(mainFile));
                byte[] buf = new byte[1024];
                int size = 0;
                while ((size = stream.read(buf)) > 0) {
                    os.write(buf, 0, size);
                }
                os.close();
                InputStreamReader isr = new InputStreamReader(new FileInputStream(mainFile), "UTF-8");
                properties.load(isr);
                isr.close();
            } else if (FileUtils.getExtension(entry.getName()).equals(".png")) {//画像
                File imageFile = new File(entry.getName());
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(imageFile));
                byte[] buf = new byte[1024];
                int size = 0;
                while((size = stream.read(buf)) > 0 ){
                    os.write(buf, 0, size);
                }
                os.close();
                int index = Integer.valueOf(FileUtils.getFileNameWithoutExtension(imageFile));

                try (ImageFileReader imageFileReader = new ImageFileReader(imageFile)) {
                    Image image = imageFileReader.read();
                    images.put(index, image);
                }
                imageFile.delete();
            }

            stream.closeEntry();;
        }
        //main.propertiesは全ての画像読み込みが終了してから行う。
        mainFile.delete();

        return new SaveData(properties, images);
    }

    /**
     * ファイルをクローズします。
     */
    public void close() throws IOException {
        this.stream.close();
    }
}
