package io.github.cheatbook.amzrevenuemanager.infrastructure.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * 国際化（i18n）に関する設定クラスです。
 */
@Configuration
public class I18nConfig {

    /**
     * ロケールを解決するためのリゾルバーを定義します。
     * Accept-Language ヘッダーに基づいてロケールを決定します。
     * デフォルトは日本語に設定します。
     *
     * @return ロケールリゾルバー
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.JAPANESE);
        return resolver;
    }
}
