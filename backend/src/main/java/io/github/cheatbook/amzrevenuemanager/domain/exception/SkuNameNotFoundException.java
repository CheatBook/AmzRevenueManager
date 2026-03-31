package io.github.cheatbook.amzrevenuemanager.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * SKU名が見つからない場合にスローされる例外です。
 */
public class SkuNameNotFoundException extends BusinessException {
    public SkuNameNotFoundException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
