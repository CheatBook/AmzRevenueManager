package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryWithParentSkuDto {
    private LocalDate date;
    private List<ParentSkuRevenueForDailyDto> parentSkuSummaries;
    private ParentSkuRevenueForDailyDto dailyTotal;
}