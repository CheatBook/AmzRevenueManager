package io.github.cheatbook.amzrevenuemanager.domain.service;

/**
 * 重複した決済IDが検出された場合にスローされる例外です。
 */
public class DuplicateSettlementIdException extends Exception {
    /**
     * コンストラクタ
     *
     * @param message 例外メッセージ
     */
    public DuplicateSettlementIdException(String message) {
        super(message);
    }
}