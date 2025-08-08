package io.github.cheatbook.amzrevenuemanager.domain.service;

public class DuplicateSettlementIdException extends Exception {
    public DuplicateSettlementIdException(String message) {
        super(message);
    }
}