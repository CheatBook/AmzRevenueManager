package io.github.cheatbook.amzrevenuemanager.interfaces.web;

import io.github.cheatbook.amzrevenuemanager.application.service.ReportApplicationService;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.TransactionDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-data")
@RequiredArgsConstructor
public class TransactionDataController {

    private final ReportApplicationService reportApplicationService;

    @GetMapping
    public List<TransactionDataDto> getTransactionData() {
        return reportApplicationService.getTransactionData();
    }
}