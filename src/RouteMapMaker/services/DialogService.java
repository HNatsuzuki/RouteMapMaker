package RouteMapMaker.services;

import java.util.Optional;

 /**
  * ダイアログ表示を行うインターフェイスです。
  */
public interface DialogService<TInitialValue, TResult> {
    /**
     * ダイアログを表示します。
     *
     * @return 実行結果 (キャンセルされた場合、Optional.empty())
     */
    public Optional<TResult> showDialog();

    /**
     * ダイアログを表示します。
     *
     * @param value 初期値
     * @return 実行結果 (キャンセルされた場合、Optional.empty())
     */
    default public Optional<TResult> showDialog(TInitialValue value) {
      throw new UnsupportedOperationException("このダイアログには初期値を設定できません。");
    }
}
