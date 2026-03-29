package io.github.cheatbook.amzrevenuemanager.domain.importer.reader;

import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

/**
 * 決済レポートのリーダーです。
 * <p>
 * AutoCloseableを実装しているため、try-with-resources文で使用できます。
 * </p>
 */
public class SettlementReportReader implements AutoCloseable {

    /**
     * CSVパーサー
     */
    private final CSVParser csvParser;

    /**
     * CSVイテレーター
     */
    private final Iterator<CSVRecord> csvIterator;

    /**
     * コンストラクタ
     *
     * @param file アップロードされた決済レポートファイル
     * @throws IOException ファイルの読み込みに失敗した場合
     */
    public SettlementReportReader(MultipartFile file) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), ReportConstants.CHARSET_SHIFT_JIS));
        CSVFormat format = CSVFormat.Builder.create(CSVFormat.TDF)
                .setHeader()
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();
        this.csvParser = new CSVParser(reader, format);
        this.csvIterator = this.csvParser.iterator();
    }

    /**
     * CSVイテレーターを取得します。
     *
     * @return CSVイテレーター
     */
    public Iterator<CSVRecord> getCsvIterator() {
        return csvIterator;
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