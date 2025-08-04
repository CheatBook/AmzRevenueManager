package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentSkuDailySummaryDto {
    private String parentSku;
    private String parentSkuName;
    private List<DailyRevenueSummaryDto> dailySummaries;
}