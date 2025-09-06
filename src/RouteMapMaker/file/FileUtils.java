package RouteMapMaker.file;

import java.io.File;

/**
 * ファイルに関するユーティリティメソッド群です。
 */
public final class FileUtils {
    /**
     * 拡張子を除いたファイル名を取得します。
     *
     * @param fileName ファイル名
     * @return 拡張子を除いたファイル名
     * @throws IllegalArgumentException 引数が null の場合にスローされます。
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName が null です。");
        }

        File file = new File(fileName);

        return getFileNameWithoutExtension(file);
    }

    /**
     * 拡張子を除いたファイル名を取得します。
     *
     * @param file ファイル
     * @return 拡張子を除いたファイル名
     * @throws IllegalArgumentException 引数が null の場合にスローされます。
     */
    public static String getFileNameWithoutExtension(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file が null です。");
        }

        String fileName = file.getName();

        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    /**
     * ファイルの拡張子を取得します。
     *
     * @param fileName ファイル名
     * @return 拡張子 (見つからなかった場合は空文字)
     * @throws IllegalArgumentException 引数が null の場合にスローされます。
     */
    public static String getExtension(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName が null です。");
        }

        File file = new File(fileName);

        return getExtension(file);
    }

    /**
     * ファイルの拡張子を取得します。
     *
     * @param file ファイル
     * @return 拡張子 (見つからなかった場合は空文字)
     * @throws IllegalArgumentException 引数が null の場合にスローされます。
     */
    public static String getExtension(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file が null です。");
        }

        String fileName = file.getName();

        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            return "";
        } else {
            return fileName.substring(index);
        }
    }
}
