package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalSkuRevenueSummaryDto {
    private SkuRevenueSummaryDto parentSummary;
    private List<SkuRevenueSummaryDto> childrenSummaries;
}