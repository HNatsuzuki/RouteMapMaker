package RouteMapMaker.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.image.Image;

/**
 * ERM ファイル書き出し用クラスです。
 */
public class ErmFileWriter implements AutoCloseable {
    private final File file;
    private final OutputStream stream;

    /**
     * コンストラクタ
     *
     * @param file 書き込むファイル
     * @throws FileNotFoundException 読み込むファイルが存在しない場合にスローされます。
     */
    public ErmFileWriter(File file) throws FileNotFoundException {
        this.file = file;
        this.stream = new FileOutputStream(file);
    }

    /**
     * データをファイルに書き出します。
     *
     * @param saveData 書き込むデータ
     * @throws IOException データ書き出し中にエラーが発生した場合にスローされます。
     */
    public void write(SaveData saveData) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.stream, "UTF-8"))) {
            String[] properties = saveData.getSortedProperties();

            for (String property : properties) {
                bufferedWriter.append(property);
                bufferedWriter.newLine();
            }
        }

        //画像の書き出し
        String imageFileDir = this.file.getParent();
        Map<Integer, Image> images = saveData.getImages();

        for (Entry<Integer, Image> image : images.entrySet()) {
            File imageFile = new File(imageFileDir, image.getKey() + ".png");//番号+".png"
            
            try (ImageFileWriter imageFileWriter = new ImageFileWriter(imageFile)) {
                imageFileWriter.write(image.getValue());
            }
        }
    }

    /**
     * ファイルをクローズします。
     */
    public void close() throws IOException {
        this.stream.close();
    }
}
