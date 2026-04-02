/**
 * 共通ロガークラス
 */
export class Logger {
  /**
   * 情報ログを出力します。
   * @param message メッセージ
   * @param context 追加コンテキスト
   */
  public static info(message: string, context?: any): void {
    console.log(`[INFO] ${message}`, context ? JSON.stringify(context, null, 2) : '');
  }

  /**
   * 警告ログを出力します。
   * @param message メッセージ
   * @param context 追加コンテキスト
   */
  public static warn(message: string, context?: any): void {
    console.warn(`[WARN] ${message}`, context ? JSON.stringify(context, null, 2) : '');
  }

  /**
   * エラーログを出力します。
   * @param message メッセージ
   * @param error エラーオブジェクト
   */
  public static error(message: string, error?: any): void {
    console.error(`[ERROR] ${message}`, error);
  }

  /**
   * デバッグログを出力します。
   * @param message メッセージ
   * @param context 追加コンテキスト
   */
  public static debug(message: string, context?: any): void {
    if (process.env.DEBUG === 'true') {
      console.debug(`[DEBUG] ${message}`, context ? JSON.stringify(context, null, 2) : '');
    }
  }
}
