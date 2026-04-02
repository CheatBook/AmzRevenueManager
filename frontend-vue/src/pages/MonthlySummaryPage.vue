<script setup lang="ts">
import { ref, onMounted } from "vue";
import { get } from "aws-amplify/api";

interface ParentSkuRevenueForMonth {
  parentSku: string;
  parentSkuJapaneseName: string;
  totalSales: number;
  totalFees: number;
  totalAdCost: number;
  grossProfit: number;
  orderCount: number;
  productCost: number;
}

interface ParentSkuMonthlySummary {
  year: number;
  month: number;
  parentSkuRevenues: ParentSkuRevenueForMonth[];
  monthlyTotal: ParentSkuRevenueForMonth;
}

const summary = ref<ParentSkuMonthlySummary[]>([]);
const isLoading = ref(false);

onMounted(async () => {
  isLoading.value = true;
  try {
    const restOperation = get({
      apiName: "AmzRevenueApi",
      path: "/monthly-summary",
    });
    const response = await restOperation.response;
    const body = (await response.body.json()) as any;
    
    // 配列であること、および各要素の構造をチェック
    if (Array.isArray(body)) {
      summary.value = body;
    } else {
      console.error("予期しないデータ形式を受信しました:", body);
    }
  } catch (error) {
    console.error("月次サマリー取得エラー:", error);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div>
    <h1>月次親SKU別サマリー</h1>
    <div v-if="isLoading">読み込み中...</div>
    <div v-else-if="summary.length === 0">データがありません。</div>
    <div v-else class="table-container">
      <table>
        <thead>
          <tr>
            <th>年月</th>
            <th>親SKU</th>
            <th>総売上</th>
            <th>手数料・その他費用</th>
            <th>広告費</th>
            <th>商品代金</th>
            <th>粗利益</th>
            <th>注文数</th>
          </tr>
        </thead>
        <tbody v-for="(monthlyData, index) in summary" :key="index">
          <template v-if="monthlyData && monthlyData.parentSkuRevenues">
            <tr
              v-for="(item, subIndex) in monthlyData.parentSkuRevenues"
              :key="`${index}-${subIndex}`"
            >
              <td
                v-if="subIndex === 0"
                data-label="年月"
                :rowspan="monthlyData.parentSkuRevenues.length + (monthlyData.monthlyTotal ? 1 : 0)"
              >
                {{ monthlyData.year }}/{{ monthlyData.month }}
              </td>
              <td data-label="親SKU">
                {{ item.parentSkuJapaneseName || item.parentSku }}
              </td>
              <td data-label="総売上">
                {{ (item.totalSales || 0).toLocaleString() }}
              </td>
              <td data-label="手数料・その他費用">
                {{ (item.totalFees || 0).toLocaleString() }}
              </td>
              <td data-label="広告費">
                {{ (item.totalAdCost || 0).toLocaleString() }}
              </td>
              <td data-label="商品代金">
                {{ (item.productCost || 0).toLocaleString() }}
              </td>
              <td data-label="粗利益">
                {{ (item.grossProfit || 0).toLocaleString() }}
              </td>
              <td data-label="注文数">
                {{ (item.orderCount || 0).toLocaleString() }}
              </td>
            </tr>
            <tr v-if="monthlyData.monthlyTotal" style="font-weight: bold">
              <td data-label="親SKU">合計</td>
              <td data-label="総売上">
                {{ (monthlyData.monthlyTotal.totalSales || 0).toLocaleString() }}
              </td>
              <td data-label="手数料・その他費用">
                {{ (monthlyData.monthlyTotal.totalFees || 0).toLocaleString() }}
              </td>
              <td data-label="広告費">
                {{ (monthlyData.monthlyTotal.totalAdCost || 0).toLocaleString() }}
              </td>
              <td data-label="商品代金">
                {{ (monthlyData.monthlyTotal.productCost || 0).toLocaleString() }}
              </td>
              <td data-label="粗利益">
                {{ (monthlyData.monthlyTotal.grossProfit || 0).toLocaleString() }}
              </td>
              <td data-label="注文数">
                {{ (monthlyData.monthlyTotal.orderCount || 0).toLocaleString() }}
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
