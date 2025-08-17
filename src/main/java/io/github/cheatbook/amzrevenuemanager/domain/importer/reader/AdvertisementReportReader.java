/**
 * ドメイン層のインポーターのリーダー関連のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.importer.reader;

import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 広告レポートのリーダーです。
 * <p>
 * AutoCloseableを実装しているため、try-with-resources文で使用できます。
 * </p>
 */
public class AdvertisementReportReader implements AutoCloseable {

    /**
     * CSVパーサー
     */
    private final CSVParser csvParser;

    /**
     * コンストラクタ
     *
     * @param file アップロードされた広告レポートファイル
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public AdvertisementReportReader(MultipartFile file) throws IOException {
        BOMInputStream bomIn = new BOMInputStream(file.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(bomIn, ReportConstants.CHARSET_UTF8));

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        this.csvParser = new CSVParser(reader, format);
    }

    /**
     * CSVパーサーを取得します。
     *
     * @return CSVパーサー
     */
    public CSVParser getCsvParser() {
        return csvParser;
    }

    /**
     * リソースをクローズします。
     *
     * @throws IOException クローズに失敗した場合
     */
    @Override
    public void close() throws IOException {
        if (csvParser != null && !csvParser.isClosed()) {
            csvParser.close();
        }
    }
}