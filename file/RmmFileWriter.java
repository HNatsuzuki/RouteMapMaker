package RouteMapMaker.file;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javafx.scene.image.Image;

/**
 * RMM ファイル書き出し用クラスです。
 */
public class RmmFileWriter implements AutoCloseable {
    private final ZipOutputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 書き出すファイル
     * @throws FileNotFoundException 読み込むファイルが存在しない場合にスローされます。
     */
    public RmmFileWriter(File file) throws FileNotFoundException {
        this.stream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)), Charset.forName("UTF-8"));
    }

    /**
     * データをファイルに書き出します。
     *
     * @param saveData 書き込むデータ
     * @throws IOException データ書き出し中にエラーが発生した場合にスローされます。
     */
    public void write(SaveData saveData) throws IOException {
        ZipEntry entry = new ZipEntry("main.properties");
        this.stream.putNextEntry(entry);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream, "UTF-8")) {
                String[] properties = saveData.getSortedProperties();
    
                for (String property : properties) {
                    outputStreamWriter.write(property);
                    outputStreamWriter.write(System.lineSeparator());
                }
            }

            byte[] propertiesBytes = byteArrayOutputStream.toByteArray();
            this.stream.write(propertiesBytes);
        }

        this.stream.closeEntry();

        Map<Integer, Image> images = saveData.getImages();

        for (Entry<Integer, Image> image : images.entrySet()) {
            entry = new ZipEntry(image.getKey() + ".png");

            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageFileWriter imageFileWriter = new ImageFileWriter(byteArrayOutputStream)) {
                imageFileWriter.write(image.getValue());
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                this.stream.write(imageBytes);
            }

            this.stream.closeEntry();
        }
    }

    /**
     * ファイルをクローズします。
     */
    public void close() throws IOException {
        this.stream.close();
    }
}
