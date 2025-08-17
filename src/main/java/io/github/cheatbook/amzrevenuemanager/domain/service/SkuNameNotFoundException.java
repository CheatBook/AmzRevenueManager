package io.github.cheatbook.amzrevenuemanager.domain.service;

/**
 * SKU名が見つからない場合にスローされる例外です。
 */
public class SkuNameNotFoundException extends Exception {
    /**
     * コンストラクタ
     *
     * @param message 例外メッセージ
     */
    public SkuNameNotFoundException(String message) {
        super(message);
    }
}