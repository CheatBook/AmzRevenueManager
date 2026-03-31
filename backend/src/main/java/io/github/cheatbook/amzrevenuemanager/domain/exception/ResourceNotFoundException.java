package io.github.cheatbook.amzrevenuemanager.domain.exception;

import org.springframework.http.HttpStatus;

/**
 * リソースが見つからない場合の例外
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
