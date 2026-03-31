package io.github.cheatbook.amzrevenuemanager.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * 重複した決済IDが検出された場合にスローされる例外です。
 */
public class DuplicateSettlementIdException extends BusinessException {
    public DuplicateSettlementIdException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
