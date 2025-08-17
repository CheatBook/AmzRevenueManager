/**
 * インターフェース層（Web）のDTOクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.interfaces.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 階層的なSKU収益サマリーDTOです。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HierarchicalSkuRevenueSummaryDto {
    /**
     * 親サマリー
     */
    private SkuRevenueSummaryDto parentSummary;

    /**
     * 子サマリーのリスト
     */
    private List<SkuRevenueSummaryDto> childrenSummaries;
}