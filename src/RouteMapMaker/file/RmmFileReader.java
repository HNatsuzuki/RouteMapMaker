package RouteMapMaker.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        while ((entry = stream.getNextEntry()) != null ) {
            String entryFileName = entry.getName();
            if (entryFileName.equals("main.properties")) {
                //mainの読み込み
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[1024];
                    int size = 0;

                    while ((size = stream.read(buf)) > 0) {
                        outputStream.write(buf, 0, size);
                    }

                    byte[] propertiesBytes = outputStream.toByteArray();

                    try (InputStreamReader inputStream = new InputStreamReader(new ByteArrayInputStream(propertiesBytes), "UTF-8")) {
                        properties.load(inputStream);
                    }
                }
            } else if (FileUtils.getExtension(entryFileName).equals(".png")) {//画像
                try {
                    int index = Integer.valueOf(FileUtils.getFileNameWithoutExtension(entryFileName));
    
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        byte[] buf = new byte[1024];
                        int size = 0;

                        while ((size = stream.read(buf)) > 0) {
                            outputStream.write(buf, 0, size);
                        }

                        byte[] imageBytes = outputStream.toByteArray();

                        try (ImageFileReader imageFileReader = new ImageFileReader(new ByteArrayInputStream(imageBytes))) {
                            Image image = imageFileReader.read();
                            images.put(index, image);
                        }
                    }
                } catch (NumberFormatException e) {
                    // ファイル名が数字でない場合は読み込み処理を行わない．
                    System.out.println("skipped reading: " + entryFileName);
                }
            }

            stream.closeEntry();
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
