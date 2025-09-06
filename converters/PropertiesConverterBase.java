package RouteMapMaker.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PropertiesConverterBase {
    protected List<String> errorMessages = new ArrayList<>();

    /**
     * エラー有無を取得します。
     *
     * @return エラーがある場合 true
     */
    public boolean hasError() {
        return errorMessages.size() > 0;
    }

    /**
     * エラーメッセージを取得します。
     *
     * @return エラーメッセージ
     */
    public List<String> getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }
}
