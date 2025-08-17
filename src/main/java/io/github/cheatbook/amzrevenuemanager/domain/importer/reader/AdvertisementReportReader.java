package io.github.cheatbook.amzrevenuemanager.domain.importer.reader;

import io.github.cheatbook.amzrevenuemanager.domain.constant.ReportConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdvertisementReportReader implements AutoCloseable {

    private final CSVParser csvParser;

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

    public CSVParser getCsvParser() {
        return csvParser;
    }

    @Override
    public void close() throws IOException {
        if (csvParser != null && !csvParser.isClosed()) {
            csvParser.close();
        }
    }
}