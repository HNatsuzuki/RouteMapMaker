package RouteMapMaker.models;

import java.text.ParseException;

/**
 * 破線パターンを表すクラスです。
 */
public class LineDash {
    /** 実線 */
    public static final LineDash SOLID = new LineDash(null);
    private double[] lineDashPattern;

    public LineDash(double[] pattern){
        lineDashPattern = pattern;
    }

    public double[] get(){
        return lineDashPattern;
    }

    /**
     * 文字列から破線パターンに変換します。
     *
     * @param patternText 破線パターンを表す文字列
     * @return 破線パターン
     * @throws ParseException 文字列の解析に失敗した場合にスローされます。
     */
    public static LineDash parse(String patternText) throws ParseException {
        String[] splitted = patternText.split(",");

        if (splitted.length < 2) {
            throw new ParseException("破線パターンを表す要素数は2つ以上の必要があります。", 0);
        }

        double[] pattern = new double[splitted.length];

        for (int i = 0; i < splitted.length; ++i) {
            try {
                pattern[i] = Double.parseDouble(splitted[i]);

                if (pattern[i] < 1) {
                    throw new ParseException("要素として使用できるのは1以上の半角数字のみです。", i);
                }
            } catch (NumberFormatException e) {
                throw new ParseException("不適切な文字が使用されています。使用できるのは1以上の半角数字と区切りカンマのみです。", i);
            }
        }

        return new LineDash(pattern);
    }
}
