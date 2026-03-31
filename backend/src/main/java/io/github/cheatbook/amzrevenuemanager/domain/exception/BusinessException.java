package io.github.cheatbook.amzrevenuemanager.domain.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 業務例外の基底クラス
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
