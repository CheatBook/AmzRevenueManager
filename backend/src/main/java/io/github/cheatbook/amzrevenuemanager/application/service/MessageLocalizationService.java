package io.github.cheatbook.amzrevenuemanager.application.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * 多言語対応メッセージを取得するためのサービスです。
 */
@Service
@RequiredArgsConstructor
public class MessageLocalizationService {

    private final MessageSource messageSource;

    /**
     * メッセージキーに対応するローカライズされたメッセージを取得します。
     *
     * @param key メッセージキー
     * @return ローカライズされたメッセージ
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * メッセージキーと引数に対応するローカライズされたメッセージを取得します。
     *
     * @param key メッセージキー
     * @param args メッセージ引数
     * @return ローカライズされたメッセージ
     */
    public String getMessage(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}
