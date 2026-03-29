package io.github.cheatbook.amzrevenuemanager.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.cheatbook.amzrevenuemanager.domain.entity.Settlement;
import io.github.cheatbook.amzrevenuemanager.domain.entity.SettlementId;
import io.github.cheatbook.amzrevenuemanager.domain.summary.ParentSkuSummaryData;
import io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.TransactionDataDto;
 
 /**
  * 決済リポジトリです。
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, SettlementId> {
    /**
     * 重複しないSKUのリストを取得します。
     *
     * @return SKUのリスト
     */
    @Query("SELECT DISTINCT t.sku FROM Settlement t")
    List<String> findDistinctSkus();

    /**
     * 重複しない月のリストを取得します。
     *
     * @return 月のリスト
     */
    @Query("SELECT DISTINCT CAST(SUBSTRING(CAST(s.postedDateTime AS string), 1, 7) || '-01T00:00:00' AS LocalDateTime) FROM Settlement s")
    List<LocalDateTime> findDistinctMonths();

    /**
     * 指定された期間内の決済を取得します。
     *
     * @param start 開始日時
     * @param end   終了日時
     * @return 決済のリスト
     */
    List<Settlement> findByPostedDateTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 指定された決済IDが存在するかどうかを確認します。
     *
     * @param settlementId 決済ID
     * @return 存在する場合はtrue、そうでない場合はfalse
     */
    boolean existsBySettlementId(String settlementId);

    /**
     * 親SKUごとのサマリーデータを取得します。
     *
     * @return 親SKUサマリーデータのリスト
     */
    @Query("SELECT new io.github.cheatbook.amzrevenuemanager.domain.summary.ParentSkuSummaryData(" +
           "COALESCE(sn.parentSku, s.sku), " +
           "SUM(CASE WHEN s.amountDescription = 'Principal' THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription IN ('Commission', 'FBAPerUnitFulfillmentFee', 'ShippingChargeback', 'RefundCommission') THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription = 'Shipping' THEN s.amount ELSE 0 END), " +
           "SUM(CASE WHEN s.amountDescription IN ('Tax', 'ShippingTax') THEN s.amount ELSE 0 END), " +
           "SUM(s.quantityPurchased)) " +
           "FROM Settlement s LEFT JOIN SkuName sn ON s.sku = sn.sku " +
           "GROUP BY COALESCE(sn.parentSku, s.sku)")
    List<ParentSkuSummaryData> findParentSkuSummaryData();

   /**
    * トランザクションデータを取得します。
    *
    * @return トランザクションデータのリスト
    */
   @Query("SELECT new io.github.cheatbook.amzrevenuemanager.interfaces.web.dto.TransactionDataDto(" +
          "s.settlementId, " +
          "s.sku, " +
          "s.amountDescription, " +
          "s.quantityPurchased, " +
          "sd.purchaseDate, " +
          "s.postedDateTime, " +
          "s.amount) " +
          "FROM Settlement s LEFT JOIN SalesDate sd ON s.amazonOrderId = sd.amazonOrderId")
   List<TransactionDataDto> findTransactionData();
}