package RouteMapMaker.services;

import java.util.Optional;

 /**
  * ダイアログ表示を行うインターフェイスです。
  */
public interface DialogService<T> {
    /**
     * ダイアログを表示します。
     *
     * @return 実行結果 (キャンセルされた場合、Optional.empty())
     */
    public Optional<T> showDialog();
}
