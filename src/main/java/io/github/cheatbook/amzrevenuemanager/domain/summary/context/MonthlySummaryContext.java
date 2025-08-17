/**
 * ドメイン層のサマリーコンテキスト関連のクラスを定義するパッケージです。
 */
package io.github.cheatbook.amzrevenuemanager.domain.summary.context;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Advertisement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Purchase;
import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SkuName;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.ParentSkuMonthlySummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 月次サマリーの計算に必要なコンテキスト情報を保持するクラスです。
 */
@Getter
@Setter
public class MonthlySummaryContext {

    private final List<Settlement> settlements;
    private final List<SkuName> skuNames;
    private final List<Advertisement> advertisements;
    private final List<Purchase> purchases;

    private Map<String, String> skuToParentSkuMap;
    private Map<String, String> parentSkuToJapaneseNameMap;
    private Map<YearMonth, List<Settlement>> transactionsByMonth;
    private Map<String, ParentSkuMonthlySummaryDto.ParentSkuRevenueForMonthDto> parentSkuSummaryMap;
    private Map<String, List<String>> parentSkuToOrderIdsMap;

    /**
     * コンストラクタ
     *
     * @param settlements    決済リスト
     * @param skuNames       SKU名リスト
     * @param advertisements 広告リスト
     * @param purchases      仕入れリスト
     */
    public MonthlySummaryContext(List<Settlement> settlements, List<SkuName> skuNames, List<Advertisement> advertisements, List<Purchase> purchases) {
        this.settlements = settlements;
        this.skuNames = skuNames;
        this.advertisements = advertisements;
        this.purchases = purchases;
        this.parentSkuSummaryMap = new HashMap<>();
        this.parentSkuToOrderIdsMap = new HashMap<>();
    }
}