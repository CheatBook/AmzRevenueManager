/**
 * Amazon収益管理アプリケーションのメインパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Bootアプリケーションのエントリーポイントです。
 * <p>
 * {@code @SpringBootApplication} アノテーションにより、
 * このクラスがSpring Bootアプリケーションのメインクラスであることを示します。
 * {@code @EnableCaching} アノテーションにより、キャッシュ機能を有効にします。
 * </p>
 */
@SpringBootApplication
@EnableCaching
public class AmzRevenueManagerApplication {

	/**
	 * アプリケーションを起動するメインメソッドです。
	 *
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		// Spring Bootアプリケーションを起動します。
		SpringApplication.run(AmzRevenueManagerApplication.class, args);
	}

}
